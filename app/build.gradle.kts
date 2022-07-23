plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "io.github.rsookram.mediaplayer"
        minSdk = 30
        targetSdk = 32
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
            if (System.getenv("MEDIA_STORE_FILE") != null) {
                storeFile = file(System.getenv("MEDIA_STORE_FILE"))
                storePassword = System.getenv("MEDIA_STORE_PASSWORD")
                keyAlias = System.getenv("MEDIA_KEY_ALIAS")
                keyPassword = System.getenv("MEDIA_KEY_PASSWORD")
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
}

dependencies {
    val exoplayer = "2.18.0"
    implementation("com.google.android.exoplayer:exoplayer-core:$exoplayer")
    implementation("com.google.android.exoplayer:exoplayer-ui:$exoplayer")
}
