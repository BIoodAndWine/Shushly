import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget()
    
    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
                
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.coroutines)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.cio)
            }
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

android {
    namespace = "com.shushly.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.shushly.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.desktop {
    application {
        mainClass = "com.shushly.app.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "Shushly"
            packageVersion = "1.0.0"
            description = "Shushly - One-Click DNS Ad Blocker"
            copyright = "Copyright 2026 Shushly"
            vendor = "Shushly"

            windows {
                menuGroup = "Shushly"
                upgradeUuid = "8b51d8d9-29db-4ff9-8f08-251c72f10b80"
            }
        }
    }
}
