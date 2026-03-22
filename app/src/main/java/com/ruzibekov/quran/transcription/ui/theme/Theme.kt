package com.ruzibekov.quran.transcription.ui.theme

import androidx.compose.material3.MaterialTheme
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

@Composable
fun QuranTranscriptionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
