object Versions {

    private const val kotlin = "1.3.31"

    // 2.9.3 works fine, but 2.9.4 is stricter and fails to play some invalid files
    // https://github.com/google/ExoPlayer/commit/32bad6915851b39a48d5385b1268a87d87d7ff20#diff-bf2270d195b25f7412752dbbd0d3fecaR120
    private const val exoplayer = "2.9.3"

    val minSdk = 23
    val targetSdk = 28

    val androidGradlePlugin = "com.android.tools.build:gradle:3.4.0"
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin"

    val appCompat = "androidx.appcompat:appcompat:1.0.2"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"

    val ktxCore = "androidx.core:core-ktx:1.0.1"

    val exoplayerCore = "com.google.android.exoplayer:exoplayer-core:$exoplayer"
    val exoplayerUi = "com.google.android.exoplayer:exoplayer-ui:$exoplayer"
    val exoplayerHls = "com.google.android.exoplayer:exoplayer-hls:$exoplayer"

    val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin"
}
