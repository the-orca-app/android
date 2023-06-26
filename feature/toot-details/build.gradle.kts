plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    @Suppress("SpellCheckingInspection")
    namespace = Metadata.namespace("feature.tootdetails")

    compileSdk = Versions.Mastodonte.SDK_COMPILE

    defaultConfig {
        minSdk = Versions.Mastodonte.SDK_MIN
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            @Suppress("UnstableApiUsage")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    @Suppress("UnstableApiUsage")
    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    kotlinOptions {
        jvmTarget = Versions.java.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }
}

dependencies {
    debugImplementation(project(":core:in-memory"))

    implementation(project(":core"))
    implementation(project(":platform:theme"))
    implementation(project(":platform:ui"))
    implementation(Dependencies.COMPOSE_MATERIAL_ICONS_EXTENDED)
    implementation(Dependencies.LIFECYCLE_VIEWMODEL)
    implementation(Dependencies.LOADABLE_LIST)
    implementation(Dependencies.LOADABLE_PLACEHOLDER)
}