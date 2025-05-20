plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.sekret)
    alias(libs.plugins.atomicfu)
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
            implementation(libs.tooling)
            implementation(libs.coroutines)
            implementation(libs.sekret)
            implementation(libs.kase.change)
            implementation(libs.immutable)

            api(libs.firebase)
            api(libs.firebase.firestore)
            api(libs.firebase.auth)
            api(libs.firebase.config)

            implementation(project(":core"))
        }

        commonTest.dependencies {
            implementation(libs.test)
            implementation(libs.assertk)
        }

        androidMain.dependencies {
            implementation(libs.bundles.google.auth)
            api(project.dependencies.platform(libs.android.firebase))
            api(libs.android.firebase.analytics)
            api(libs.android.firebase.crashlytics)
        }
    }
}

android {
    compileSdk = 35
    namespace = "dev.datlag.mimasu.firebase"

    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
}