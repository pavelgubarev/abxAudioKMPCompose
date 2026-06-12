package gubarev.abxtestompose

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val presenter: MainPresenterInterface = Presenter()
    val openFilesPresenter: OpenFilesPresenterInterface = OpenFilesPresenter.create()

    override fun onCleared() {
        presenter.dispose()
        openFilesPresenter.dispose()
    }
}
