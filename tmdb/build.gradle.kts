plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.sekret)
    alias(libs.plugins.stacktrace.decoroutinator) apply false
}

kotlin {
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()

    js(IR) {
        browser()
        nodejs()
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.sekret)
            implementation(libs.serialization)
            implementation(libs.serialization.json)
            implementation(libs.tooling)
            implementation(libs.kache)
            implementation(libs.napier)
            api(libs.immutable)
            api(libs.ktorfit)
            api(libs.paging)

            implementation(project(":core"))
        }

        val jvmMain by getting {
            // apply(plugin = libs.plugins.stacktrace.decoroutinator.get().pluginId)
        }
    }
}