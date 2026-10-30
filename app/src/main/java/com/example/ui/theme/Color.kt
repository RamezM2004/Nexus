package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Base Velvet Dark Surfaces (inspired by the reference UI kit)
val CharcoalBackground = Color(0xFF14141F)
val CharcoalSurface = Color(0xFF1E1E2E)
val CharcoalSurfaceElevated = Color(0xFF27273A)
val CharcoalSurfaceHigh = Color(0xFF313147)
val HairlineBorder = Color(0xFF34344A)
val HairlineBorderHighlight = Color(0xFF474766)

// Default Accents
val CobaltPrimary = Color(0xFFEC4899)
val CobaltPrimaryBright = Color(0xFFF472B6)
val CobaltPrimaryContainer = Color(0xFF3B1936)
val OnCobaltPrimaryContainer = Color(0xFFFCE7F3)

val CyanAccent = Color(0xFFF59E0B)
val CyanAccentContainer = Color(0xFF452708)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFFA3ADC2)
val TextMuted = Color(0xFF6E7891)

val StatusConnected = Color(0xFF10B981)
val StatusConnectedContainer = Color(0xFF064E3B)
val StatusWarning = Color(0xFFF59E0B)
val StatusWarningContainer = Color(0xFF451A03)
val StatusError = Color(0xFFEF4444)
val StatusErrorContainer = Color(0xFF450A0A)

@Immutable
data class LinkThemePalette(
    val id: String,
    val name: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color
) {
    val primaryGradient: Brush
        get() = Brush.linearGradient(listOf(primary, secondary))

    val verticalCardGlow: Brush
        get() = Brush.verticalGradient(
            listOf(
                primary.copy(alpha = 0.20f),
                secondary.copy(alpha = 0.06f),
                surface
            )
        )

    val ringGradient: Brush
        get() = Brush.sweepGradient(
            listOf(primary, secondary, primary)
        )
}

val CuratedThemePalettes = listOf(
    // 1. Matches the attached reference image: Velvet Dark with Magenta/Purple -> Warm Amber/Orange gradients
    LinkThemePalette(
        id = "neon_sunset",
        name = "Pulse Sunset (Reference)",
        primary = Color(0xFFE11D8F),
        secondary = Color(0xFFF59E0B),
        tertiary = Color(0xFFA855F7),
        background = Color(0xFF151421),
        surface = Color(0xFF201F30),
        surfaceElevated = Color(0xFF2A283E),
        border = Color(0xFF36344E)
    ),
    // 2. Electric Cobalt & Cyan
    LinkThemePalette(
        id = "electric_cobalt",
        name = "Electric Cobalt",
        primary = Color(0xFF3B82F6),
        secondary = Color(0xFF38BDF8),
        tertiary = Color(0xFF6366F1),
        background = Color(0xFF0E1017),
        surface = Color(0xFF161924),
        surfaceElevated = Color(0xFF1F2433),
        border = Color(0xFF2B3245)
    ),
    // 3. Cyber Emerald & Mint
    LinkThemePalette(
        id = "cyber_emerald",
        name = "Cyber Emerald",
        primary = Color(0xFF10B981),
        secondary = Color(0xFF06B6D4),
        tertiary = Color(0xFF34D399),
        background = Color(0xFF0D1514),
        surface = Color(0xFF152220),
        surfaceElevated = Color(0xFF1E302D),
        border = Color(0xFF28423E)
    ),
    // 4. Aurora Violet & Rose
    LinkThemePalette(
        id = "aurora_violet",
        name = "Aurora Violet",
        primary = Color(0xFF8B5CF6),
        secondary = Color(0xFFEC4899),
        tertiary = Color(0xFFC084FC),
        background = Color(0xFF13111C),
        surface = Color(0xFF1C1929),
        surfaceElevated = Color(0xFF262238),
        border = Color(0xFF352F4D)
    ),
    // 5. Crimson Laser & Gold
    LinkThemePalette(
        id = "crimson_laser",
        name = "Crimson Laser",
        primary = Color(0xFFF43F5E),
        secondary = Color(0xFFFB923C),
        tertiary = Color(0xFFFBBF24),
        background = Color(0xFF171114),
        surface = Color(0xFF241A1F),
        surfaceElevated = Color(0xFF302229),
        border = Color(0xFF45303A)
    )
)

fun buildCustomHslPalette(
    primaryHue: Float,
    secondaryHue: Float,
    deepOledDark: Boolean
): LinkThemePalette {
    val primary = Color.hsl(primaryHue.coerceIn(0f, 360f), 0.85f, 0.58f)
    val secondary = Color.hsl(secondaryHue.coerceIn(0f, 360f), 0.90f, 0.56f)
    val tertiary = Color.hsl(((primaryHue + secondaryHue) / 2f) % 360f, 0.80f, 0.62f)
    return LinkThemePalette(
        id = "custom",
        name = "Custom Studio",
        primary = primary,
        secondary = secondary,
        tertiary = tertiary,
        background = if (deepOledDark) Color(0xFF09090F) else Color(0xFF151421),
        surface = if (deepOledDark) Color(0xFF12121C) else Color(0xFF201F30),
        surfaceElevated = if (deepOledDark) Color(0xFF1B1B29) else Color(0xFF2A283E),
        border = if (deepOledDark) Color(0xFF28283C) else Color(0xFF36344E)
    )
}

val LocalLinkPalette = staticCompositionLocalOf { CuratedThemePalettes.first() }
