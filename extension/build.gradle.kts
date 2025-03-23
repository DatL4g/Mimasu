import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    /*@OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }*/

    sourceSets {
        commonMain.dependencies {
            implementation(libs.stdlib)
            implementation(project(":core"))

            runtimeOnly(npm("webextension-polyfill", "0.12.0"))
        }
    }
}