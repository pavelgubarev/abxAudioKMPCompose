package gubarev.abxtestompose

expect class MediaPlayerController(platformContext: PlatformContext) {

    fun prepare(pathSource: String, listener: MediaPlayerListener, delegate: PresenterInterface, code: TrackCode)

    fun start()

    fun pause()

    fun isPlaying(): Boolean

    fun release()

    fun syncTo(progress: Double)

    fun duration(): Double
}

expect class PlatformContext()