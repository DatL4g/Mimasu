import com.android.build.gradle.internal.dsl.NdkOptions
import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import dev.datlag.tooling.existsSafely
import dev.datlag.tooling.scopeCatching
import dev.datlag.tooling.systemEnv
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.serialization)
    alias(libs.plugins.konfig)
    alias(libs.plugins.sekret)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.about)
    alias(libs.plugins.stacktrace.decoroutinator)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.performance)
    alias(libs.plugins.compose.report)
}

val artifact = "dev.datlag.mimasu"
val ADMOB_ANDROID_TESTING = "ca-app-pub-3940256099942544~3347511713"
group = artifact

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

kotlin {
    jvmToolchain(21)
    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    js {
        outputModuleName.set("composeApp.js")
        browser()
        binaries.executable()
    }

    /*
    wasmJs {
        browser()
        binaries.executable()
    }*/

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(compose.animationGraphics)

            implementation(libs.material3)
            implementation("org.jetbrains.compose.ui:ui-backhandler:${libs.versions.compose.asProvider().get()}")

            implementation(libs.lifecycle)
            implementation(libs.navigation)
            implementation(libs.adaptive)
            implementation(libs.adaptive.layout)
            implementation(libs.adaptive.navigation)

            implementation(libs.coroutines)
            implementation(libs.datetime)
            implementation(libs.ktor)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.tooling.compose)
            implementation(libs.coil)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)
            implementation(libs.coil.compose)
            implementation(libs.kermit)
            implementation(libs.placeholder)
            implementation(libs.placeholder.material3)
            implementation(libs.tolgee)
            implementation(libs.haze)
            implementation(libs.haze.materials)
            implementation(libs.about)

            implementation(project(":extension"))
            implementation(project(":core"))
            implementation(project(":ui"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.coroutines.android)
            implementation(libs.android)
            implementation(libs.activity)
            implementation(libs.activity.compose)
            implementation(libs.multidex)
            implementation(libs.ktor.jvm)
            implementation(libs.cronet)
            implementation(libs.cronet.okhttp)
            implementation(libs.google.fonts)
            implementation(libs.android.startup)
            implementation(libs.splashscreen)
            implementation(libs.certificate.transparency.android)
            implementation(libs.bundles.android.ads)
            implementation(libs.kermit.crashlytics)
            implementation(libs.youtube.player)
            compileOnly(libs.tv.material)

            implementation(libs.bundles.android.cast)
            implementation(libs.bundles.android.media)

            implementation(project(":composeTV"))
        }

        jsMain.dependencies {
            implementation(libs.ktor.js)
            implementation(libs.coroutines.js)
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

    namespace = "dev.datlag.mimasu"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        targetSdk = 36

        applicationId = "dev.datlag.mimasu"
        versionCode = appVersionCode
        versionName = appVersion

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
    androidResources {
        generateLocaleConfig = false
    }
    signingConfigs {
        rootProject.layout.projectDirectory.file("keystore.jks").asFile.takeIf {
            it.existsSafely()
        }?.let {
            maybeCreate("release").apply {
                storeFile = it
                storePassword = systemEnv("KEYSTORE_PASSWORD")
                keyAlias = systemEnv("KEY_ALIAS")
                keyPassword = systemEnv("KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders["admob_app_id"] = getAdmobAppId() ?: ADMOB_ANDROID_TESTING
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("src/androidMain/proguard-rules.pro")
            )
            ndk {
                debugSymbolLevel = NdkOptions.DebugSymbolLevel.SYMBOL_TABLE.name
            }
            configure<CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
            }
        }
        debug {
            manifestPlaceholders["admob_app_id"] = ADMOB_ANDROID_TESTING
            ndk {
                debugSymbolLevel = NdkOptions.DebugSymbolLevel.FULL.name
            }
        }
    }
}

//https://developer.android.com/develop/ui/compose/testing#setup
/*dependencies {
    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)
}*/

compose {
    resources {
        generateResClass = auto
    }
    web { }
}

buildkonfig {
    packageName = artifact

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "packageName", artifact)
    }
}

sekret {
    properties {
        enabled.set(true)

        googleServicesFile.set(project.layout.projectDirectory.file("google-services.json"))
        yamlFile.set(project.layout.projectDirectory.file("sekret.yaml"))
    }
}

aboutLibraries {
    collect {
        includePlatform.set(true)
    }
    library {
        duplicationMode.set(DuplicateMode.MERGE)
        duplicationRule.set(DuplicateRule.GROUP)
    }
    export {
        excludeFields.set(setOf("generated"))
        prettyPrint.set(true)
        outputPath.set(project.layout.projectDirectory.file("src/commonMain/composeResources/files/aboutlibraries.json"))
    }
}

private fun getAdmobAppId(): String? {
    var propFile = rootProject.file("local.properties")
    if (!propFile.existsSafely()) {
        propFile = project.file("local.properties")
    }

    if (propFile.existsSafely()) {
        val props = Properties()

        scopeCatching {
            propFile.inputStream().use {
                props.load(it)
            }
        }.onSuccess {
            return props.getProperty("admob.app.id")?.ifBlank {
                null
            } ?: systemEnv("ADMOB_APP_ID")?.ifBlank { null }?.trim()
        }
    }

    return systemEnv("ADMOB_APP_ID")?.ifBlank { null }?.trim()
}