plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.rsookram.mediaplayer"

    compileSdk = 33

    defaultConfig {
        applicationId = "io.github.rsookram.mediaplayer"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        resConfigs("en", "anydpi")
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            if (System.getenv("STORE_FILE") != null) {
                storeFile = file(System.getenv("STORE_FILE"))
                storePassword = System.getenv("STORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
        }
    }

    lint {
        checkReleaseBuilds = false
    }

    packagingOptions {
        exclude("/kotlin/**")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/*.version")
        exclude("kotlin-tooling-metadata.json")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    val media = "1.0.0"
    implementation("androidx.media3:media3-exoplayer:$media")
    implementation("androidx.media3:media3-ui:$media")
}
