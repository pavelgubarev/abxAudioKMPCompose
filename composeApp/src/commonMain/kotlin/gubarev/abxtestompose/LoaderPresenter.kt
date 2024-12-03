package gubarev.abxtestompose

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoaderPresenter {


    private val _progressFlow = MutableStateFlow(0)
    val progressFlow: StateFlow<Int> = _progressFlow


    suspend fun startDownload() {
        val handler = FileHandler()
        interactor.downloadExample(
            handler
        )
    }

}