rootProject.name = "Mimasu"

include(":composeApp", ":composeApp:tv")
include(":tmdb")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://jogamp.org/deployment/maven")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}