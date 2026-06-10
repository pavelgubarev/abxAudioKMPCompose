package gubarev.abxtestompose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// iOS Calendar palette: clean grouped background, white interactive surfaces
private val iosColorScheme = lightColorScheme(
    background = Color.Transparent,
    surface = Color.Transparent,
    primary = Color(0xFFE5E5EA),
    onPrimary = Color(0xFF1C1C1E),
    primaryContainer = Color(0xFFE5E5EA),
    onPrimaryContainer = Color(0xFF1C1C1E),
    secondary = Color(0xFFE5E5EA),
    onSecondary = Color(0xFF1C1C1E),
    outline = Color(0xFFD1D1D6),
)

private val iosShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// iOS systemGroupedBackground — exactly what Calendar uses
private val iosBackground = Color(0xFFF2F2F7)

@Composable
actual fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = iosColorScheme, shapes = iosShapes) {
        Box(modifier = Modifier.fillMaxSize().background(iosBackground)) {
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                content()
            }
        }
    }
}
