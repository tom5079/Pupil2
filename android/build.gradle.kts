plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "xyz.quaver.pupil"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("com.arkivanov.decompose:extensions-compose-jetpack:${rootProject.extra["decompose.version"]}")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.30.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
}

android {
    namespace = "xyz.quaver.pupil.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "xyz.quaver.pupil.android"
        minSdk = 24
        targetSdk = 33
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