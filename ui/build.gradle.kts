plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // macosX64() // not supported by kodein compose
    // macosArm64() // not supported by kodein compose

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.coroutines)
            implementation(libs.serialization)
            implementation(libs.tooling)
            implementation(project(":core"))

            api(project(":tmdb"))
            api(libs.kodein)
            api(libs.kodein.compose)
            api(libs.viewmodel)
            api(libs.paging)
        }

        androidMain.dependencies {
            implementation(libs.paging.compose)
        }

        val pagingCommonMain by creating {
            dependsOn(commonMain.get())

            jvmMain.orNull?.dependsOn(this)
            nativeMain.orNull?.dependsOn(this)
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
        targetCompatibility = JavaVersion.VERSION_21
    }
}