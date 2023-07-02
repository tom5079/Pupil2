plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    id("kotlin-parcelize")
    kotlin("android")
}

group = "xyz.quaver.pupil"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app:common"))
    implementation(AndroidSpecific.AndroidX.ACTIVITY_COMPOSE)
    implementation(AndroidSpecific.Accompanist.DRAWABLE_PAINTER)
    implementation(AndroidSpecific.Accompanist.SYSTEM_UI_CONTROLLER)
}

android {
    namespace = "xyz.quaver.pupil.android"
    compileSdk = AndroidConfig.COMPILE_SDK
    defaultConfig {
        applicationId = "xyz.quaver.pupil.android"
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}