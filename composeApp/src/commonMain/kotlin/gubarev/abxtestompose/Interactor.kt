package gubarev.abxtestompose
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

final class Interactor {

    fun downloadExample(fileHandler: FileHandler) {
        val downloader = FileDownloader(httpClient = defaultPlatformEngine)
        runBlocking {
            val job = launch {
                downloader.progressFlow.collect { progress ->
                    println("Download progress: $progress%")
                }
            }

            val filePath = downloader.downloadFile(
                url = "https://gubarev.ru/abx_audio_samples/Time-30.m4a",
                fileHandler = fileHandler,
                fileName = "Time-30.m4a"
            )

            println("File downloaded to: $filePath")
            job.cancel() // Stop collecting progress updates once the download is complete
        }
    }
}