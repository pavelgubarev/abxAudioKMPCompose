package gubarev.abxtestompose

import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun Loader(presenter: LoaderPresenter) {

    val progress by presenter.downloadProgress.collectAsStateWithLifecycle()

    MaterialTheme(colorScheme = lightColorScheme()) {


        Text("Progress: ${progress }")


        Button( { presenter.startDownload() } ) {
            Text("Load")
        }
    }
}