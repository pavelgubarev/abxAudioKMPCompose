package gubarev.abxtestompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    val presenter = ABXTestingPesenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(presenter)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val presenter = ABXTestingPesenter()
    App(presenter)
}