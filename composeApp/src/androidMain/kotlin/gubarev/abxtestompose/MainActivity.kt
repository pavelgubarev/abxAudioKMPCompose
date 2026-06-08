package gubarev.abxtestompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformContext.init(this)
        presenter = Presenter()

        setContent {
            App(presenter)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val presenter = Presenter()
    App(presenter)
}