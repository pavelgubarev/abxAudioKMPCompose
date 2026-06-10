package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private sealed interface PickState {
    data object None : PickState
    data object Validating : PickState
    data class Valid(val path: String) : PickState
    data object Invalid : PickState
}

@Composable
fun OpenFilesScreen(
    onLoad: (pathA: String, pathB: String) -> Unit,
    onBack: () -> Unit
) {
    var stateA by remember { mutableStateOf<PickState>(PickState.None) }
    var stateB by remember { mutableStateOf<PickState>(PickState.None) }

    val scope = rememberCoroutineScope()

    fun pick(onResult: (PickState) -> Unit): (String) -> Unit = { path ->
        onResult(PickState.Validating)
        scope.launch {
            val valid = withContext(Dispatchers.Default) { validateAudioFile(path) }
            onResult(if (valid) PickState.Valid(path) else PickState.Invalid)
        }
    }

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Open Files", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        TrackFileCard(label = "Track A", state = stateA, onFilePicked = pick { stateA = it })
        Spacer(Modifier.height(16.dp))
        TrackFileCard(label = "Track B", state = stateB, onFilePicked = pick { stateB = it })

        Spacer(Modifier.height(32.dp))

        val pathA = (stateA as? PickState.Valid)?.path
        val pathB = (stateB as? PickState.Valid)?.path

        Button(
            onClick = { onLoad(pathA!!, pathB!!) },
            enabled = pathA != null && pathB != null
        ) {
            Text("Load")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) { Text("Back") }
    }
}

@Composable
private fun TrackFileCard(label: String, state: PickState, onFilePicked: (String) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FilePickerButton(
                label = if (state is PickState.None) "Select file…" else "Change file…",
                onFilePicked = onFilePicked
            )
            Spacer(Modifier.height(4.dp))
            when (state) {
                is PickState.None -> {}
                is PickState.Validating -> Text("Checking…", style = MaterialTheme.typography.bodySmall)
                is PickState.Valid -> Text(
                    state.path.substringAfterLast('/'),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                is PickState.Invalid -> Text(
                    "Not a playable audio file",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
