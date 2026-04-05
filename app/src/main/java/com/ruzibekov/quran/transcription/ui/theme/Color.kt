package com.ruzibekov.quran.transcription.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

// Light theme colors
val PrimaryGreen = Color(0xFF0D6B5E)
val PrimaryGreenLight = Color(0xFF14917F)
val PrimaryGreenDark = Color(0xFF094D44)
val PrimaryGreenContainer = Color(0xFFE8F5F1)
val PrimaryGreenBright = Color(0xFF14917F)
val SecondaryAmber = Color(0xFFC9A84C)
val SecondaryAmberLight = Color(0xFFF5E6B8)
val SecondaryAmberContainer = Color(0xFFF5E6B8)
val OnSecondaryAmber = Color(0xFF9A7B2F)
val BackgroundColor = Color(0xFFF0F7F4)
val SurfaceColor = Color(0xFFFFFFFF)
val SurfaceVariantColor = Color(0xFFE8F5F1)
val OutlineColor = Color(0xFF8A9B93)
val TextPrimaryColor = Color(0xFF1A2B25)
val TextSecondaryColor = Color(0xFF5A6E65)
val TextMutedColor = Color(0xFF8A9B93)

// Dark theme colors
val DarkPrimaryGreen = Color(0xFF4EDBC5)
val DarkPrimaryGreenDark = Color(0xFF1A4A42)
val DarkPrimaryGreenContainer = Color(0xFF1A3D36)
val DarkSecondaryAmberLight = Color(0xFF3D3524)
val DarkOnSecondaryAmber = Color(0xFFD4B86A)
val DarkBackgroundColor = Color(0xFF0F1A17)
val DarkSurfaceColor = Color(0xFF1A2520)
val DarkSurfaceVariantColor = Color(0xFF243530)
val DarkOutlineColor = Color(0xFF5A6E65)
val DarkTextPrimaryColor = Color(0xFFE0EDE8)
val DarkTextSecondaryColor = Color(0xFFA5B5AD)
val DarkTextMutedColor = Color(0xFF6E8078)

val TextMutedColorComposable: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == DarkBackgroundColor)
        DarkTextMutedColor else TextMutedColor
