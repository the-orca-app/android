package com.jeanbarrossilva.orca.external

import org.gradle.api.JavaVersion

object Versions {
    const val ACCOMPANIST = "0.31.3-beta"
    const val ACTIVITY = "1.7.2"
    const val APPCOMPAT = "1.6.1"
    const val BROWSER = "1.5.0"
    const val COIL = "2.4.0"
    const val COMPOSE_COMPILER = "1.4.7"
    const val COMPOSE_MATERIAL_3 = "1.1.0"
    const val COMPOSE = "1.5.1"
    const val CONSTRAINTLAYOUT = "2.1.4"
    const val CORE = "1.10.1"
    const val COROUTINES = "1.7.1"
    const val FRAGMENT = "1.6.0"
    const val GRADLE = "8.1.1"
    const val JUNIT = "4.13.2"
    const val KOIN = "3.4.1"
    const val KOIN_ANDROID = "3.4.2"
    const val KOTLIN = "1.8.21"
    const val KSP = "1.8.21-1.0.11"
    const val KTOR = "2.3.2"
    const val LIFECYCLE = "2.6.1"
    const val LOADABLE = "1.6.9"
    const val MATERIAL = "1.9.0"
    const val MOCKK = "1.13.7"
    const val NAVIGATION = "2.6.0"
    const val PAGINATE = "0.3.0"
    const val ROOM = "2.5.2"
    const val SECRETS = "2.0.1"
    const val SERIALIZATION = "1.5.1"
    const val SLF4J = "2.0.7"
    const val TEST_CORE = "1.5.0"
    const val TEST_ESPRESSO = "3.5.0"
    const val TEST_RUNNER = "1.5.0"
    const val TIME4A = "4.8-2021a"
    const val VIEWMODEL = "2.6.1"

    val java = JavaVersion.toVersion(System.getProperty("JAVA_VERSION"))

    object Orca {
        const val CODE = 1
        const val NAME = "1.0.0"
        const val SDK_COMPILE = 34
        const val SDK_MIN = 28
        const val SDK_TARGET = SDK_COMPILE
    }
}