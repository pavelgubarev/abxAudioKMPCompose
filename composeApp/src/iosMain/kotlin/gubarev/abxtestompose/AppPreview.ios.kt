package gubarev.abxtestompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun AppIosPreview() {
    val state = ABXTestingState(
        tracksLoaded = true,
        pathA = "file:///music/track_a.flac",
        pathB = "file:///music/track_b.flac",
        metadataA = AudioMetadata(1411, 44100, 16),
        metadataB = AudioMetadata(320, 44100, null),
        trialsCount = 5,
        correctAnswersCount = 3,
    )
    AppTheme {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            statisticsCard(state)
            Spacer(Modifier.height(16.dp))
            answerButtons {}
        }
    }
}
