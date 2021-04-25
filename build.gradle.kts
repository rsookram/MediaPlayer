buildscript {

    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(Versions.androidGradlePlugin)
        classpath(Versions.kotlinGradlePlugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
