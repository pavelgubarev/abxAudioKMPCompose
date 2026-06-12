package gubarev.abxtestompose
data class ABXTestingState(
    val tracksLoaded: Boolean = false,
    val pathA: String? = null,
    val pathB: String? = null,
    val metadataA: AudioMetadata? = null,
    val metadataB: AudioMetadata? = null,
    val userChosenTrack: TrackCode = TrackCode.A,
    val trialsCount: Int = 0,
    val correctAnswersCount: Int = 0,
    val currentCorrectAnswer: TrackCode = TrackCode.A,
    val sliderProgress: Double = 0.0,
    val isPlaying: Boolean = false,
    val trials: TrialsState = TrialsState.NotEnoughTrials
)

sealed class TrialsState {
    data class EnoughTrials(val canTellDifference: Boolean) : TrialsState()
    object NotEnoughTrials : TrialsState()
}