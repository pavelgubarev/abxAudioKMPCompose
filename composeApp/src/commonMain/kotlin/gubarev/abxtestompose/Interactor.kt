package gubarev.abxtestompose

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoaderInteractor {

    suspend fun downloadFile(downloaderModel: DownloaderModel): String? {
        val downloader = FileDownloader(httpClient = defaultPlatformEngine)

        return downloader.downloadFile(downloaderModel)
    }
}