package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LoaderView(presenter: LoaderPresenter) {

    val progressA by presenter.downloadProgress.first.collectAsStateWithLifecycle()
    val progressB by presenter.downloadProgress.second.collectAsStateWithLifecycle()

    MaterialTheme(colorScheme = lightColorScheme()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
            Button({ presenter.startDownload() }) {
                Text("Load")
            }
            LinearProgressIndicator( { progressA }, Modifier.fillMaxWidth())
            LinearProgressIndicator( { progressB }, Modifier.fillMaxWidth())
        }
    }
}