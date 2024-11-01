plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.stacktrace.decoroutinator) apply false
}

kotlin {
    androidTarget()
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    macosX64()
    macosArm64()

    js(IR) {
        browser()
        nodejs()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines)
            implementation(libs.serialization)
            implementation(libs.datetime)
            api(libs.immutable)
        }

        val androidMain by getting {
            // apply(plugin = libs.plugins.stacktrace.decoroutinator.get().pluginId)
        }

        val jvmMain by getting {
            // apply(plugin = libs.plugins.stacktrace.decoroutinator.get().pluginId)
        }
    }
}

android {
    compileSdk = 35
    namespace = "dev.datlag.mimasu.core"
    sourceSets["main"].aidl.srcDirs("src/androidMain/aidl")

    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        aidl = true
    }
}