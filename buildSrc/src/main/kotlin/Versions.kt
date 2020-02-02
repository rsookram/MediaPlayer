object Versions {

    private const val kotlin = "1.3.61"

    // 2.9.3 works fine, but 2.9.4 is stricter and fails to play some invalid files
    // https://github.com/google/ExoPlayer/commit/32bad6915851b39a48d5385b1268a87d87d7ff20#diff-bf2270d195b25f7412752dbbd0d3fecaR120
    private const val exoplayer = "2.9.3"

    const val minSdk = 25
    const val targetSdk = 28

    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.3"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin"

    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"

    const val ktxCore = "androidx.core:core-ktx:1.0.2"

    const val exoplayerCore = "com.google.android.exoplayer:exoplayer-core:$exoplayer"
    const val exoplayerUi = "com.google.android.exoplayer:exoplayer-ui:$exoplayer"
    const val exoplayerHls = "com.google.android.exoplayer:exoplayer-hls:$exoplayer"

    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin"
}
