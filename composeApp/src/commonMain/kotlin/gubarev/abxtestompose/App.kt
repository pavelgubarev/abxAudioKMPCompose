package gubarev.abxtestompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material.Slider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke

@Composable
@Preview
fun App(presenter: Presenter) {
    val state by presenter.state.collectAsStateWithLifecycle()

    MaterialTheme(colorScheme = lightColorScheme()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            statisticsCard(state)

            answerButtons { trackCode ->
                presenter.didTapAnswer(trackCode)
            }

            player(presenter, state)
        }

        LaunchedEffect(Unit) {
            presenter.onAppear()
        }
    }
}

@Composable
private fun player(
    presenter: Presenter,
    state: ABXTestingState
) {
    Card() {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
            Button(onClick = { presenter.playOrPause() }) {
                Text(if (state.isPlaying) "Pause" else "Play")
            }
            playerSlider(
                state.sliderProgress
            ) { sliderPosition ->
                presenter.didChangeSliderProgress(
                    progress = sliderPosition.toDouble()
                )
            }
        }
        trackChoiceSegmentedControl(state.userChosenTrack) { trackToPlay ->
            presenter.didChooseTrack(trackToPlay)
        }
    }
}

@Composable
fun statisticsCard(state: ABXTestingState) {
    Card() {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text("${state.trialsCount} total trials ")
            Text("${state.correctAnswersCount} correct answers", Modifier.padding(bottom = 10.dp))
            when (val trialsState = state.trials) {
                is TrialsState.EnoughTrials -> {
                    if (trialsState.canTellDifference) {
                        Text("You can tell the difference between tracks")
                    } else {
                        Text("You cannot tell the difference between tracks")
                    }
                }

                is TrialsState.NotEnoughTrials -> {
                    Text("You need at least ${trialsToMinCorrect.keys.min()} trials to know the result")
                }
            }
        }
    }
}

@Composable
fun answerButtons(didTap: (TrackCode) -> Unit) {
    Column(Modifier.fillMaxWidth().padding( start = 16.dp, top = 24.dp, 16.dp, bottom = 24.dp), horizontalAlignment = Alignment.Start) {
        Text("My answer: ")
        Column() {
            Button(onClick = { didTap(TrackCode.A) }) {
                Text("X is A")
            }
            Button(onClick = { didTap(TrackCode.B) }) {
                Text("X is B")
            }
        }
    }
}

@Composable
fun playerSlider(sliderPosition: Double, onValueChange: (Float) -> Unit) {
    Slider(
        value = sliderPosition.toFloat(),
        onValueChange = {
            onValueChange(it)
        },
        valueRange = 0f..100f
    )
}

@Composable
fun trackChoiceSegmentedControl(selectedOption: TrackCode, onOptionSelected: (TrackCode) -> Unit) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrackCode.entries.forEach { option ->
            val isSelected = option == selectedOption
            OutlinedButton(
                onClick = { onOptionSelected(option) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text(option.toString())
            }
        }
    }
}
