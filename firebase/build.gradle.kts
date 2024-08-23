plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.serialization)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.sekret)
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
            api(libs.firebase)
            api(libs.firebase.firestore)
            api(libs.firebase.auth)
            api(libs.firebase.config)
            implementation(libs.datetime)
            implementation(libs.napier)
            implementation(libs.sekret)

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
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
}