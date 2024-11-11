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

import androidx.compose.material.Slider

@Composable
@Preview
fun App() {
    val presenter = Presenter()

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { presenter.didTapPlay(TrackCode.A) }) {
                Text("Play A!")
            }
            Button(onClick = { presenter.didTapPlay(TrackCode.B) }) {
                Text("Play B!")
            }
            playerSlider(presenter)
        }
    }
}

@Composable
fun playerSlider(presenter: Presenter) {
    var sliderPosition by remember { mutableStateOf(50f) }

    Slider(
        value = sliderPosition,
        onValueChange = {
            sliderPosition = it
            presenter.didChangeSliderProgress(it)
        },
        valueRange = 0f..100f
    )
}