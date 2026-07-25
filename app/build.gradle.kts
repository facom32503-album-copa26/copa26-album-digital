import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Lê segredos/config de local.properties (fora do controle de versão).
val localProperties: Properties = Properties().apply {
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) propsFile.inputStream().use { load(it) }
}
val footballApiToken: String = localProperties.getProperty("FOOTBALL_API_TOKEN", "")
// API privada de fotos: mantemos football-data.org como fonte de dados e servimos
// apenas as imagens por conta própria, protegidas por uma chave privada.
// 10.0.2.2 = localhost da máquina host visto de dentro do emulador Android.
val photoApiBaseUrl: String =
    localProperties.getProperty("PHOTO_API_BASE_URL", "http://10.0.2.2:3000/")
val photoApiKey: String =
    localProperties.getProperty("PHOTO_API_KEY", "copa26-dev-key")

android {
    namespace = "com.example.copa26_album_digital"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.copa26_album_digital"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Injeta o token no BuildConfig — nunca hardcoded no código-fonte.
        buildConfigField("String", "FOOTBALL_API_TOKEN", "\"$footballApiToken\"")
        // API privada de fotos (base + chave de auth).
        buildConfigField("String", "PHOTO_API_BASE_URL", "\"$photoApiBaseUrl\"")
        buildConfigField("String", "PHOTO_API_KEY", "\"$photoApiKey\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}