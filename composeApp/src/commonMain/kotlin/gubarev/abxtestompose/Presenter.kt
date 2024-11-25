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

    fun didChooseTrack(chosenTrack: TrackCode) {
        if (chosenTrack == _state.value.userChosenTrack) {
            return
        }

        // TODO: remove
        val test = audioPlayers[chosenTrack]?.duration()

        _state.update {
            it.copy( userChosenTrack = chosenTrack)
        }

        switchToTrack(trackToPlay = getTrackToPlay(chosenTrack))
    }

    private fun getTrackToPlay(chosenTrack: TrackCode): TrackCode {
        return if (chosenTrack == TrackCode.X) {
            _state.value.currentCorrectAnswer
        } else {
            chosenTrack
        }
    }

    private fun switchToTrack(trackToPlay: TrackCode) {
        audioPlayers[trackToPlay]?.let {
            audioPlayers[anotherPlayerCode(trackToPlay)]?.pause()
            it.start()
            it.syncTo(progress = audioPlayers[anotherPlayerCode(trackToPlay)]?.getCurrentTime() ?: 0.0)
        }
    }

    private fun anotherPlayerCode(code: TrackCode): TrackCode {
        return if (code == TrackCode.A) TrackCode.B else TrackCode.A
    }

    private fun setNextCorrectAnswer() {
        _state.update {
            it.copy( currentCorrectAnswer = if (kotlin.random.Random.nextBoolean()) TrackCode.A else TrackCode.B )
        }

        if (state.value.isPlaying) {
           playOrPause()
        }
    }

    fun didTapAnswer(answer: TrackCode) {
        _state.update {
            it.copy( answersCount = it.answersCount + 1)
        }

        if (answer == _state.value.currentCorrectAnswer) {
            _state.update {
                it.copy( correctAnswersCount = it.correctAnswersCount + 1)
            }
        }

        setNextCorrectAnswer()
    }

    fun didChangeSliderProgress(progress: Double) {
        syncProgress(progress)
    }

    fun onAppear() {
        setNextCorrectAnswer()
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
        _state.update {
            it.copy(isPlaying = it.isPlaying)
        }

        for ((code, player) in audioPlayers) {
            if (!_state.value.isPlaying) {
                player.pause()
            }
            if (_state.value.isPlaying && code == getTrackToPlay(_state.value.userChosenTrack)) {
                player.start()
            }
        }
    }

    override fun didTimeChangeTo(time: Double, code: TrackCode) {
        if (code == _state.value.userChosenTrack) {
            val duration = audioPlayers[code]?.duration() ?: 0.0
            val progress = time * 100 / duration

            _state.update {
                it.copy(sliderProgress = progress)
            }
        }
    }

}