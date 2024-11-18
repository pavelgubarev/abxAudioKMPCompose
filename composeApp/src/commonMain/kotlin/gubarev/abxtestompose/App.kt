package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.material.Slider
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
@Preview
fun App(presenter: Presenter) {

    val state by presenter.state.collectAsStateWithLifecycle()

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { presenter.didTapPlay(TrackCode.A) }) {
                Text("Play A!")
            }
            Button(onClick = { presenter.didTapPlay(TrackCode.B) }) {
                Text("Play B!")
            }
            Button(onClick = { presenter.playOrPause() }) {
                Text(text = if (state.isPlaying) "Pause" else "Play")
            }
            playerSlider(
                state.trackProgress
            ) { sliderPosition ->
                presenter.didChangeSliderProgress(
                    progress = sliderPosition
                )
            }
            Text("Playing track " + state.userChosenTrack.toString())
        }
    }
}

@Composable
fun playerSlider(sliderPosition: Float, onValueChange: (Float) -> Unit) {
    Slider(
        value = sliderPosition,
        onValueChange = {
            onValueChange(it)
        },
        valueRange = 0f..100f
    )
}