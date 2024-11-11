package gubarev.abxtestompose

expect class MediaPlayerController(platformContext: PlatformContext) {
    fun prepare(pathSource: String, listener: MediaPlayerListener)

    fun start()

    fun pause()

    fun stop()

    fun isPlaying(): Boolean

    fun release()

    fun syncTo(progress: Double)

    fun duration(): Double
}

expect class PlatformContext()