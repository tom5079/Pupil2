plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("kotlin-parcelize")
}

group = "xyz.quaver.pupil"
version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop") {
        jvmToolchain(11)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.materialIconsExtended)

                api("org.kodein.di:kodein-di:7.19.0")
                api("org.kodein.di:kodein-di-framework-compose:7.19.0")

                api("com.arkivanov.decompose:decompose:${extra["decompose.version"]}")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:${extra["decompose.version"]}")

                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

                implementation("io.ktor:ktor-client-core:${extra["ktor.version"]}")
                implementation("io.ktor:ktor-client-cio:${extra["ktor.version"]}")
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
                api("androidx.window:window:1.1.0")
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
    namespace = "xyz.quaver.pupil.common"
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}