package gubarev.abxtestompose

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class LoaderPresenter {

    private val interactor = LoaderInteractor()
    var downloadProgress= Pair(MutableStateFlow(0f), MutableStateFlow(0f))

    fun download(onFinish: (Map<TrackCode, String>) -> Unit) {
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
                            downloadProgress.first
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
            val result1: String = job1.await().toString()
            val result2: String = job2.await().toString()

            onFinish(
                mapOf(
                    TrackCode.A to result1,
                    TrackCode.B to result2
                )
            )
        }
    }
}