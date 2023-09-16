package com.jeanbarrossilva.orca.external

object Plugins {
    const val GRADLE = "com.android.tools.build:gradle:${Versions.GRADLE}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
    const val ROOM = "androidx.room:room-compiler:${Versions.ROOM}"
    const val SECRETS = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin" +
        ":secrets-gradle-plugin:${Versions.SECRETS}"
}