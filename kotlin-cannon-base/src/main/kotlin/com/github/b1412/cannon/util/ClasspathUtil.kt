package com.github.b1412.cannon.util

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.util.ClassUtils


fun findClasses(target: Class<*>, pattern: String): List<Class<*>> {
    val resourcePatternResolver = PathMatchingResourcePatternResolver()
    val metadataReaderFactory = CachingMetadataReaderFactory(resourcePatternResolver)
    val resources = resourcePatternResolver.getResources(pattern)
    return resources.map {
        metadataReaderFactory.getMetadataReader(it).classMetadata.className
    }.map {
        ClassUtils.forName(it, Thread.currentThread().contextClassLoader)
    }.filter {
        target.isAssignableFrom(it) && it != target
    }
}
