package com.example.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

val LinkShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun LinkTheme(
    palette: LinkThemePalette = CuratedThemePalettes.first(),
    content: @Composable () -> Unit
) {
    val animPrimary by animateColorAsState(palette.primary, tween(280), label = "primary")
    val animSecondary by animateColorAsState(palette.secondary, tween(280), label = "secondary")
    val animTertiary by animateColorAsState(palette.tertiary, tween(280), label = "tertiary")
    val animBg by animateColorAsState(palette.background, tween(280), label = "bg")
    val animSurface by animateColorAsState(palette.surface, tween(280), label = "surface")
    val animElevated by animateColorAsState(palette.surfaceElevated, tween(280), label = "elevated")
    val animBorder by animateColorAsState(palette.border, tween(280), label = "border")

    val animatedPalette = palette.copy(
        primary = animPrimary,
        secondary = animSecondary,
        tertiary = animTertiary,
        background = animBg,
        surface = animSurface,
        surfaceElevated = animElevated,
        border = animBorder
    )

    val colorScheme = darkColorScheme(
        primary = animatedPalette.primary,
        onPrimary = Color.White,
        primaryContainer = animatedPalette.primary.copy(alpha = 0.22f),
        onPrimaryContainer = Color.White,
        secondary = animatedPalette.secondary,
        onSecondary = animatedPalette.background,
        secondaryContainer = animatedPalette.secondary.copy(alpha = 0.22f),
        onSecondaryContainer = Color.White,
        tertiary = animatedPalette.tertiary,
        onTertiary = animatedPalette.background,
        background = animatedPalette.background,
        onBackground = TextPrimary,
        surface = animatedPalette.surface,
        onSurface = TextPrimary,
        surfaceVariant = animatedPalette.surfaceElevated,
        onSurfaceVariant = TextSecondary,
        outline = animatedPalette.border,
        outlineVariant = HairlineBorderHighlight,
        error = StatusError,
        onError = Color.White,
        errorContainer = StatusErrorContainer,
        onErrorContainer = Color(0xFFFECACA)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    CompositionLocalProvider(LocalLinkPalette provides animatedPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = LinkShapes,
            content = content
        )
    }
}
