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

    js(IR) {
        browser()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.animationGraphics)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(libs.fonticons)
            implementation(libs.tooling.compose)
            implementation(libs.tooling.country)
            implementation(libs.navigation)

            implementation(libs.material3)
            implementation("org.jetbrains.compose.ui:ui-backhandler:${libs.versions.compose.asProvider().get()}")

            implementation(libs.coroutines)
            implementation(libs.serialization)
            implementation(libs.tooling)
            implementation(libs.collection)
            implementation(libs.kermit)
            implementation(libs.ktor)
            implementation(libs.tolgee)

            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)
            implementation(libs.coil.compose)

            implementation(project(":core"))

            api(project(":extension"))
            api(project(":tmdb"))
            api(project(":firebase"))
            api(libs.kodein)
            api(libs.kodein.compose)
            api(libs.viewmodel)
            api(libs.paging)
            api(libs.paging.compose)
        }

        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(libs.android.startup)
            implementation(libs.cronet)
            implementation(libs.review)

            implementation(libs.bundles.android.cast)
            implementation(libs.bundles.android.media)

            implementation(project(":rive"))
        }

        jsMain.dependencies {
            implementation(project(":rive"))
        }

        val skikoMain by creating {
            dependsOn(commonMain.get())

            jvmMain.orNull?.dependsOn(this)
            nativeMain.orNull?.dependsOn(this)
            jsMain.orNull?.dependsOn(this)
            wasmJsMain.orNull?.dependsOn(this)
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