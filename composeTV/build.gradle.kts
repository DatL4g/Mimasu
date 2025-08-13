plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.report)
}

val artifact = "dev.datlag.mimasu.tv"
group = artifact

kotlin {
    androidTarget()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.components.resources)
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
            implementation(libs.placeholder)
            implementation(libs.tolgee)

            implementation(libs.bundles.android.media)

            implementation(project(":extension"))
            implementation(project(":core"))
            implementation(project(":ui"))
        }
    }
}

compose {
    resources {
        generateResClass = auto
        packageOfResClass = artifact
    }
}

android {
    compileSdk = 36
    namespace = artifact

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