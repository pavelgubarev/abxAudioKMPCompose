package gubarev.abxtestompose
data class ABXTestingState(
    val userChosenTrack: TrackCode = TrackCode.A,
    val answersCount: Int = 0,
    val correctAnswersCount: Int = 0,
    var currentCorrectAnswer: TrackCode = TrackCode.A,
    val sliderProgress: Double = 0.0,
    val isPlaying: Boolean = false,
    var canTellDifference: DifferenceState = DifferenceState.NotEnoughTrials
)

sealed class DifferenceState {
    data class EnoughTrials(val canTellDifference: Boolean) : DifferenceState()
    object NotEnoughTrials : DifferenceState()
}