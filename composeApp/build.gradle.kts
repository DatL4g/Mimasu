import kotlin.math.min

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.report)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.osdetector)
    alias(libs.plugins.serialization)
}

htmlComposeCompilerReport {
    outputDirectory.set(rootProject.layout.buildDirectory.asFile)
}

kotlin {
    androidTarget()
    jvm()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.kodein)
            implementation(libs.kodein.compose)

            implementation(libs.haze)
            implementation(libs.haze.materials)

            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.tooling.decompose)
            implementation(libs.locale)
            implementation(libs.napier)

            implementation(libs.windowsize)
            implementation(libs.ktor)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.datetime)

            implementation(libs.kmpalette)
            implementation(libs.kolor)

            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)
            implementation(libs.coil.compose)

            implementation(libs.serialization.json)
            implementation(libs.serialization.protobuf)

            implementation(libs.ktor.jvm)
            implementation(project(":tmdb"))
            implementation(project("tv"))
        }

        val androidMain by getting {
            apply(plugin = "kotlin-parcelize")

            dependencies {
                implementation(libs.android)
                implementation(libs.activity)
                implementation(libs.activity.compose)
                implementation(libs.multidex)
                implementation(libs.ackpine)

                implementation(libs.ktor.jvm)
                implementation(libs.coroutines.android)

                implementation(libs.splashscreen)
            }
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.ktor.jvm)
            implementation(libs.coroutines.swing)
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

android {
    sourceSets["main"].setRoot("src/androidMain/")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    sourceSets["main"].assets.srcDirs("src/androidMain/assets", "src/commonMain/assets")

    compileSdk = 34
    namespace = "dev.datlag.mimasu"

    defaultConfig {
        applicationId = "dev.datlag.mimasu"
        minSdk = 21
        targetSdk = 34
        versionCode = 100
        versionName = "1.0.0"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
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

composeCompiler {
    enableStrongSkippingMode.set(true)
    enableNonSkippingGroupOptimization.set(true)
}

compose {
    desktop {
        application {
            mainClass = "dev.datlag.mimasu.MainKt"
        }
    }
}