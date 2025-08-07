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
        maven("https://central.sonatype.com/repository/maven-snapshots/")
    }
}

include(":composeApp", ":composeApp:sekret", "composeTV")
include(":tmdb")
include(":ui")
include(":core")
include(":extension")
include(":firebase")
include(":rive")
include(":kache")