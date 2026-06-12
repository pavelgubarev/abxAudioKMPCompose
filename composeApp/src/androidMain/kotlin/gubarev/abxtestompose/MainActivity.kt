package gubarev.abxtestompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformContext.init(this)

        setContent {
            App(viewModel.presenter, viewModel.openFilesPresenter)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(Presenter(), OpenFilesPresenter.create())
}
