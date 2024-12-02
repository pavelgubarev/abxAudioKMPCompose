package gubarev.abxtestompose
data class ABXTestingState(
    val userChosenTrack: TrackCode = TrackCode.A,
    val trialsCount: Int = 0,
    val correctAnswersCount: Int = 0,
    var currentCorrectAnswer: TrackCode = TrackCode.A,
    val sliderProgress: Double = 0.0,
    val isPlaying: Boolean = false,
    var trials: TrialsState = TrialsState.NotEnoughTrials,
)

sealed class TrialsState {
    data class EnoughTrials(val canTellDifference: Boolean) : TrialsState()
    object NotEnoughTrials : TrialsState()
}