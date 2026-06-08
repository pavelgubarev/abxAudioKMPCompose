package gubarev.abxtestompose

import androidx.compose.runtime.Composable

@Composable
expect fun FilePickerButton(label: String, onFilePicked: (String) -> Unit)
