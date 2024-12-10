package gubarev.abxtestompose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

interface ABXTestingPresenterInterface {
    fun didTimeChangeTo(time: Double, code: TrackCode)
}

class ABXTestingPresenter: ABXTestingPresenterInterface {

    private val context = PlatformContext()
    private var audioPlayers: MutableMap<TrackCode, MediaPlayerController> = mutableMapOf()
    private var bothCodes = arrayOf(TrackCode.A, TrackCode.B)

    private val _state = MutableStateFlow(ABXTestingState())
    val state: StateFlow<ABXTestingState> = _state.asStateFlow()

    private var viewModelJob = SupervisorJob()
    protected val viewModelScope = CoroutineScope(Main + viewModelJob )

    private val listener = object : MediaPlayerListener {
        override fun onReady() {
        }

        override fun onAudioCompleted() {
        }

        override fun onError() {

        }
    }

    fun configure(tracksToTest: TracksToTest) {
        bothCodes.forEach { code ->
            val player = MediaPlayerController(platformContext = context)
            tracksToTest[code]?.let { track ->
                player.prepare(track, listener = listener, delegate = this, code = code)
            }
            audioPlayers[code] = player
        }
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
            if (_state.value.isPlaying) {
                it.start()
            }
            it.syncTo(progress = audioPlayers[anotherPlayerCode(trackToPlay)]?.getCurrentTime() ?: 0.0)
        }
    }

    private fun anotherPlayerCode(code: TrackCode): TrackCode {
        return if (code == TrackCode.A) TrackCode.B else TrackCode.A
    }

    private fun setNextCorrectAnswer() {
        _state.update {
            it.copy( currentCorrectAnswer = if (Random.nextBoolean()) TrackCode.A else TrackCode.B )
        }

        if (state.value.isPlaying) {
           playOrPause()
        }
    }

    fun didTapAnswer(answer: TrackCode) {
        _state.update {
            it.copy( trialsCount = it.trialsCount + 1)
        }

        if (answer == _state.value.currentCorrectAnswer) {
            _state.update {
                it.copy( correctAnswersCount = it.correctAnswersCount + 1)
            }
        }
        updateCanGetDifferenceState()
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
            val newProgress = (player.duration() * progress / 100)
            player.syncTo(newProgress)
        }

        _state.update {
            it.copy( sliderProgress = progress )
        }
    }

    fun playOrPause() {
        _state.update {
            it.copy(isPlaying = !it.isPlaying)
        }

        for ((code, player) in audioPlayers) {
            if (!_state.value.isPlaying) {
                player.pause()
            } else {
                if (code == getTrackToPlay(_state.value.userChosenTrack)) {
                    player.start()
                }
            }
        }
    }

    private fun updateCanGetDifferenceState() {
        when {
            (_state.value.trialsCount < trialsToMinCorrect.keys.min()) -> {
                _state.update {
                    it.copy (trials = TrialsState.NotEnoughTrials)
                }
            }
            (_state.value.trialsCount in trialsToMinCorrect.keys.min()..trialsToMinCorrect.keys.max()) -> {
                val minCorrectAnswersCount = trialsToMinCorrect[_state.value.trialsCount] ?: 0
                val canTellDifference: Boolean = _state.value.correctAnswersCount >= minCorrectAnswersCount
                _state.update {
                    it.copy (trials = TrialsState.EnoughTrials(canTellDifference))
                }
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