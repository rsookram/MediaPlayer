plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.rsookram.mediaplayer"

    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.rsookram.mediaplayer"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        resourceConfigurations += listOf("en", "anydpi")
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

    packaging.resources.excludes += listOf(
        "/kotlin/**",
        "META-INF/*.kotlin_module",
        "META-INF/*.version",
        "kotlin-tooling-metadata.json",
    )

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    val media = "1.2.1"
    implementation("androidx.media3:media3-exoplayer:$media")
    implementation("androidx.media3:media3-ui:$media")
}
