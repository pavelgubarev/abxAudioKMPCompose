package gubarev.abxtestompose

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val presenter = LoaderPresenter()

    LoaderView(presenter)
}