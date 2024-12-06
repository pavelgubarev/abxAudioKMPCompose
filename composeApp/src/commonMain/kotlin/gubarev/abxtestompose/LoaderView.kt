package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LoaderView(presenter: LoaderPresenter, onNavigateToTesting: (TracksToTest) -> Unit) {

    val progressA by presenter.downloadProgress.first.collectAsStateWithLifecycle()
    val progressB by presenter.downloadProgress.second.collectAsStateWithLifecycle()

    var downloadedTracks: TracksToTest = mapOf()

    MaterialTheme(colorScheme = lightColorScheme()) {
        var isButtonEnabled by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
            Card(Modifier.padding(bottom = 24.dp)) {
                Button({
                    presenter.download() { tracks ->
                        downloadedTracks = tracks
                        isButtonEnabled = true
                    }
                }
                ) {
                    Text("Load")
                }
                Text("Sample A", Modifier.padding(bottom = 8.dp))
                LinearProgressIndicator( { progressA }, Modifier.fillMaxWidth())
                Text("Sample B")
                LinearProgressIndicator( { progressB }, Modifier.fillMaxWidth())
            }

            Button(
                onClick = { onNavigateToTesting(downloadedTracks ?: mapOf()) },
                enabled = isButtonEnabled
            ) {
                Text(text = "Go Testing")
            }
        }
    }

}