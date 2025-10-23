plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    jvm()
    js(IR) {
        browser()
        nodejs()
    }

    iosArm64 {
        binaries {
            sharedLib()
        }
    }

    iosSimulatorArm64 {
        binaries {
            sharedLib()
        }
    }

    iosX64 {
        binaries {
            sharedLib()
        }
    }

    androidNativeX86 {
        binaries {
            sharedLib()
        }
    }

    androidNativeX64 {
        binaries {
            sharedLib {
                linkerOpts += listOf(
                    "-Wl,-z,max-page-size=16384",
                    "-Wl,-z,common-page-size=16384",
                    "-v"
                )
            }
        }
    }

    androidNativeArm32 {
        binaries {
            sharedLib()
        }
    }

    androidNativeArm64 {
        binaries {
            sharedLib()
        }
    }

    linuxX64 {
        binaries {
            sharedLib()
        }
    }

    linuxArm64 {
        binaries {
            sharedLib()
        }
    }

    macosX64 {
        binaries {
            sharedLib()
        }
    }

    macosArm64 {
        binaries {
            sharedLib()
        }
    }

    mingwX64 {
        binaries {
            sharedLib()
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(libs.sekret)
        }

        val jniNativeMain by creating {
            nativeMain.orNull?.let { dependsOn(it) } ?: dependsOn(commonMain.get())
            androidNativeMain.orNull?.dependsOn(this)
            linuxMain.orNull?.dependsOn(this)
            mingwMain.orNull?.dependsOn(this)
            macosMain.orNull?.dependsOn(this)
        }

        val jniMain by creating {
            dependsOn(commonMain.get())
            androidMain.orNull?.dependsOn(this)
            jvmMain.orNull?.dependsOn(this)
        }
    }
}
android {
    compileSdk = 36
    namespace = "dev.datlag.mimasu.sekret"

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_21
    }
}
