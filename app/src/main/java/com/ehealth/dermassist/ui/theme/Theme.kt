package com.ehealth.dermassist.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryGreen,
        secondary = PrimaryBlue,
        tertiary = PillPurple,
        background = Color(0xFF1A1C1E),
        surface = Color(0xFF1A1C1E),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryGreen,
        onPrimary = Color.White,
        primaryContainer = SecondaryGreen,
        onPrimaryContainer = PrimaryGreen,
        secondary = PrimaryBlue,
        onSecondary = Color.White,
        secondaryContainer = IconBgBlue,
        onSecondaryContainer = PrimaryBlue,
        tertiary = PillPurple,
        onTertiary = Color.White,
        tertiaryContainer = IconBgPurple,
        onTertiaryContainer = PillPurple,
        error = ErrorRed,
        onError = Color.White,
        errorContainer = IconBgRed,
        onErrorContainer = ErrorRed,
        background = BackgroundWhite,
        onBackground = DarkText,
        surface = SurfaceLight,
        onSurface = DarkText,
        surfaceVariant = BackgroundGradientStart,
        onSurfaceVariant = BodyText,
        outline = BorderColor,
        outlineVariant = SecondaryGreenBorder,
        inverseSurface = IconBgOrange,
        inverseOnSurface = BadgeOrangeText,
        inversePrimary = IconBgGreen,
    )

val MaterialTheme.dimens: Dimens
    @Composable @ReadOnlyComposable get() = LocalDimens.current

@Composable
fun DermAssistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    CompositionLocalProvider(LocalDimens provides Dimens()) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
