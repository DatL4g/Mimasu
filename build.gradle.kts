plugins {
    alias(libs.plugins.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.atomicfu) apply false
    alias(libs.plugins.cocoapods) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.report) apply false
    alias(libs.plugins.compose.reload) apply false
    alias(libs.plugins.konfig) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.osdetector) apply false
    alias(libs.plugins.tolgee) apply false
    alias(libs.plugins.sekret) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.stacktrace.decoroutinator) apply false
    alias(libs.plugins.versions)
}

// Force new atomicfu version, compose uses 0.23.2
allprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlinx" && requested.name.startsWith("atomicfu")) {
                useVersion(libs.versions.atomicfu.get())
            }
        }
    }
}