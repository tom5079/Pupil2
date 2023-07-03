pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("android").version(extra["kotlin.version"] as String)
        kotlin("plugin.serialization").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["agp.version"] as String)
        id("com.android.library").version(extra["agp.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("dev.icerock.mobile.multiplatform-resources").version(extra["moko.version"] as String)
    }
}

rootProject.name = "Pupil2"

include(":app:android", ":app:desktop", ":app:common", ":core")

file("sources").list()?.forEach { source ->
    include(":sources:$source")
}