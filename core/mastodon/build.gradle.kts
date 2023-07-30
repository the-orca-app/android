import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization") version Versions.KOTLIN
    id("com.android.library")
    id("com.google.devtools.ksp") version Versions.KSP
    id("org.jetbrains.kotlin.android")

    @Suppress("SpellCheckingInspection")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Versions.java.toString()
    }
}

android {
    namespace = Metadata.namespace("core.mastodon")
    compileSdk = Versions.Mastodonte.SDK_TARGET

    defaultConfig {
        minSdk = Versions.Mastodonte.SDK_MIN
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    kotlinOptions {
        jvmTarget = Versions.java.toString()
    }

    secrets {
        defaultPropertiesFileName = "public.properties"
        ignoreList += "^(?!mastodon\\.clientSecret).*$"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }
}

dependencies {
    api(Dependencies.PAGINATE)
    api(Dependencies.ROOM)
    api(Dependencies.STORE)

    implementation(project(":core"))
    implementation(project(":platform:theme"))
    implementation(project(":platform:ui"))
    implementation(Dependencies.BROWSER)
    implementation(Dependencies.COMPOSE_MATERIAL_ICONS_EXTENDED)
    implementation(Dependencies.KOIN_ANDROID)
    implementation(Dependencies.KTOR_CIO)
    implementation(Dependencies.KTOR_CONTENT_NEGOTIATION)
    implementation(Dependencies.KTOR_CORE)
    implementation(Dependencies.KTOR_SERIALIZATION_KOTLINX_JSON)
    implementation(Dependencies.VIEWMODEL)

    ksp(Plugins.ROOM)
}