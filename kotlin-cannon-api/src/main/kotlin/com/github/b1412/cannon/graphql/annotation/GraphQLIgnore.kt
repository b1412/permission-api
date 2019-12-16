package com.github.b1412.cannon.graphql.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FIELD)
@Retention(RUNTIME)
annotation class GraphQLIgnore
