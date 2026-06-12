package gubarev.abxtestompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    lateinit var presenter: Presenter
    lateinit var openFilesPresenter: OpenFilesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformContext.init(this)
        presenter = Presenter()
        openFilesPresenter = OpenFilesPresenter(OpenFilesInteractor())

        setContent {
            App(presenter, openFilesPresenter)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(Presenter(), OpenFilesPresenter(OpenFilesInteractor()))
}