package gubarev.abxtestompose

expect class MediaPlayerController(platformContext: PlatformContext) {
   // var currentProgress: Float

    fun prepare(pathSource: String, listener: MediaPlayerListener)

    fun start()

    fun pause()

    fun isPlaying(): Boolean

    fun release()

    fun syncTo(progress: Double)

    fun duration(): Double
}

expect class PlatformContext()