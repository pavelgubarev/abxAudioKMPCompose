package gubarev.abxtestompose

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Presenter {

    private val context = PlatformContext()
    private var audioPlayers: MutableMap<TrackCode, MediaPlayerController> = mutableMapOf()

    private val _state = MutableStateFlow<ABXTestingState>(ABXTestingState())
    val state: StateFlow<ABXTestingState> = _state.asStateFlow()

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

    fun didTapPlay(chosenTrack: TrackCode) {
        _state.update {
            it.copy( userChosenTrack = chosenTrack)
        }

        arrayOf(TrackCode.A, TrackCode.B).forEach { code ->
            audioPlayers[code]?.let { player ->
                if (code == chosenTrack) player.start() else player.pause()
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

        _state.update {
            it.copy( trackProgress = progress )
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