package com.github.leon.generator

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.util.ClassUtils


fun findClasses(target: Class<*>, pattern: String): List<Class<*>> {
    val resourcePatternResolver = PathMatchingResourcePatternResolver()
    val metadataReaderFactory = CachingMetadataReaderFactory(resourcePatternResolver)
    val resources = resourcePatternResolver.getResources(pattern)
    return resources.map {
        ClassUtils.forName(metadataReaderFactory.getMetadataReader(it).classMetadata.className, Thread.currentThread().contextClassLoader)
    }.filter {
        target.isAssignableFrom(it) && it != target
    }
}