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
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { presenter.playOrPause() }) {
                Text(text = if (state.isPlaying) "Pause" else "Play")
            }
            playerSlider(
                state.sliderProgress
            ) { sliderPosition ->
                presenter.didChangeSliderProgress(
                    progress = sliderPosition.toDouble()
                )
            }
            TrackChoiceSegmentedControl(
                state.userChosenTrack
            ) { trackToPlay ->
                presenter.didChooseTrack(trackToPlay)
            }
            answerButtons() { trackCode ->
                presenter.didTapAnswer(trackCode)
            }
            Text("Total answers: " + state.answersCount)
            Text("Correct answers: " + state.correctAnswersCount)
        }
    }
    LaunchedEffect(Unit) {
        presenter.onAppear()
    }
}

@Composable
fun answerButtons(didTap: (TrackCode) -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { didTap(TrackCode.A) }) {
            Text("X is A")
        }
        Button(onClick = { didTap(TrackCode.B) }) {
            Text("X is B")
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
fun MaterialSegmentedControl(
    selectedOption: TrackCode,
    onOptionSelected: (TrackCode) -> Unit
) {
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

@Composable
fun TrackChoiceSegmentedControl(
    userChosenTrack: TrackCode,
    onOptionSelected: (TrackCode) -> Unit
) {
    Column {
        Text("Playing track "+ userChosenTrack.toString(), Modifier.padding(16.dp))
        MaterialSegmentedControl(
            selectedOption = userChosenTrack,
            onOptionSelected = onOptionSelected
        )
    }
}