package gubarev.abxtestompose

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun GlassCard(modifier: Modifier, content: @Composable () -> Unit) {
    Card(modifier = modifier) { content() }
}
