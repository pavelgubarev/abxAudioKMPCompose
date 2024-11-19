package gubarev.abxtestompose

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface PresenterInterface {
    fun didTimeChangeTo(time: Double, code: TrackCode)
}

class Presenter: PresenterInterface {

    private val context = PlatformContext()
    private var audioPlayers: MutableMap<TrackCode, MediaPlayerController> = mutableMapOf()
    private var bothCodes = arrayOf(TrackCode.A, TrackCode.B)

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
        bothCodes.forEach { code ->
            val player = MediaPlayerController(platformContext = context)
            player.prepare(tracksToTest[code]!!, listener = listener, delegate = this, code = code)
            audioPlayers[code] = player
        }
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

    fun didChangeSliderProgress(progress: Double) {
        syncProgress(progress)
    }

    private fun syncProgress(progress: Double) {
        audioPlayers.values.forEach { player ->
            val newProgress = (player.duration() * progress / 100).toDouble()
            player.syncTo(newProgress)
        }

        _state.update {
            it.copy( sliderProgress = progress )
        }
    }

    fun playOrPause() {
        val isCurrentlyPlaying = !_state.value.isPlaying

        _state.update {
            it.copy(isPlaying = isCurrentlyPlaying)
        }

        //TODO: можно один цикл по audioplayers?
        bothCodes.forEach { code ->
            audioPlayers[code]?.let { player ->
                if (!isCurrentlyPlaying) {
                    player.pause()
                }
                if (isCurrentlyPlaying && code == _state.value.userChosenTrack) {
                    player.start()
                }
            }
        }
    }

    override fun didTimeChangeTo(time: Double, code: TrackCode) {

        if (code == _state.value.userChosenTrack) {
            val duration = audioPlayers[code]?.duration() ?: 0.0
            val progress = time * 100 / duration

            //TODO:  обновить время другого плеера
            _state.update {
                it.copy(sliderProgress = progress)
            }
        }
    }

}