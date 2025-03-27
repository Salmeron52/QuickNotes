// Importaciones necesarias para leer el archivo de propiedades
import java.util.Properties
import java.io.FileInputStream
import org.gradle.api.GradleException // Para lanzar un error si falta la clave


// --- INICIO: Código para leer local.properties ---
val localProperties = Properties()
// Accedemos al archivo local.properties en la raíz del proyecto
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists() && localPropertiesFile.isFile) {
    try {
        FileInputStream(localPropertiesFile).use { fis ->
            localProperties.load(fis)
        }
    } catch (e: Exception) {
        // Puedes imprimir un warning si prefieres no detener la compilación aquí
        project.logger.warn("Advertencia: No se pudo cargar local.properties: ${e.message}")
    }
} else {
    project.logger.warn("Advertencia: El archivo local.properties no existe en la raíz del proyecto.")
    // Podríamos lanzar una excepción aquí si consideramos que las claves son absolutamente necesarias
    // throw GradleException("El archivo local.properties no existe en la raíz del proyecto.
    // Creamos uno con admob.appId y admob.bannerAdUnitId.")
}

// Obtener las claves. Usamos ?: para proporcionar un valor por defecto o lanzar error
// Es MEJOR lanzar error para builds de release si las claves son obligatorias.
// Para debug, podemos poner IDs de prueba de AdMob si queremos.
val admobAppId: String = localProperties.getProperty("admob.appId")
    ?: throw GradleException("Define admob.appId en tu archivo local.properties") // Lanza error si no se encuentra
val admobBannerAdUnitId: String = localProperties.getProperty("admob.bannerAdUnitId")
    ?: throw GradleException("Define admob.bannerAdUnitId en tu archivo local.properties") // Lanza error si no se encuentra

// --- FIN: Código para leer local.properties ---

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.buenhijogames.quicknotes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.buenhijogames.quicknotes"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // --- INICIO: Inyección de claves ---
        // 1. Para AndroidManifest.xml
        manifestPlaceholders["admobAppId"] = admobAppId

        // 2. Para usar en código Kotlin (BuildConfig)
        // ¡Importante las comillas escapadas para que sea un String literal en el código generado!
        buildConfigField("String", "ADMOB_BANNER_AD_UNIT_ID", "\"$admobBannerAdUnitId\"")
        // --- FIN: Inyección de claves ---
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        // Habilitar buildConfig es necesario para usar buildConfigField
        buildConfig = true // Asegúrate de que esto esté en true
    }
    packaging { // Usa packaging en lugar de packagingOptions en versiones más nuevas de AGP
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //Room
    implementation(libs.androidx.room.runtime)
    /*kapt(libs.androidx.room.runtime)*/
    kapt("androidx.room:room-compiler:2.6.1")
    implementation(libs.androidx.room.ktx)

    //Admob
    implementation(libs.play.services.ads)
}