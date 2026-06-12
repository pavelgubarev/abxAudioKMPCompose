package gubarev.abxtestompose

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val presenter = remember { Presenter() }
    val openFilesPresenter = remember { OpenFilesPresenter.create() }
    App(presenter, openFilesPresenter)
}
