plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)

    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.coroutines)
            api(libs.paging)
        }

        androidMain.dependencies {
            implementation(libs.paging.compose)
        }
    }
}

android {
    compileSdk = 35
    namespace = "dev.datlag.mimasu.ui"

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
}