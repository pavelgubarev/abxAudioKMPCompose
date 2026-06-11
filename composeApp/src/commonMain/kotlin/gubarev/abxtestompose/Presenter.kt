package gubarev.abxtestompose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface PresenterInterface {
    fun didTimeChangeTo(time: Double, code: TrackCode)
}

class Presenter: PresenterInterface {

    private val context = PlatformContext()
    private val settings = AppSettings()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var audioPlayers: MutableMap<TrackCode, MediaPlayerController> = mutableMapOf()
    private var bothCodes = arrayOf(TrackCode.A, TrackCode.B)

    private val _state = MutableStateFlow(ABXTestingState())
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
        val savedA = settings.get("path_a")
        val savedB = settings.get("path_b")
        if (savedA != null && savedB != null) {
            scope.launch {
                val durA = getAudioDuration(savedA)
                val durB = getAudioDuration(savedB)
                if (durA != null && durB != null && kotlin.math.abs(durA - durB) <= 1.0) {
                    loadTracks(savedA, savedB)
                }
            }
        }
    }

    fun loadTracks(pathA: String, pathB: String) {
        audioPlayers.values.forEach { it.release() }
        audioPlayers.clear()
        _state.value = ABXTestingState(tracksLoaded = true, pathA = pathA, pathB = pathB)
        bothCodes.forEach { code ->
            val path = if (code == TrackCode.A) pathA else pathB
            val player = MediaPlayerController(platformContext = context)
            player.prepare(path, listener = listener, delegate = this, code = code)
            audioPlayers[code] = player
        }
        settings.set("path_a", pathA)
        settings.set("path_b", pathB)
        setNextCorrectAnswer()
        scope.launch {
            val metaA = getAudioMetadata(pathA)
            val metaB = getAudioMetadata(pathB)
            _state.update { it.copy(metadataA = metaA, metadataB = metaB) }
        }
    }

    fun didChooseTrack(chosenTrack: TrackCode) {
        if (chosenTrack == _state.value.userChosenTrack) {
            return
        }

        val fromTrack = _state.value.userChosenTrack

        _state.update {
            it.copy( userChosenTrack = chosenTrack)
        }

        switchToTrack(trackToPlay = getTrackToPlay(chosenTrack), fromTrack = getTrackToPlay(fromTrack))
    }

    private fun getTrackToPlay(chosenTrack: TrackCode): TrackCode {
        return if (chosenTrack == TrackCode.X) {
            _state.value.currentCorrectAnswer
        } else {
            chosenTrack
        }
    }

    private fun switchToTrack(trackToPlay: TrackCode, fromTrack: TrackCode) {

        println("$trackToPlay, $fromTrack")

        audioPlayers[trackToPlay]?.let {
            audioPlayers[anotherPlayerCode(trackToPlay)]?.pause()
            if (_state.value.isPlaying) {
                it.start()
            }
            val syncToTrack = if (fromTrack == trackToPlay) trackToPlay else anotherPlayerCode(trackToPlay)
            it.syncTo(progress = audioPlayers[syncToTrack]?.getCurrentTime() ?: 0.0)
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

    fun stopPlayback() {
        if (_state.value.isPlaying) {
            _state.update { it.copy(isPlaying = false) }
            audioPlayers.values.forEach { it.pause() }
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
        if (code == getTrackToPlay(_state.value.userChosenTrack)) {
            val duration = audioPlayers[code]?.duration() ?: 0.0
            if (duration > 0) {
                _state.update {
                    it.copy(sliderProgress = time * 100 / duration)
                }
            }
        }
    }
}