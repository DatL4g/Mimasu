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

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.sekret)
            implementation(libs.tooling)
            implementation(libs.kache)
            implementation(libs.ktor)
            implementation(libs.serialization)

            api(libs.immutable)
            api(libs.paging)
        }
    }
}