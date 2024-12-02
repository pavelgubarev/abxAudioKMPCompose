package gubarev.abxtestompose

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

final class Interactor {

    val downloader = FileDownloader(httpClient = defaultPlatformEngine)

    val downloadProgress = downloader.progressFlow

    suspend fun downloadExample(fileHandler: FileHandler) {
        withContext(Dispatchers.IO)  {
            val filePath = downloader.downloadFile(
                url = "https://gubarev.ru/abx_audio_samples/Time-30.m4a",
                fileHandler = fileHandler,
                fileName = "Time-30.m4a"
            )
            println("File downloaded to: $filePath")
        }
    }
}