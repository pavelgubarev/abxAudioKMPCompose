package gubarev.abxtestompose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

private enum class Screen { Main, OpenFiles }

@Composable
fun App(presenter: Presenter) {
    val state by presenter.state.collectAsStateWithLifecycle()
    var screen by remember { mutableStateOf(Screen.Main) }

    AppTheme {
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
        if (state.tracksLoaded && state.pathA != null && state.pathB != null) {
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    TrackInfo("Track A", state.pathA, state.metadataA)
                    Spacer(Modifier.height(8.dp))
                    TrackInfo("Track B", state.pathB, state.metadataB)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onOpenFiles) { Text("Change…") }
                }
            }
        } else {
            Button(onClick = onOpenFiles) { Text("Open files…") }
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
private fun TrackInfo(label: String, path: String, metadata: AudioMetadata?) {
    Text("$label: ${path.substringAfterLast('/')}", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
    val detail = metadata?.displayString()?.takeIf { it.isNotEmpty() }
    if (detail != null) {
        Text(detail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun player(
    presenter: Presenter,
    state: ABXTestingState
) {
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
        Text("Trial ${state.trialsCount + 1}", style = MaterialTheme.typography.titleMedium)
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    trackChoiceSegmentedControl(state.userChosenTrack) { trackToPlay ->
                        presenter.didChooseTrack(trackToPlay)
                    }
                    FilledIconButton(
                        onClick = { presenter.playOrPause() },
                        modifier = Modifier.size(88.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }
                playerSlider(state.sliderProgress) { sliderPosition ->
                    presenter.didChangeSliderProgress(progress = sliderPosition.toDouble())
                }
            }
        }
    }
}

@Composable
fun statisticsCard(state: ABXTestingState) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text("${state.correctAnswersCount}/${state.trialsCount}", style = MaterialTheme.typography.displayMedium)
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

@Composable
fun answerButtons(didTap: (TrackCode) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { didTap(TrackCode.A) },
            modifier = Modifier.weight(1f).height(64.dp),
        ) {
            Text("X is A", style = MaterialTheme.typography.titleMedium)
        }
        Button(
            onClick = { didTap(TrackCode.B) },
            modifier = Modifier.weight(1f).height(64.dp),
        ) {
            Text("X is B", style = MaterialTheme.typography.titleMedium)
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
@Preview
private fun AppPreview() {
    val state = ABXTestingState(
        tracksLoaded = true,
        pathA = "file:///music/track_a.flac",
        pathB = "file:///music/track_b.flac",
        metadataA = AudioMetadata(1411, 44100, 16),
        metadataB = AudioMetadata(320, 44100, null),
        trialsCount = 5,
        correctAnswersCount = 3,
    )
    AppTheme {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            statisticsCard(state)
            Spacer(Modifier.height(16.dp))
            answerButtons {}
        }
    }
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
