plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.luisgarcializ.ensenyas"
    compileSdk = 35 // Mantén 35

    defaultConfig {
        applicationId = "com.luisgarcializ.ensenyas"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // Para Compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Asegúrate de que esta versión coincida con tu Kotlin Gradle Plugin (1.9.0)
    }
}

dependencies {

    // ANDROIDX CORE & LIFECYCLE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // COMPOSE BOM (Bill Of Materials) para gestionar versiones consistentes de Compose
    // Usa la versión más reciente que sea compatible con tu compileSdk y kotlinCompilerExtensionVersion
    // Por ejemplo, para compileSdk 35 y Kotlin 1.9.0, puedes usar Compose BOM 2024.04.00
    implementation(platform("androidx.compose:compose-bom:2024.04.00")) // <<-- ACTUALIZADO Y CENTRALIZADO

    // COMPOSE UI & MATERIAL DESIGN
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview) // Incluye AndroidView para preview
    implementation(libs.androidx.material3) // Una sola declaración de material3
    implementation(libs.androidx.material.icons.extended) // Para más iconos

    // COMPOSE LIVEDATA INTEGRATION
    implementation(libs.androidx.runtime.livedata) // Usa solo esta, la otra es duplicada/antigua

    // FIREBASE BOM
    implementation(platform(libs.firebase.bom))

    // FIREBASE SDKs
    implementation(libs.firebase.auth.ktx)
    implementation(libs.google.firebase.database.ktx)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.firebase.analytics.ktx) // Opcional (analítica)

    // COROUTINES PARA TAREAS DE FIREBASE
    implementation(libs.kotlinx.coroutines.play.services)

    // NAVIGATION COMPOSE
    implementation(libs.androidx.navigation.compose)

    // EXOPLAYER (MEDIA3)
    // Asegúrate de que estas versiones sean las últimas estables y compatibles con tu compileSdk
    implementation("androidx.media3:media3-exoplayer:1.3.1") // Módulo principal
    implementation("androidx.media3:media3-ui:1.3.1")       // Módulo de UI (contiene PlayerView y las clases para interop con Compose)
    implementation("androidx.media3:media3-common:1.3.1")   // Módulo común (si lo necesitas explícitamente, ya suele ser transitivo)
    implementation("androidx.media3:media3-exoplayer-dash:1.3.1") // Para DASH, si lo usas

    // IMAGE LOADING LIBRARY (Coil)
    implementation("io.coil-kt:coil-compose:2.6.0") // O la última versión estable

    // TESTS
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.00")) // MISMA VERSIÓN DEL BOM
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}