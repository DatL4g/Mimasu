import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.report)
    alias(libs.plugins.konfig)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.osdetector)
    alias(libs.plugins.sekret)
    alias(libs.plugins.serialization)
}

htmlComposeCompilerReport {
    outputDirectory.set(rootProject.layout.buildDirectory.asFile)
}

val artifact = "dev.datlag.mimasu"

buildkonfig {
    packageName = artifact

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "packageName", artifact)
    }
}

sekret {
    properties {
        enabled.set(true)
        packageName.set(artifact)

        nativeCopy {
            androidJNIFolder.set(project.layout.projectDirectory.dir("src/androidMain/jniLibs"))
            desktopComposeResourcesFolder.set(project.layout.projectDirectory.dir("src/jvmMain/resources"))
        }
    }
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.paging.compose)

            implementation(libs.kodein)
            implementation(libs.kodein.compose)

            implementation(libs.haze)
            implementation(libs.haze.materials)

            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.tooling.decompose)
            implementation(libs.locale)
            implementation(libs.napier)

            implementation(libs.windowsize)
            implementation(libs.ktor)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.datetime)

            implementation(libs.kmpalette)
            implementation(libs.kolor)

            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)
            implementation(libs.coil.compose)

            implementation(libs.serialization.json)
            implementation(libs.serialization.protobuf)

            implementation(project(":tmdb"))
            implementation(project(":firebase"))
            implementation(project("tv"))
        }

        val androidMain by getting {
            apply(plugin = "kotlin-parcelize")

            dependencies {
                implementation(libs.android)
                implementation(libs.activity)
                implementation(libs.activity.compose)
                implementation(libs.multidex)
                implementation(libs.ackpine)

                implementation(libs.ktor.jvm)
                implementation(libs.coroutines.android)

                implementation(libs.splashscreen)
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

android {
    sourceSets["main"].setRoot("src/androidMain/")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    sourceSets["main"].assets.srcDirs("src/androidMain/assets", "src/commonMain/assets")

    compileSdk = 34
    namespace = "dev.datlag.mimasu"

    defaultConfig {
        applicationId = "dev.datlag.mimasu"
        minSdk = 21
        targetSdk = 34
        versionCode = 100
        versionName = "1.0.0"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

composeCompiler {
    enableStrongSkippingMode.set(true)
    enableNonSkippingGroupOptimization.set(true)
}

compose {
    desktop {
        application {
            mainClass = "dev.datlag.mimasu.MainKt"

            when (getHost()) {
                Host.Linux -> {
                    jvmArgs("--add-opens", "java.desktop/sun.awt.X11=ALL-UNNAMED")
                    jvmArgs("--add-opens", "java.desktop/sun.awt.wl=ALL-UNNAMED")
                }
                else -> { }
            }

            nativeDistributions {
                packageName = "Mimasu"
                packageVersion = "1.0.0"

                appResourcesRootDir.set(project.layout.projectDirectory.dir("src/jvmMain/resources"))
            }
        }
    }
}

fun getHost(): Host {
    return when (osdetector.os) {
        "linux" -> Host.Linux
        "osx" -> Host.MAC
        "windows" -> Host.Windows
        else -> {
            val hostOs = System.getProperty("os.name")
            val isMingwX64 = hostOs.startsWith("Windows")

            when {
                hostOs == "Linux" -> Host.Linux
                hostOs == "Mac OS X" -> Host.MAC
                isMingwX64 -> Host.Windows
                else -> throw IllegalStateException("Unknown OS: ${osdetector.classifier}")
            }
        }
    }
}

enum class Host(val label: String) {
    Linux("linux"),
    Windows("win"),
    MAC("mac");
}