object AndroidConfig {
    const val COMPILE_SDK = 33
    const val MIN_SDK = 21
    const val TARGET_SDK = 33
}

object Versions {
    const val KOTLIN = "1.8.10"
    const val COROUTINE = "1.7.2"
    const val ACCOMPANIST = "0.30.1"
    const val KTOR_CLIENT = "2.3.2"
    const val DECOMPOSE = "2.0.0"
    const val MOKO = "0.23.0"
}

object Kotlin {
    const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"
    const val SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1"
    const val COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}"
    const val COROUTINE_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINE}"
    const val DATETIME = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
}

object AndroidSpecific {
    object Accompanist {
        const val FLOW_LAYOUT = "com.google.accompanist:accompanist-flowlayout:${Versions.ACCOMPANIST}"
        const val APPCOMPAT_THEME = "com.google.accompanist:accompanist-appcompat-theme:${Versions.ACCOMPANIST}"
        const val INSETS = "com.google.accompanist:accompanist-insets:${Versions.ACCOMPANIST}"
        const val INSETS_UI = "com.google.accompanist:accompanist-insets-ui:${Versions.ACCOMPANIST}"
        const val DRAWABLE_PAINTER = "com.google.accompanist:accompanist-drawablepainter:${Versions.ACCOMPANIST}"
        const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:${Versions.ACCOMPANIST}"
    }

    object AndroidX {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.4.1"
        const val CORE_KTX = "androidx.core:core-ktx:1.7.0"
        const val NAVIGATION_COMPOSE = "androidx.navigation:navigation-compose:2.4.1"
        const val DATASTORE = "androidx.datastore:datastore:1.0.0"
        const val DATASTORE_PREFERENCES = "androidx.datastore:datastore-preferences:1.0.0"
        const val LIFECYCLE_LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:2.4.1"
        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.7.2"
    }

    object Kotlin {
        const val COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINE}"
    }

    object AndroidTest {
        const val CORE = "androidx.test:core:1.4.0"
        const val JUNIT = "androidx.test.ext:junit:1.1.3"
        const val RULES = "androidx.test:rules:1.4.0"
        const val RUNNER = "androidx.test:runner:1.4.0"
        const val ESPRESSO = "androidx.test.espresso:espresso-core:3.4.0"
    }
}

object Kodein {
    const val DI = "org.kodein.di:kodein-di-framework-compose:7.19.1"
    const val LOG = "org.kodein.log:kodein-log:0.11.1"
}

object Decompose {
    const val DECOMPOSE = "com.arkivanov.decompose:decompose:${Versions.DECOMPOSE}"
    const val COMPOSE_EXTENSION = "com.arkivanov.decompose:extensions-compose-jetbrains:${Versions.DECOMPOSE}"
}

object KtorClient {
    const val CORE = "io.ktor:ktor-client-core:${Versions.KTOR_CLIENT}"
    const val CIO = "io.ktor:ktor-client-cio:${Versions.KTOR_CLIENT}"
    const val OKHTTP = "io.ktor:ktor-client-okhttp:${Versions.KTOR_CLIENT}"
    const val SERIALIZATION = "io.ktor:ktor-client-serialization:${Versions.KTOR_CLIENT}"

    const val TEST = "io.ktor:ktor-client-mock:${Versions.KTOR_CLIENT}"
}

object Moko {
    const val RESOURCES = "dev.icerock.moko:resources:${Versions.MOKO}"
    const val RESOURCES_COMPOSE = "dev.icerock.moko:resources-compose:${Versions.MOKO}"
}

object Firebase {
    const val BOM = "com.google.firebase:firebase-bom:29.0.3"
    const val ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
    const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics-ktx"
    const val PERF = "com.google.firebase:firebase-perf-ktx"
}

object Test {
    const val JUNIT = "junit:junit:4.13.1"
}

object Misc {
    const val COIL_COMPOSE = "io.coil-kt:coil-compose:2.0.0-rc03"
    const val PROTOBUF = "com.google.protobuf:protobuf-javalite:3.19.1"
    const val DOCUMENTFILEX = "xyz.quaver:documentfilex:0.7.1"
    const val SUBSAMPLEDIMAGE = "xyz.quaver:subsampledimage:0.0.1-alpha22-SNAPSHOT"
    const val JSOUP = "org.jsoup:jsoup:1.14.3"
    const val DISK_LRU_CACHE = "com.jakewharton:disklrucache:2.0.2"
}
