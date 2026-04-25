package nz.keeleysgreenhouse.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light theme only — v1 ships no dark mode.
private val LightColors = lightColorScheme(
    primary = Forest,
    onPrimary = Cream,
    secondary = Terracotta,
    onSecondary = Cream,
    tertiary = Honey,
    onTertiary = OnSurfaceInk,
    background = Cream,
    onBackground = OnSurfaceInk,
    surface = Cream,
    onSurface = OnSurfaceInk,
    surfaceVariant = Cream,
    onSurfaceVariant = OnSurfaceInk,
    outline = Brass,
    error = AppError,
    onError = Cream
)

@Composable
fun GreenhouseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = GreenhouseTypography,
        shapes = GreenhouseShapes,
        content = content
    )
}
