buildscript {

    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath(Versions.androidGradlePlugin)
        classpath(Versions.kotlinGradlePlugin)
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}
