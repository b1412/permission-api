package com.github.leon.generator.entity


import arrow.core.getOrElse
import arrow.core.toOption
import com.cannon.entity.BaseEntity
import com.cannon.entity.User
import com.github.leon.classpath.ClassSearcher
import com.github.leon.generator.ext.Utils
import com.github.leon.generator.findClasses
import com.github.leon.generator.metadata.*
import org.hibernate.validator.constraints.Range
import org.joor.Reflect
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.persistence.Column
import javax.persistence.Id
import javax.validation.constraints.*

data class CodeEntity(


        var fields: List<CodeField> = listOf(),
        var id: Int? = null,
        var code: Int = 0,
        var name: String = "",

        var display: String? = null,
        var security: Boolean = true,
        var excelImport: Boolean = false,
        var excelExport: Boolean = false,
        var tree: Boolean = false,

        var permissions: List<CodePermission> = mutableListOf(),
        var menus: List<CodeMenu> = mutableListOf(),
        var css: String? = null,

        var embeddedEntity: MutableList<CodeEntity> = mutableListOf(),
        var embeddedList: MutableList<String> = mutableListOf(),
        var primaryKeyInList: Boolean = false,

        var action: CodeAction? = null,
        var toolbar: CodeToolbar? = null

)

fun String.remainLastIndexOf(s: String): String {
    var index = this.lastIndexOf(s)
    return this.substring(0, index++)
}

fun scanForCodeEnum(): List<CodeEnum> {
    return ClassSearcher.of(Enum::class.java).search<Enum<*>>().map {
        CodeEnum(
                name = it.name.remainLastIndexOf("."),
                value = (Reflect.on(it).call("values").get() as Array<*>)
                        .map { it.toString().remainLastIndexOf(".") }
                        .toList())
    }
}

val entityCode: MutableMap<String, Int> = mutableMapOf()

fun scanForCodeEntities(path: String): List<CodeEntity> {
    val classLoader = Thread.currentThread().contextClassLoader
    val inputStream = classLoader.getResourceAsStream("generator/local.properties")
    val appProps = Properties()
    appProps.load(inputStream)
    val projectId = appProps.getProperty("projectId").toInt().dec() * 200
    val entities = path.split(",").flatMap { findClasses(BaseEntity::class.java, it) }
    entities.mapIndexed { index, clazz -> entityCode.put(clazz.name, index + projectId) }
    return entities
            .asSequence()
            .filter {
                it.getDeclaredAnnotation(EntityFeature::class.java).toOption().map { it.generated }.getOrElse { true }
            }.map(::entityClass2CodeEntity)
            .toList()
}

fun entityClass2CodeEntity(clazz: Class<*>): CodeEntity {

    var codeEntity = CodeEntity(
            name = clazz.simpleName
    )

    codeEntity.permissions = clazz.getAnnotationsByType(PermissionFeature::class.java)
            .map { CodePermission(role = it.role, rule = it.rule, httpMethod = it.httpMethods) }


    codeEntity.toolbar = clazz.getAnnotation(ToolbarFeatures::class.java).toOption().map {
        CodeToolbar(
                showReset = it.showReset,
                showAdd = it.showAdd,
                showExcelImport = it.showExcelImport,
                showExcelExport = it.showExcelExport,
                toolbarItems = it.value.map { item ->
                    CodeToolbarItem(
                            display = item.display,
                            icon = item.icon,
                            target = item.target,
                            confirmMessage = item.confirmMessage,
                            confirmTitle = item.confirmTitle,
                            modalName = item.modalName,
                            modalNoteFieldName = item.modalNoteFieldName.split(",")
                                    .map { modalField ->
                                        CodeModalField(name = modalField, type = if (modalField == "tp") "boolean" else "String")
                                    },
                            modalPlaceholder = item.modalPlaceholder,
                            selectOneEntity = item.selectOneEntity,
                            selectOneDisplay = item.selectOneDisplay,
                            multiple = item.multiple,
                            httpMethod = item.httpMethod,
                            url = item.url,
                            permissionName = item.permissionName
                    )
                }
        )
    }.getOrElse {
        CodeToolbar()
    }
    codeEntity.action = clazz.getAnnotation(ActionFeatures::class.java).toOption().map {
        CodeAction(
                showDelete = it.showDelete,
                showEdit = it.showEdit,
                codeActionItems = it.value.map { item ->
                    CodeActionItem(
                            display = item.display,
                            icon = item.icon,
                            target = item.target,
                            confirmTitle = item.confirmTitle,
                            confirmMessage = item.confirmMessage,
                            modalName = item.modalName,
                            modalPlaceholder = item.modalPlaceholder,
                            modalNoteFieldName = item.modalNoteFieldName,
                            httpMethod = item.httpMethod,
                            url = item.url,
                            permissionName = item.permissionName
                    )
                }
        )
    }.getOrElse {
        CodeAction()
    }

    codeEntity.menus = clazz.getAnnotationsByType(MenuFeature::class.java)
            .map {
                val map: MutableMap<String, Any> = if (it.queryParams.isNotBlank()) {
                    it.queryParams.split(";").map {
                        val (l, r) = it.split("=")
                        Pair(l, r)
                    }.toMap().toMutableMap()
                } else {
                    mutableMapOf()
                }
                map["entity"] = codeEntity.name.decapitalize()
                map["columnValue"] = Utils.spacedCapital2LowerCamel(it.name)
                map["menuName"] = it.name
                val enhancedQueryParams = "{" + map.map { "${it.key}:'${it.value}'" }.joinToString(",") + "}"


                CodeMenu(parentName = it.parentName,
                        entity = codeEntity,
                        sort = it.sort,
                        name = if (it.name.isNotBlank()) {
                            it.name
                        } else {
                            codeEntity.name
                        },
                        actionItems = it.actionItems.toMutableList(),
                        toolbarItems = it.toolbarItems.toMutableList(),
                        queryParams = enhancedQueryParams)
            }


    val map = clazz.getAnnotationsByType(CssFeature::class.java)
            .map { cssFeature ->
                Pair(cssFeature.condition, cssFeature.cssClass)
            }.toMap()

    codeEntity.css = "{" + map.map { "'${it.value}': item.${it.key}" }.joinToString(",") + "}"

    val ignoredFields = listOf("serialVersionUID", "Companion")
    clazz.declaredAnnotations.forEach {
        when (it) {
            is EntityFeature -> {
                codeEntity = codeEntity.copy(
                        code = entityCode[clazz.name]!!,
                        security = it.security,
                        embeddedList = it.embeddedList.split(",").toMutableList(),
                        tree = it.tree,
                        excelImport = it.excelImport,
                        excelExport = it.excelExport,
                        primaryKeyInList = it.primaryKeyInList
                )
            }
            is ExcelFeature -> {
                codeEntity = codeEntity.copy(
                        excelExport = it.exportable,
                        excelImport = it.importable
                )
            }
        }
    }
    var allFields = clazz.declaredFields + clazz.superclass.declaredFields
    if (clazz.superclass == User::class.java) {
        allFields += clazz.superclass.superclass.declaredFields
    }
    val (rawFields, embeddedEntity) = allFields
            .filter { field: Field -> ignoredFields.all { ignoreField -> ignoreField != field.name } }
            .partition { field -> field.getAnnotation(FieldFeature::class.java).toOption().map { it.embeddedEntity.not() }.getOrElse { true } }
    val columns: MutableMap<String, MutableList<CodeColumn>> = mutableMapOf()


    var fields = rawFields.map { field ->
        var codeField = CodeField(
                name = field.name,
                label = field.getAnnotation(FieldFeature::class.java).toOption().map {
                    if (it.label.isNotBlank()) {
                        it.label
                    } else {
                        Utils.spacedCapital(field.name)
                    }
                }.getOrElse { Utils.spacedCapital(field.name) },

                type = when {
                    List::class.java.isAssignableFrom(field.type) ->
                        FieldType(name = "List",
                                element = (field.genericType as ParameterizedType).actualTypeArguments[0]
                                        .typeName.remainLastIndexOf("."))
                    BaseEntity::class.java.isAssignableFrom(field.type) ->
                        FieldType(name = "Entity", element = field.type.simpleName)
                    else -> FieldType(name = field.type.simpleName)
                }
        )
        field.declaredAnnotations.forEach { fieldAnnotation ->
            when (fieldAnnotation) {
                is NotNull -> {
                    codeField = codeField.copy(required = true)
                }
                is Size -> {
                    codeField = codeField.copy(sizeMin = fieldAnnotation.min, sizeMax = fieldAnnotation.max)
                }
                is Max -> {
                    codeField = codeField.copy(rangeMax = fieldAnnotation.value)
                }
                is Min -> {
                    codeField = codeField.copy(rangeMin = fieldAnnotation.value)
                }
                is Range -> {
                    codeField = codeField.copy(rangeMin = fieldAnnotation.min, rangeMax = fieldAnnotation.max)
                }
                is Pattern -> {
                    codeField = codeField.copy(pattern = fieldAnnotation.regexp)
                }
                is Future -> {
                    codeField = codeField.copy(future = true)
                }
                is Past -> {
                    codeField = codeField.copy(past = true)
                }
                is Column -> {
                    codeField = codeField.copy(
                            unique = fieldAnnotation.unique
                    )
                }
                is Id -> codeField = codeField.copy(primaryKey = true)
                is FieldFeature -> {
                    codeField = codeField.copy(
                            searchable = fieldAnnotation.searchable,
                            sortable = fieldAnnotation.sortable,
                            readonly = fieldAnnotation.readonly,
                            switch = fieldAnnotation.switch,
                            selectOne = fieldAnnotation.selectOne,
                            selectMany = fieldAnnotation.selectMany,
                            addDynamicMany = fieldAnnotation.addDynamicMany,
                            hiddenInForm = fieldAnnotation.hiddenInForm,
                            hiddenInSubForm = fieldAnnotation.hiddenInSubForm,
                            hiddenInList = fieldAnnotation.hiddenInList,
                            limit = fieldAnnotation.limit,
                            textarea = fieldAnnotation.textarea,
                            rows = fieldAnnotation.rows,
                            cols = fieldAnnotation.cols,
                            richText = fieldAnnotation.richText,
                            display = fieldAnnotation.display.split(",").filter { it.isNotEmpty() },
                            weight = fieldAnnotation.weight,
                            range = fieldAnnotation.range
                    )
                }
                is ExcelFeature -> {
                    codeField = codeField.copy(
                            column = if (fieldAnnotation.column.isNotBlank()) {
                                fieldAnnotation.column
                            } else {
                                field.name
                            },
                            header = if (fieldAnnotation.header.isNotBlank()) {
                                fieldAnnotation.header
                            } else {
                                field.name
                            },
                            exportable = fieldAnnotation.exportable,
                            importable = fieldAnnotation.importable
                    )
                }
                is AttachmentFeature -> {
                    codeField = codeField.copy(
                            attachment = true,
                            attachmentConfig = AttachmentConfig(
                                    maxFiles = fieldAnnotation.maxFiles
                            ))
                }
            }
        }
        val columnFeatures = field.getDeclaredAnnotationsByType(ColumnFeature::class.java)

        columnFeatures.forEach { columnFeature ->
            val list = columns.getOrDefault(columnFeature.menuName, mutableListOf())
            list.add(CodeColumn(
                    asc = columnFeature.isAsc,
                    active = columnFeature.isActive,
                    columnDisplay = if (columnFeature.columnDisplay.isNotBlank()) {
                        columnFeature.columnDisplay
                    } else {
                        Utils.spacedCapital(field.name)
                    },
                    columnModel = if (columnFeature.columnModel.isNotBlank()) {
                        columnFeature.columnModel
                    } else {
                        Utils.lowerCamel(field.name)
                    },
                    sortable = columnFeature.isSortable,
                    display = columnFeature.display,
                    hiddenInList = columnFeature.hiddenInList,
                    hiddenInForm = columnFeature.hiddenInForm
            ))
            columns[columnFeature.menuName] = list
        }
        val entityFeature = clazz.getAnnotation(EntityFeature::class.java)
        when (codeField.name) {
//            "createdAt" -> {
//                codeField = codeField.copy(hiddenInForm = !entityFeature.createdAtInForm)
//                codeField = codeField.copy(hiddenInList = !entityFeature.createdAtInList)
//            }
//            "creator" -> {
//                codeField = codeField.copy(hiddenInForm = !entityFeature.creatorInForm, display = listOf("name"))
//                codeField = codeField.copy(hiddenInList = !entityFeature.creatorInList)
//            }
//            "updatedAt" -> {
//                codeField = codeField.copy(hiddenInForm = !entityFeature.updatedAtInForm)
//                codeField = codeField.copy(hiddenInList = !entityFeature.updatedAtInList)
//            }
//            "modifier" -> {
//                codeField = codeField.copy(hiddenInForm = !entityFeature.modifierInForm, display = listOf("name"))
//                codeField = codeField.copy(hiddenInList = !entityFeature.modifierInList)
//            }
//            "user" -> {
//                codeField = codeField.copy(hiddenInForm = !entityFeature.userInForm)
//                codeField = codeField.copy(hiddenInList = !entityFeature.userInList)
//                codeField = codeField.copy(selectOne = true, display = listOf("name"))
//            }
//            "version" -> {
//                codeField = codeField.copy(hiddenInForm = !entityFeature.versionInForm)
//                codeField = codeField.copy(hiddenInList = !entityFeature.versionInList)
//            }
            else -> codeField
        }
        codeField

    }

    clazz.getAnnotation(UserColumns::class.java)?.value?.forEach { userColumn ->

        /*  val codeField = if ("id" == userColumn.name) {
              CodeField(
                      type = FieldType("Long"),
                      name = userColumn.name,
                      hiddenInList = true,
                      hiddenInForm = true
              )
          } else {
              CodeField(
                      type = FieldType("String"),
                      name = userColumn.name,
                      hiddenInList = false,
                      hiddenInForm = false
              )
          }
          fields += codeField*/
        userColumn.columnFeatures.forEach { columnFeature ->
            val list = columns.getOrDefault(columnFeature.menuName, mutableListOf())
            list.add(CodeColumn(
                    asc = columnFeature.isAsc,
                    active = columnFeature.isActive,
                    columnDisplay = if (columnFeature.columnDisplay.isNotBlank()) {
                        columnFeature.columnDisplay
                    } else {
                        Utils.spacedCapital(userColumn.name)
                    },
                    columnModel = if (columnFeature.columnModel.isNotBlank()) {
                        columnFeature.columnModel
                    } else {
                        Utils.lowerCamel(userColumn.name)
                    },
                    sortable = columnFeature.isSortable,
                    display = columnFeature.display,
                    hiddenInList = columnFeature.hiddenInList,
                    hiddenInForm = columnFeature.hiddenInForm
            ))
            columns[columnFeature.menuName] = list
        }
    }
    codeEntity.fields = fields
    codeEntity.embeddedEntity = embeddedEntity.map { entityClass2CodeEntity(it.type) }.toMutableList()
    codeEntity.menus.forEach {
        val v = columns[it.name]
        if (v != null) {
            it.columns.addAll(v)
        }
    }
    return codeEntity

}
