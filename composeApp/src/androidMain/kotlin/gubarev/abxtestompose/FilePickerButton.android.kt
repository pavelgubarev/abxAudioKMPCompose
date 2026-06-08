package gubarev.abxtestompose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
actual fun FilePickerButton(label: String, onFilePicked: (String) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.toString()?.let { onFilePicked(it) }
    }
    Button(onClick = { launcher.launch("audio/*") }) {
        Text(label)
    }
}
