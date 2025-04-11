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
    }
}

android {
    compileSdk = 35
    namespace = "dev.datlag.mimasu.rive"

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
}