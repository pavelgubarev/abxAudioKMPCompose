package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun OpenFilesScreen(
    presenter: OpenFilesPresenterInterface,
    state: OpenFilesState,
    canGoBack: Boolean,
    onLoad: (pathA: String, pathB: String) -> Unit,
    onBack: () -> Unit
) {
    DisposableEffect(Unit) {
        onDispose { presenter.cancelSampleLoad() }
    }

    LaunchedEffect(state.sampleLoaded) {
        if (state.sampleLoaded) {
            presenter.consumeSampleLoaded()
            if (state.canLoad) {
                onLoad(state.pathA!!, state.pathB!!)
            }
        }
    }

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Open Files", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Choose two tracks of equal length to compare",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("Developer sample", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Lossless vs lossy — a quick demo to try the app",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                if (state.isLoadingSample) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Downloading…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(12.dp))
                        TextButton(onClick = { presenter.cancelSampleLoad() }) {
                            Text("Cancel")
                        }
                    }
                } else {
                    Button(
                        onClick = { presenter.loadSample() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Load sample from the developer")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TrackFileCard(label = "Track A", state = state.fileA, onFilePicked = { presenter.pickFile(FileSlot.A, it) })
        Spacer(Modifier.height(16.dp))
        TrackFileCard(label = "Track B", state = state.fileB, onFilePicked = { presenter.pickFile(FileSlot.B, it) })

        Spacer(Modifier.height(32.dp))

        if (state.durationMismatch) {
            Text(
                "Files must have equal length",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = { onLoad(state.pathA!!, state.pathB!!) },
            enabled = state.canLoad
        ) {
            Text("Load")
        }

        if (canGoBack) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
private fun TrackFileCard(label: String, state: FilePickState, onFilePicked: (String) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FilePickerButton(
                label = if (state is FilePickState.None) "Select file…" else "Change file…",
                onFilePicked = onFilePicked
            )
            Spacer(Modifier.height(4.dp))
            when (state) {
                is FilePickState.None -> {}
                is FilePickState.Validating -> Text("Checking…", style = MaterialTheme.typography.bodySmall)
                is FilePickState.Valid -> Text(
                    state.path.substringAfterLast('/'),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                is FilePickState.Invalid -> Text(
                    "Not a playable audio file",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
