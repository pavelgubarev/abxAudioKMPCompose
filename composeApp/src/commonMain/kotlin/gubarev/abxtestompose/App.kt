package gubarev.abxtestompose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

private enum class Screen { Main, OpenFiles }

@Composable
@Preview
fun App(presenter: Presenter) {
    val state by presenter.state.collectAsStateWithLifecycle()
    var screen by remember { mutableStateOf(Screen.Main) }

    MaterialTheme(colorScheme = lightColorScheme()) {
        when (screen) {
            Screen.Main -> MainScreen(
                presenter = presenter,
                state = state,
                onOpenFiles = { screen = Screen.OpenFiles }
            )
            Screen.OpenFiles -> OpenFilesScreen(
                onLoad = { pathA, pathB ->
                    presenter.loadTracks(pathA, pathB)
                    screen = Screen.Main
                },
                onBack = { screen = Screen.Main }
            )
        }
    }
    LaunchedEffect(Unit) {
        presenter.onAppear()
    }
}

@Composable
private fun MainScreen(presenter: Presenter, state: ABXTestingState, onOpenFiles: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onOpenFiles) {
            Text("Open files…")
        }

        Spacer(Modifier.height(16.dp))

        statisticsCard(state)

        answerButtons { trackCode ->
            presenter.didTapAnswer(trackCode)
        }

        if (state.tracksLoaded) {
            player(presenter, state)
        }
    }
}

@Composable
private fun player(
    presenter: Presenter,
    state: ABXTestingState
) {
    Card {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text("Trial ${state.trialsCount + 1}", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { presenter.playOrPause() }) {
                Text(if (state.isPlaying) "Pause" else "Play")
            }
            playerSlider(state.sliderProgress) { sliderPosition ->
                presenter.didChangeSliderProgress(progress = sliderPosition.toDouble())
            }
        }
        trackChoiceSegmentedControl(state.userChosenTrack) { trackToPlay ->
            presenter.didChooseTrack(trackToPlay)
        }
    }
}

@Composable
fun statisticsCard(state: ABXTestingState) {
    Card {
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
    Column(
        Modifier.fillMaxWidth().padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("My answer: ")
        Column {
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
        onValueChange = { onValueChange(it) },
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
