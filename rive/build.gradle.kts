plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

version = libs.versions.app.get()

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js(IR) {
        browser()
    }

    cocoapods {
        pod("RiveRuntime")
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)

            implementation(libs.serialization)
        }

        androidMain.dependencies {
            api(libs.rive.android)
        }

        jsMain.dependencies {
            implementation(npm("@rive-app/canvas", "2.31.1"))
        }
    }
}

android {
    compileSdk = 36
    namespace = "dev.datlag.mimasu.rive"

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
}