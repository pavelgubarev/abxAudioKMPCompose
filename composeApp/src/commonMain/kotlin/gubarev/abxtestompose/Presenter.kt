package gubarev.abxtestompose

class Presenter {

    private val context = PlatformContext()
    private var audioPlayers: MutableMap<TrackCode, MediaPlayerController> = mutableMapOf()

    private val listener = object : MediaPlayerListener {
        override fun onReady() {
        }

        override fun onAudioCompleted() {
        }

        override fun onError() {

        }
    }

    init {
        setInitialState(
            mutableMapOf(
                TrackCode.A to "files/Time-30.m4a",
                TrackCode.B to "files/Time-50.m4a"
            )
        )

    }

    private fun setInitialState(tracksToTest: MutableMap<TrackCode, String>) {
        val playerA = MediaPlayerController(platformContext = context)
        playerA.prepare(tracksToTest[TrackCode.A]!!, listener = listener)

        val playerB = MediaPlayerController(platformContext = context)
        playerB.prepare(tracksToTest[TrackCode.B]!!, listener = listener)

        audioPlayers[TrackCode.A] = playerA
        audioPlayers[TrackCode.B] = playerB
    }

    fun getAudioPlayer(track: TrackCode) : MediaPlayerController {
        return audioPlayers[track]!!
    }

    fun didTapPlay(trackToPlay: TrackCode) {
        arrayOf(TrackCode.A, TrackCode.B).forEach { code ->
            audioPlayers[code]?.let { player ->
                if (code == trackToPlay) player.start() else player.pause()
            }
        }
    }

    fun didChangeSliderProgress(progress: Float) {
        syncProgress(progress)
    }

    private fun syncProgress(progress: Float) {
        audioPlayers.values.forEach { player ->
            val newProgress = (player.duration() * progress / 100).toDouble()
            player.syncTo(newProgress)
        }
    }

    fun playOrPause() {
//        if (!player.isPlaying()) {
//            player.start()
//        } else {
//            player.pause()
//        }
    }

}