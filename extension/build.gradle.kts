import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.serialization)
    alias(libs.plugins.atomicfu)
}

kotlin {
    androidTarget()
    jvm()
    
    js(IR) {
        browser()
        binaries.executable()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    /*@OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }*/

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.stdlib)
            implementation(libs.coroutines)
            implementation(libs.serialization)
            implementation(libs.serialization.protobuf)
            implementation(libs.kase.change)
            implementation(libs.tooling)
            implementation(libs.android.annotation)
            implementation(libs.kermit)

            implementation(project(":core"))
        }
        androidMain.dependencies {
            implementation(libs.android.startup)
        }
        jsMain.dependencies {
            runtimeOnly(npm("webextension-polyfill", "0.12.0"))
        }
    }
}

android {
    compileSdk = 36
    namespace = "dev.datlag.mimasu.extension"
    (sourceSets.findByName("main") ?: sourceSets.findByName("androidMain"))?.aidl?.srcDir("src/androidMain/aidl")

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        aidl = true
    }
}