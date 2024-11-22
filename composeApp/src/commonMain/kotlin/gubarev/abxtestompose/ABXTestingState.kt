package gubarev.abxtestompose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ABXTestingState(
    val userChosenTrack: TrackCode = TrackCode.A,
    val answersCount: Int = 0,
    val correctAnswersCount: Int = 0,
    var currentCorrectAnswer: TrackCode = TrackCode.A,
    val sliderProgress: Double = 0.0,
    val isPlaying: Boolean = false
)