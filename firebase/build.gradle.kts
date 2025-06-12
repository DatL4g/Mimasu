plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.sekret)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
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
            implementation(libs.ktorfit)
            implementation(libs.kermit)

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
            api(libs.android.firebase.performance)
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