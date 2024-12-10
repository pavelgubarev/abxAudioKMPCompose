package gubarev.abxtestompose

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

typealias TracksToTest = Map<TrackCode, String>

sealed class DownloadResult {
    object Failure : DownloadResult()
    data class Success(val tracks: TracksToTest): DownloadResult()
}

class LoaderPresenter {

    private val interactor = LoaderInteractor()
    var downloadProgress= Pair(MutableStateFlow(0f), MutableStateFlow(0f))

    fun download(onFinish: (DownloadResult) -> Unit) {

        val scope = CoroutineScope(Dispatchers.Default)
        val jobs = ArrayList<Deferred<String?>>()
        scope.launch {
            audioSamples.toList().forEachIndexed { index, sample ->
                jobs.add(
                    async(Dispatchers.IO) {
                    interactor.downloadFile(
                        DownloaderModel(
                            sample.url,
                            FileHandler(),
                            sample.filename,
                            downloadProgress.toList()[index]
                        )
                    )
                }
                )
            }
            val results = jobs.awaitAll().mapNotNull{ it }
            if (results.count() == 2) {
                onFinish(DownloadResult.Success(
                    mapOf(
                        TrackCode.A to results[0],
                        TrackCode.B to results[1]
                    )
                )
                )
            } else {
                onFinish(DownloadResult.Failure)
            }
        }
    }
}