rootProject.name = "Mimasu"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":composeApp", ":composeApp:sekret")
include(":tmdb")
include(":ui")
include(":core")
include(":extension")
include(":firebase")