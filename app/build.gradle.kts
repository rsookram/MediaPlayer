import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Versions.targetSdk)

    defaultConfig {
        applicationId = "io.github.rsookram.mediaplayer"
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = 1
        versionName = "1.0"

        resConfigs("en")
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
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
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.allWarningsAsErrors = true
}

dependencies {
    implementation(Versions.constraintLayout)

    implementation(Versions.ktxCore)

    implementation(Versions.exoplayerCore)
    implementation(Versions.exoplayerUi)
    implementation(Versions.exoplayerHls)

    implementation(Versions.kotlinStdlib)
}
