package gubarev.abxtestompose

import kotlinx.coroutines.flow.MutableStateFlow

data class DownloaderModel(
    val url: String = "",
    val handler: FileHandler = FileHandler(),
    val fileName: String = "",
    var progress: MutableStateFlow<Float> = MutableStateFlow(0f),
    var filePath: String = ""
)

