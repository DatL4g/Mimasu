plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
        }

        val androidMain by getting {
            apply(plugin = "kotlin-parcelize")

            dependencies {
                implementation(libs.tv.foundation)
                implementation(libs.tv.lifecycle)
                implementation(libs.tv.material)
                implementation(libs.tooling)
            }
        }

        val nonAndroidMain by creating {
            dependsOn(commonMain.get())

            jvmMain.orNull?.dependsOn(this)
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

android {
    compileSdk = 34
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