plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.serialization)
    alias(libs.plugins.cocoapods)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "firebase"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    cocoapods {
        version = "1.0.0"
        ios.deploymentTarget = "12.0"

        pod("GoogleSignIn")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.tooling)
            implementation(libs.coroutines)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.auth)

            implementation(project(":core"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.google.auth)
        }
    }

}

android {
    compileSdk = 34
    namespace = "dev.datlag.mimasu.firebase"

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
}