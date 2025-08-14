plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.sekret)
    alias(libs.plugins.stacktrace.decoroutinator) apply false
}

ktorfit {
    kotlinVersion.set("-")
}

kotlin {
    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()

    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()

    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()

    js(IR) {
        browser()
        nodejs()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.sekret)
            implementation(libs.tooling)
            implementation(libs.tooling.wanakana)
            implementation(libs.ktor)
            implementation(libs.serialization)
            implementation(libs.serialization.json)
            implementation(libs.ktorfit)
            implementation(libs.datetime)
            implementation(libs.kermit)
            implementation(project(":core"))
            implementation(project(":kache"))

            api(libs.immutable)
            api(libs.paging)
        }

        commonTest.dependencies {
            implementation(libs.test)
            implementation(libs.coroutines.test)
            implementation(libs.ktor.test)

            // implementation(libs.paging.test)

            implementation(libs.assertk)
            implementation(libs.assertk.coroutines)
        }

        jvmTest.dependencies {
            implementation(libs.mockk)
        }
    }
}