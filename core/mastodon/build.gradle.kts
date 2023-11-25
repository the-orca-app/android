plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.android.maps.secrets)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.kotlin.symbolProcessor)
}

android {
  buildFeatures.compose = true
  composeOptions.kotlinCompilerExtensionVersion = libs.versions.android.compose.get()
  defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

  buildFeatures {
    buildConfig = true
    compose = true
  }

  secrets {
    defaultPropertiesFileName = "public.properties"
    ignoreList += "^(?!(mastodon\\.clientSecret)|(instancesSocial\\.token)).*$"
  }

  packagingOptions.resources.excludes +=
    arrayOf("META-INF/LICENSE.md", "META-INF/LICENSE-notice.md")
}

dependencies {
  androidTestImplementation(project(":core-test"))
  androidTestImplementation(libs.android.compose.ui.test.junit)
  androidTestImplementation(libs.android.test.core)
  androidTestImplementation(libs.android.test.runner)
  androidTestImplementation(libs.assertk)
  androidTestImplementation(libs.ktor.client.mock)
  androidTestImplementation(libs.mockk)

  api(project(":core-module"))

  ksp(project(":std:injector-processor"))

  implementation(project(":core:sample"))
  implementation(project(":platform:autos"))
  implementation(project(":platform:cache"))
  implementation(project(":platform:ui"))
  implementation(libs.android.browser)
  implementation(libs.android.room.ktx)
  implementation(libs.ktor.client.cio)
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.contentNegotiation)
  implementation(libs.ktor.serialization.json)
  implementation(libs.paginate)

  ksp(libs.android.room.compiler)

  releaseImplementation(libs.slf4j) {
    because("Ktor references \"StaticLoggerBinder\" and it is missing on minification.")
  }

  testImplementation(project(":core:sample"))
  testImplementation(project(":core:sample-test"))
  testImplementation(project(":core-test"))
  testImplementation(libs.assertk)
  testImplementation(libs.junit)
  testImplementation(libs.kotlin.coroutines.test)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.ktor.client.mock)
  testImplementation(libs.turbine)
}
