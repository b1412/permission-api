package com.github.leon.generator.metadata


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldFeature(
        val sortable: Boolean = false,
        val searchable: Boolean = false,
        val boolean: Boolean = true,
        val readonly: Boolean = false,
        val switch: Boolean = false,
        val range: Boolean = false,
        val selectOne: Boolean = false,
        val selectMany: Boolean = false,
        val addDynamicMany: Boolean = false,
        /**
         * 列表头
         */
        val label: String = "",
        /**
         * 不在表单显示
         */
        val hiddenInForm: Boolean = false,

        /**
         * 不在子表单显示
         */
        val hiddenInSubForm: Boolean = false,
        /**
         * 不在列表显示
         */
        val hiddenInList: Boolean = false,
        /**
         * 字符显示长度，剩下的用...省略
         */
        val limit: Int = 255,
        val textarea: Boolean = false,
        val rows: Int = 10,
        val cols: Int = 200,
        val richText: Boolean = false,
        /**
         * 列表中管理实体显示的字段
         */
        val display: String = "",
        /**
         * 字段显示顺序
         */
        val order: Int = 0,
        /**
         * Form中字段宽度 out of 12
         */
        val weight: Int = 6,
        val embeddedEntity: Boolean = false


)


