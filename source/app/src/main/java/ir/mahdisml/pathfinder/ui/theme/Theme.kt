package ir.mahdisml.pathfinder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import ir.mahdisml.pathfinder.Colors

private val DarkColorPalette = darkColors(
        primary = Colors.Main,
        primaryVariant = Colors.Main,
        secondary = Colors.Main
)

private val LightColorPalette = lightColors(
        primary = Colors.Main,
        primaryVariant = Colors.Main,
        secondary = Colors.Main

        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun PathFinderTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}