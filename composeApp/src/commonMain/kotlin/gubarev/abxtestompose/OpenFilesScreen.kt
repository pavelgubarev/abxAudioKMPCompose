package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun OpenFilesScreen(
    onLoad: (pathA: String, pathB: String) -> Unit,
    onBack: () -> Unit
) {
    var pathA by remember { mutableStateOf<String?>(null) }
    var pathB by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Open Files", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        TrackFileCard(
            trackLabel = "Track A",
            selectedPath = pathA,
            onFilePicked = { pathA = it }
        )

        Spacer(Modifier.height(16.dp))

        TrackFileCard(
            trackLabel = "Track B",
            selectedPath = pathB,
            onFilePicked = { pathB = it }
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onLoad(pathA!!, pathB!!) },
            enabled = pathA != null && pathB != null
        ) {
            Text("Load")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
private fun TrackFileCard(trackLabel: String, selectedPath: String?, onFilePicked: (String) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(trackLabel, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FilePickerButton(label = "Select file…", onFilePicked = onFilePicked)
            if (selectedPath != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = selectedPath.substringAfterLast('/'),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
