import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = Versions.targetSdk

    defaultConfig {
        applicationId = "io.github.rsookram.mediaplayer"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 1
        versionName = "1.0"

        resConfigs("en")
    }

    buildFeatures {
        viewBinding = true
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
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

            // Just for testing release builds. Not actually distributed.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packagingOptions {
        exclude("/kotlin/**")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/*.version")
        exclude("kotlin-tooling-metadata.json")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.allWarningsAsErrors = true
}

dependencies {
    implementation(Versions.ktxCore)

    implementation(Versions.exoplayerCore)
    implementation(Versions.exoplayerUi)
    implementation(Versions.exoplayerHls)
}
