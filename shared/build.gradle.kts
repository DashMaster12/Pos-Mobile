import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Plugins necesarios para Multiplatform, Android, Compose y el Compilador de Compose
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.0"
}

kotlin {
    // 1. CONFIGURACIÓN DEL TARGET ANDROID
    // Esto le says a Kotlin que genere código compatible con la JVM de Android
    androidTarget {
        compilerOptions {
            // Establecemos Java 11 como objetivo (estándar actual para Android)
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // 2. CONFIGURACIÓN DE TARGETS iOS
    // Definimos soporte para iPhones reales (Arm64) y simuladores (SimulatorArm64)
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared" // Nombre de la librería que usará Xcode
            isStatic = true     // Recomendado para Compose Multiplatform
        }
    }

    // 3. FUENTES DE CÓDIGO Y DEPENDENCIAS (SourceSets)
    sourceSets {
        commonMain.dependencies {
            // Dependencias de UI (Compose Multiplatform)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            // Dependencias de ciclo de vida y ViewModels compartidos
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Ktor Client y Serialization
            implementation("io.ktor:ktor-client-core:3.0.3")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
            implementation("io.ktor:ktor-client-logging:3.0.3")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

            // Coil 3 para Carga de Imágenes en Compose Multiplatform
            implementation("io.coil-kt.coil3:coil-compose:3.0.4")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.4")
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.compose.uiToolingPreview)
            implementation("io.ktor:ktor-client-okhttp:3.0.3")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// 4. CONFIGURACIÓN ESPECÍFICA DE ANDROID (Fuera del bloque Kotlin)
android {
    namespace = "pe.edu.upeu.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
}

// 5. DEPENDENCIAS DE HERRAMIENTAS
dependencies {
    debugImplementation(libs.compose.uiTooling)
}
