package gubarev.abxtestompose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

private const val KEY_PATH_A = "path_a"
private const val KEY_PATH_B = "path_b"

interface MainPresenterInterface {
    val state: StateFlow<ABXTestingState>
    fun loadTracks(pathA: String, pathB: String)
    fun didChooseTrack(chosenTrack: TrackCode)
    fun didTapAnswer(answer: TrackCode)
    fun didChangeSliderProgress(progress: Double)
    fun playOrPause()
    fun stopPlayback()
    fun onAppear()
    fun dispose()
}

class Presenter : MainPresenterInterface, MediaPlayerDelegate {

    private val context = PlatformContext()
    private val settings = AppSettings()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var audioPlayers: MutableMap<TrackCode, MediaPlayerController> = mutableMapOf()
    private var bothCodes = arrayOf(TrackCode.A, TrackCode.B)

    private val _state = MutableStateFlow(ABXTestingState())
    override val state: StateFlow<ABXTestingState> = _state.asStateFlow()

    private val listener = object : MediaPlayerListener {
        override fun onReady() {
        }

        override fun onAudioCompleted() {
        }

        override fun onError() {

        }
    }

    init {
        val savedA = settings.get(KEY_PATH_A)
        val savedB = settings.get(KEY_PATH_B)
        if (savedA != null && savedB != null) {
            scope.launch {
                val durA = getAudioDuration(savedA)
                val durB = getAudioDuration(savedB)
                if (durationsMatch(durA, durB)) {
                    loadTracks(savedA, savedB)
                }
            }
        }
    }

    override fun loadTracks(pathA: String, pathB: String) {
        audioPlayers.values.forEach { it.release() }
        audioPlayers.clear()
        _state.value = ABXTestingState(tracksLoaded = true, pathA = pathA, pathB = pathB)
        bothCodes.forEach { code ->
            val path = if (code == TrackCode.A) pathA else pathB
            val player = MediaPlayerController(platformContext = context)
            player.prepare(path, listener = listener, delegate = this, code = code)
            audioPlayers[code] = player
        }
        settings.set(KEY_PATH_A, pathA)
        settings.set(KEY_PATH_B, pathB)
        setNextCorrectAnswer()
        scope.launch {
            val metaA = getAudioMetadata(pathA)
            val metaB = getAudioMetadata(pathB)
            _state.update { it.copy(metadataA = metaA, metadataB = metaB) }
        }
    }

    override fun didChooseTrack(chosenTrack: TrackCode) {
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
        audioPlayers[trackToPlay]?.let {
            val syncToTrack = if (fromTrack == trackToPlay) trackToPlay else anotherPlayerCode(trackToPlay)
            it.syncTo(progress = audioPlayers[syncToTrack]?.getCurrentTime() ?: 0.0)
            audioPlayers[anotherPlayerCode(trackToPlay)]?.pause()
            if (_state.value.isPlaying) {
                it.start()
            }
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

    override fun didTapAnswer(answer: TrackCode) {
        _state.update {
            val newCorrectCount = if (answer == it.currentCorrectAnswer) it.correctAnswersCount + 1 else it.correctAnswersCount
            it.copy(trialsCount = it.trialsCount + 1, correctAnswersCount = newCorrectCount)
        }
        updateCanGetDifferenceState()
        setNextCorrectAnswer()
    }

    override fun didChangeSliderProgress(progress: Double) {
        syncProgress(progress)
    }

    override fun onAppear() {
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

    override fun stopPlayback() {
        if (_state.value.isPlaying) {
            _state.update { it.copy(isPlaying = false) }
            audioPlayers.values.forEach { it.pause() }
        }
    }

    override fun playOrPause() {
        val newState = _state.updateAndGet {
            it.copy(isPlaying = !it.isPlaying)
        }

        for ((code, player) in audioPlayers) {
            if (!newState.isPlaying) {
                player.pause()
            } else {
                if (code == getTrackToPlay(newState.userChosenTrack)) {
                    player.start()
                }
            }
        }
    }

    override fun dispose() {
        audioPlayers.values.forEach { it.release() }
        audioPlayers.clear()
        scope.cancel()
    }

    private fun updateCanGetDifferenceState() {
        _state.update {
            val newTrials = when {
                it.trialsCount < trialsToMinCorrect.keys.min() -> TrialsState.NotEnoughTrials
                it.trialsCount in trialsToMinCorrect.keys.min()..trialsToMinCorrect.keys.max() -> {
                    val minCorrectAnswersCount = trialsToMinCorrect[it.trialsCount] ?: 0
                    TrialsState.EnoughTrials(it.correctAnswersCount >= minCorrectAnswersCount)
                }
                else -> it.trials
            }
            it.copy(trials = newTrials)
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
