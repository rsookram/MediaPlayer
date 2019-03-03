object Versions {

    private const val kotlin = "1.3.21"
    private const val exoplayer = "2.9.6"

    val minSdk = 23
    val targetSdk = 28

    val androidGradlePlugin = "com.android.tools.build:gradle:3.3.1"
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin"

    val appCompat = "androidx.appcompat:appcompat:1.0.2"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"

    val ktxCore = "androidx.core:core-ktx:1.0.0"

    val exoplayerCore = "com.google.android.exoplayer:exoplayer-core:$exoplayer"
    val exoplayerUi = "com.google.android.exoplayer:exoplayer-ui:$exoplayer"
    val exoplayerHls = "com.google.android.exoplayer:exoplayer-hls:$exoplayer"

    val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin"
}
