plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.application")
}

object Constants {
    const val packageName = "manatoki.net"
    const val applicationIdSuffix = "manatoki"
    const val sources = "manatoki.net:.Manatoki"
    const val versionCode = 1
    const val versionName = "0.0.1-alpha01"
}

kotlin {
    android()
    jvm("desktop") {
        jvmToolchain(11)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)

                implementation(KtorClient.CORE)
                implementation(KtorClient.CIO)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
    }
}

android {
    namespace = "xyz.quaver.pupil.source"
    compileSdk = AndroidConfig.COMPILE_SDK
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    defaultConfig {
        applicationIdSuffix = Constants.applicationIdSuffix
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = Constants.versionCode
        versionName = Constants.versionName

        manifestPlaceholders.apply {
            put("sourceName", "[Pupil] ${Constants.packageName}")
            put("sources", Constants.sources)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<Jar> {
    manifest {
        attributes("Source-Name" to Constants.packageName)
        attributes("Source-Version" to Constants.versionName)
    }
}

tasks.register("installDesktop") {
    dependsOn(tasks.findByName("build"))
    doLast {
        copy {
            from("build/libs") {
                include("manatoki-desktop.jar")
            }
            into("${System.getProperty("user.home")}/.pupil/")
        }
    }
}
