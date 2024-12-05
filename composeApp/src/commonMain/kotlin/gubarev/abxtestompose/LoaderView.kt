package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LoaderView(presenter: LoaderPresenter, onNavigateToTesting: (String) -> Unit) {

    val progressA by presenter.downloadProgress.first.collectAsStateWithLifecycle()
    val progressB by presenter.downloadProgress.second.collectAsStateWithLifecycle()

    MaterialTheme(colorScheme = lightColorScheme()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
            Button({ presenter.startDownload() }) {
                Text("Load")
            }
            Text("Sample A")
            LinearProgressIndicator( { progressA }, Modifier.fillMaxWidth())
            Text("Sample B")
            LinearProgressIndicator( { progressB }, Modifier.fillMaxWidth())

            Button(onClick = { onNavigateToTesting("Ha") }) {
                Text(text = "Go Testing")
            }
        }
    }

}