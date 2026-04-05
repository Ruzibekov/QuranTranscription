package com.ruzibekov.quran.transcription.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreenContainer,
    onPrimaryContainer = PrimaryGreenDark,
    secondary = SecondaryAmber,
    onSecondary = OnSecondaryAmber,
    secondaryContainer = SecondaryAmberContainer,
    onSecondaryContainer = OnSecondaryAmber,
    tertiary = PrimaryGreenBright,
    onTertiary = Color.White,
    tertiaryContainer = PrimaryGreenContainer,
    onTertiaryContainer = PrimaryGreenDark,
    background = BackgroundColor,
    onBackground = TextPrimaryColor,
    surface = SurfaceColor,
    onSurface = TextPrimaryColor,
    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = TextSecondaryColor,
    outline = OutlineColor,
    outlineVariant = OutlineColor.copy(alpha = 0.3f),
    scrim = Color(0x73000000),
    inverseSurface = Color(0xFF25302B),
    inverseOnSurface = Color(0xFFDDECE6),
    inversePrimary = PrimaryGreenBright,
    surfaceTint = PrimaryGreen,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    onPrimary = Color(0xFF003028),
    primaryContainer = DarkPrimaryGreenContainer,
    onPrimaryContainer = Color(0xFF8AF0DA),
    secondary = SecondaryAmber,
    onSecondary = DarkOnSecondaryAmber,
    secondaryContainer = DarkSecondaryAmberLight,
    onSecondaryContainer = DarkOnSecondaryAmber,
    tertiary = DarkPrimaryGreen,
    onTertiary = Color(0xFF003028),
    tertiaryContainer = DarkPrimaryGreenContainer,
    onTertiaryContainer = Color(0xFF8AF0DA),
    background = DarkBackgroundColor,
    onBackground = DarkTextPrimaryColor,
    surface = DarkSurfaceColor,
    onSurface = DarkTextPrimaryColor,
    surfaceVariant = DarkSurfaceVariantColor,
    onSurfaceVariant = DarkTextSecondaryColor,
    outline = DarkOutlineColor,
    outlineVariant = DarkOutlineColor.copy(alpha = 0.3f),
    scrim = Color(0xCC000000),
    inverseSurface = Color(0xFFDDECE6),
    inverseOnSurface = Color(0xFF25302B),
    inversePrimary = PrimaryGreen,
    surfaceTint = DarkPrimaryGreen,
)

@Composable
fun QuranTranscriptionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
