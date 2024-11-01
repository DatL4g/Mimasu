plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.stacktrace.decoroutinator) apply false
}

kotlin {
    androidTarget()
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.materialIconsExtended)
            implementation(libs.immutable)
            implementation(libs.serialization)
            implementation(libs.coil.compose)
            implementation(libs.paging.compose)

            implementation(project(":tmdb"))
        }

        val androidMain by getting {
            apply(plugin = "kotlin-parcelize")
            // apply(plugin = libs.plugins.stacktrace.decoroutinator.get().pluginId)

            dependencies {
                implementation(libs.tv.foundation)
                implementation(libs.tv.lifecycle)
                implementation(libs.tv.material)
                implementation(libs.tooling)
                api(libs.android.autofill)
            }
        }

        val jvmMain by getting {
            // apply(plugin = libs.plugins.stacktrace.decoroutinator.get().pluginId)
        }

        val nonAndroidMain by creating {
            dependsOn(commonMain.get())

            jvmMain.dependsOn(this)
            appleMain.orNull?.dependsOn(this)
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

android {
    compileSdk = 35
    namespace = "dev.datlag.mimasu.tv"

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}