plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(libs.lifecycle)
            implementation(libs.navigation)

            implementation(libs.kermit)
            implementation(libs.tooling.compose)

            implementation(libs.tv.foundation)
            implementation(libs.tv.material)

            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)
            implementation(libs.coil.compose)
            implementation(libs.splashscreen)

            implementation(project(":ui"))
        }
    }
}

android {
    compileSdk = 36
    namespace = "dev.datlag.mimasu.tv"

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