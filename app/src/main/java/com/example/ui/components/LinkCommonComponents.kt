package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.LinkScreen
import com.example.model.PrototypeStateMode
import com.example.model.ThemeConfigState
import com.example.ui.theme.CuratedThemePalettes
import com.example.ui.theme.LocalLinkPalette
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusConnectedContainer
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorContainer
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun PrototypeStateSwitcherBar(
    currentMode: PrototypeStateMode,
    onSelectMode: (PrototypeStateMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    Surface(
        color = palette.surface.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier
            .fillMaxWidth()
            .testTag("prototype_state_switcher_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(palette.primaryGradient)
                    )
                    Text(
                        text = stringResource(R.string.state_switcher_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.secondary
                    )
                }
                Text(
                    text = "UI Prototype • No live socket",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrototypeStateMode.entries.forEach { mode ->
                    val isSelected = mode == currentMode
                    val accentColor = when (mode) {
                        PrototypeStateMode.CONNECTED -> palette.primary
                        PrototypeStateMode.LOADING -> StatusWarning
                        PrototypeStateMode.DISCONNECTED -> TextSecondary
                        PrototypeStateMode.EMPTY -> palette.secondary
                        PrototypeStateMode.ERROR -> StatusError
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.22f) else palette.surfaceElevated,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) accentColor else palette.border
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onSelectMode(mode) }
                            .testTag("state_chip_${mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) accentColor else TextMuted)
                            )
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LinkTopBar(
    currentScreen: LinkScreen,
    stateMode: PrototypeStateMode,
    deviceName: String,
    onNavigateBack: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenThemeStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    Surface(
        color = palette.background,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (currentScreen != LinkScreen.HOME) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
                            .testTag("nav_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back),
                            tint = TextPrimary
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentScreen == LinkScreen.HOME) "Link" else currentScreen.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (currentScreen == LinkScreen.HOME) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(palette.primaryGradient)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PRO STUDIO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = if (currentScreen == LinkScreen.HOME) {
                            "Galaxy S24 Ultra ↔ $deviceName"
                        } else {
                            "$deviceName • Private LAN Bridge"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConnectionStatusPill(stateMode = stateMode)

                // Theme Color Studio Button
                IconButton(
                    onClick = onOpenThemeStudio,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.surfaceElevated)
                        .border(1.dp, palette.primary.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                        .testTag("top_bar_theme_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = stringResource(R.string.cd_open_theme_studio),
                        tint = palette.secondary
                    )
                }

                if (currentScreen != LinkScreen.SECURITY) {
                    IconButton(
                        onClick = onOpenSecurity,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
                            .testTag("top_bar_security_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = stringResource(R.string.cd_open_security),
                            tint = palette.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionStatusPill(
    stateMode: PrototypeStateMode,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    val (bgColor, borderColor, dotColor, label) = when (stateMode) {
        PrototypeStateMode.CONNECTED -> Quad(
            StatusConnectedContainer.copy(alpha = 0.55f),
            StatusConnected.copy(alpha = 0.65f),
            StatusConnected,
            "Live"
        )
        PrototypeStateMode.LOADING -> Quad(
            StatusWarningContainer.copy(alpha = 0.55f),
            StatusWarning.copy(alpha = 0.65f),
            StatusWarning,
            "Sync…"
        )
        PrototypeStateMode.DISCONNECTED -> Quad(
            palette.surfaceElevated,
            palette.border,
            TextMuted,
            "Offline"
        )
        PrototypeStateMode.EMPTY -> Quad(
            palette.primary.copy(alpha = 0.18f),
            palette.secondary.copy(alpha = 0.5f),
            palette.secondary,
            "Empty"
        )
        PrototypeStateMode.ERROR -> Quad(
            StatusErrorContainer.copy(alpha = 0.65f),
            StatusError.copy(alpha = 0.7f),
            StatusError,
            "Error"
        )
    }

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.testTag("connection_status_pill")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun LinkSurfaceCard(
    modifier: Modifier = Modifier,
    containerColor: Color = LocalLinkPalette.current.surface,
    borderColor: Color = LocalLinkPalette.current.border,
    useSubtleGradientGlow: Boolean = false,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalLinkPalette.current
    val shape = RoundedCornerShape(22.dp)
    val clickableModifier = if (onClick != null) {
        Modifier
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        Modifier
    }

    Surface(
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.then(clickableModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (useSubtleGradientGlow) {
                        Modifier.background(palette.verticalCardGlow)
                    } else {
                        Modifier
                    }
                )
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Circular Gradient Ring gauge inspired directly by the circular steps/timer/heart rings in the reference image.
 */
@Composable
fun GradientCircularRing(
    progress: Float,
    centerTitle: String,
    centerSubtitle: String,
    modifier: Modifier = Modifier,
    size: Dp = 148.dp,
    strokeWidth: Dp = 12.dp
) {
    val palette = LocalLinkPalette.current
    val clamped = progress.coerceIn(0f, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)

            // Track circle
            drawArc(
                color = palette.surfaceElevated,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Glowing outer halo
            drawArc(
                brush = Brush.linearGradient(listOf(palette.primary.copy(alpha = 0.35f), palette.secondary.copy(alpha = 0.35f))),
                startAngle = -90f,
                sweepAngle = 360f * clamped,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx * 1.45f, cap = StrokeCap.Round)
            )

            // Main gradient progress arc
            drawArc(
                brush = Brush.linearGradient(listOf(palette.primary, palette.secondary)),
                startAngle = -90f,
                sweepAngle = 360f * clamped,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = centerTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = centerSubtitle.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

/**
 * Smooth Gradient Level Slider Bar inspired by the "Levels" card in the bottom-right of the reference image.
 */
@Composable
fun GradientLevelSlider(
    label: String,
    valueText: String,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    val clamped = progress.coerceIn(0f, 1f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelMedium,
                color = palette.secondary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        onProgressChange(fraction)
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        onProgressChange(fraction)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                val trackHeight = 10.dp.toPx()
                val yOffset = (size.height - trackHeight) / 2f
                val activeWidth = (size.width * clamped).coerceAtLeast(trackHeight)

                // Inactive dark rounded track
                drawRoundRect(
                    color = palette.background,
                    topLeft = Offset(0f, yOffset),
                    size = Size(size.width, trackHeight),
                    cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
                )

                // Active gradient fill bar
                drawRoundRect(
                    brush = Brush.horizontalGradient(listOf(palette.primary, palette.secondary)),
                    topLeft = Offset(0f, yOffset),
                    size = Size(activeWidth, trackHeight),
                    cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
                )

                // Glowing thumb circle
                val thumbCenter = Offset(
                    x = activeWidth.coerceIn(trackHeight / 2f, size.width - trackHeight / 2f),
                    y = size.height / 2f
                )
                drawCircle(
                    color = palette.secondary.copy(alpha = 0.32f),
                    radius = 12.dp.toPx(),
                    center = thumbCenter
                )
                drawCircle(
                    color = Color.White,
                    radius = 8.dp.toPx(),
                    center = thumbCenter
                )
                drawCircle(
                    color = palette.primary,
                    radius = 5.dp.toPx(),
                    center = thumbCenter
                )
            }
        }
    }
}

@Composable
fun GradientPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    testTag: String? = null
) {
    val palette = LocalLinkPalette.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(palette.primaryGradient)
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                icon()
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Interactive Theme Color Studio Modal allowing the user to switch between curated gradient themes
 * (including the reference Neon Sunset palette) or dial in any custom Primary + Secondary Hue!
 */
@Composable
fun ThemeColorStudioDialog(
    themeConfig: ThemeConfigState,
    onSelectPreset: (String) -> Unit,
    onUpdatePrimaryHue: (Float) -> Unit,
    onUpdateSecondaryHue: (Float) -> Unit,
    onToggleDeepOled: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalLinkPalette.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("theme_studio_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.verticalCardGlow)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = palette.secondary
                        )
                        Column {
                            Text(
                                text = "Theme Color Studio",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary
                            )
                            Text(
                                text = "Customize gradients, accents & dark surfaces",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Theme Studio",
                            tint = TextSecondary
                        )
                    }
                }

                Text(
                    text = "CURATED GRADIENT PALETTES",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CuratedThemePalettes.forEach { preset ->
                        val isSelected = !themeConfig.useCustomHues && themeConfig.selectedPresetId == preset.id
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) palette.surfaceElevated else palette.background.copy(alpha = 0.65f),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) preset.primary else palette.border
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onSelectPreset(preset.id) }
                                .testTag("theme_preset_${preset.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(listOf(preset.primary, preset.secondary)))
                                    )
                                    Text(
                                        text = preset.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = preset.secondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom Freeform Hue Sliders
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = palette.background.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CUSTOM COLOR FREEDOM (HSL)",
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.secondary
                            )
                            Text(
                                text = "${themeConfig.customPrimaryHue.roundToInt()}° / ${themeConfig.customSecondaryHue.roundToInt()}°",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        Text(
                            text = "Primary Accent Hue",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Slider(
                            value = themeConfig.customPrimaryHue,
                            onValueChange = onUpdatePrimaryHue,
                            valueRange = 0f..360f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = palette.primary,
                                inactiveTrackColor = palette.surfaceElevated
                            ),
                            modifier = Modifier.height(24.dp)
                        )

                        Text(
                            text = "Secondary Gradient Hue",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Slider(
                            value = themeConfig.customSecondaryHue,
                            onValueChange = onUpdateSecondaryHue,
                            valueRange = 0f..360f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = palette.secondary,
                                inactiveTrackColor = palette.surfaceElevated
                            ),
                            modifier = Modifier.height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Deep Pitch-Black AMOLED Surface",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                            Switch(
                                checked = themeConfig.deepOledDark,
                                onCheckedChange = { onToggleDeepOled() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = palette.primary
                                )
                            )
                        }
                    }
                }

                GradientPillButton(
                    text = "Done & Apply Theme",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "theme_studio_done_button"
                )
            }
        }
    }
}

@Composable
fun SectionHeaderRow(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = TextSecondary
        )
        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = palette.secondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun StateFeedbackBanner(
    stateMode: PrototypeStateMode,
    bannerMessage: String?,
    onReconnect: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onOpenSecurity: () -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when (stateMode) {
            PrototypeStateMode.LOADING -> {
                LinkSurfaceCard(
                    containerColor = StatusWarningContainer.copy(alpha = 0.35f),
                    borderColor = StatusWarning.copy(alpha = 0.6f),
                    modifier = Modifier.testTag("state_banner_loading")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp,
                            color = StatusWarning
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Establishing Encrypted LAN Session…",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Verifying TLS 1.3 certificate pin with WIN11-WORKSTATION (192.168.1.42:9443)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            PrototypeStateMode.DISCONNECTED -> {
                LinkSurfaceCard(
                    containerColor = palette.surfaceElevated,
                    borderColor = palette.border,
                    modifier = Modifier.testTag("state_banner_disconnected")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Disconnected",
                            tint = TextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Windows 11 Laptop Disconnected",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Connect over local Wi-Fi to enable file transfers, touchpad input, and presentation pointer.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onReconnect,
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primary),
                            modifier = Modifier.testTag("banner_reconnect_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Connect Now")
                        }
                        OutlinedButton(
                            onClick = onOpenSecurity,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.testTag("banner_security_button")
                        ) {
                            Text("Pairing Settings", color = TextPrimary)
                        }
                    }
                }
            }

            PrototypeStateMode.EMPTY -> {
                LinkSurfaceCard(
                    containerColor = palette.primary.copy(alpha = 0.14f),
                    borderColor = palette.secondary.copy(alpha = 0.45f),
                    modifier = Modifier.testTag("state_banner_empty")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "Empty State",
                            tint = palette.secondary,
                            modifier = Modifier.size(26.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Empty Prototype State Active",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Transfer queues, media sessions, and clipboard logs are cleared so you can inspect zero-states.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        OutlinedButton(
                            onClick = onRestoreSampleData,
                            border = BorderStroke(1.dp, palette.secondary),
                            modifier = Modifier.testTag("banner_restore_sample_button")
                        ) {
                            Text("Load Mock Data", color = palette.secondary)
                        }
                    }
                }
            }

            PrototypeStateMode.ERROR -> {
                LinkSurfaceCard(
                    containerColor = StatusErrorContainer.copy(alpha = 0.45f),
                    borderColor = StatusError.copy(alpha = 0.75f),
                    modifier = Modifier.testTag("state_banner_error")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Connection Error",
                            tint = StatusError,
                            modifier = Modifier.size(26.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Companion Transport Error (Port 9443)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Simulated socket timeout to WIN11-WORKSTATION. Check Windows Firewall or retry handshake.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onReconnect,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                            modifier = Modifier.testTag("banner_retry_error_button")
                        ) {
                            Text("Retry Handshake")
                        }
                        OutlinedButton(
                            onClick = onOpenSecurity,
                            border = BorderStroke(1.dp, StatusError.copy(alpha = 0.5f))
                        ) {
                            Text("Inspect Diagnostics", color = TextPrimary)
                        }
                    }
                }
            }

            PrototypeStateMode.CONNECTED -> Unit
        }

        AnimatedVisibility(
            visible = bannerMessage != null && stateMode == PrototypeStateMode.CONNECTED,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (bannerMessage != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surfaceElevated,
                    border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transient_status_toast")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusConnected,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = bannerMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                        IconButton(
                            onClick = onDismissMessage,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss notification",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
