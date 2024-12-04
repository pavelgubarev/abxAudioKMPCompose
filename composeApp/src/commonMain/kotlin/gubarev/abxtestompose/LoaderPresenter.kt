package gubarev.abxtestompose

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class LoaderPresenter {

    private val interactor = LoaderInteractor()
    var downloadProgress= Pair(MutableStateFlow(0f), MutableStateFlow(0f))

    fun startDownload() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            //TODO: сделать цикл
            val job1 = async(Dispatchers.IO) {
                audioSamples.first().let {
                    interactor.downloadFile(
                        DownloaderModel(
                            it.url,
                            FileHandler(),
                            it.filename,
                            downloadProgress.first,
                            path1
                        )
                    )
                }
            }
            val job2 = async(Dispatchers.IO) {
                audioSamples[1].let  {
                    interactor.downloadFile(
                        DownloaderModel(
                            it.url,
                            FileHandler(),
                            it.filename,
                            downloadProgress.second
                        )
                    )
                }
            }
            val result1 = job1.await()
            val result2 = job2.await()

            println("Results: $result1")
        }
    }
}