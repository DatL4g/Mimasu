plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.serialization)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.sekret)
}

val artifact = "dev.datlag.mimasu.ui"
group = artifact

kotlin {
    androidTarget()
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // macosX64() // not supported by kodein compose and firebase
    // macosArm64() // not supported by kodein compose and firebase

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.animationGraphics)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(libs.fonticons)
            implementation(libs.tooling.compose)

            implementation("org.jetbrains.compose.ui:ui-backhandler:${libs.versions.compose.asProvider().get()}")

            implementation(libs.coroutines)
            implementation(libs.serialization)
            implementation(libs.tooling)
            implementation(libs.collection)
            implementation(libs.kermit)
            implementation(libs.ktor)

            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)
            implementation(libs.coil.compose)

            implementation(project(":core"))

            api(project(":tmdb"))
            api(project(":firebase"))
            api(libs.kodein)
            api(libs.kodein.compose)
            api(libs.viewmodel)
            api(libs.paging)
        }

        androidMain.dependencies {
            api(libs.paging.compose)
            implementation(libs.android.startup)

            implementation(project(":extension"))
            implementation(project(":rive"))
        }

        val pagingCommonMain by creating {
            dependsOn(commonMain.get())

            jvmMain.orNull?.dependsOn(this)
            nativeMain.orNull?.dependsOn(this)
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

compose {
    resources {
        generateResClass = auto
        packageOfResClass = artifact
        publicResClass = true
        nameOfResClass = "UiRes"
    }
}

android {
    compileSdk = 36
    namespace = "dev.datlag.mimasu.ui"

    defaultConfig {
        minSdk = 21

        multiDexEnabled = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
}