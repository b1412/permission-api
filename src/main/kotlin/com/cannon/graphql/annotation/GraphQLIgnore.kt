package com.cannon.graphql.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FIELD)
@Retention(RUNTIME)
annotation class GraphQLIgnore
