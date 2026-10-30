package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LinkScreen
import com.example.model.LinkUiState
import com.example.ui.components.GradientLevelSlider
import com.example.ui.components.LinkSurfaceCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.StateFeedbackBanner
import com.example.ui.theme.LocalLinkPalette
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun RemoteControlScreen(
    uiState: LinkUiState,
    onTrackpadMove: (Float, Float) -> Unit,
    onMouseButtonClick: (String) -> Unit,
    onVerticalScroll: (Float) -> Unit,
    onSensitivityChange: (Float) -> Unit,
    onToggleExpandedTrackpad: () -> Unit,
    onToggleModifier: (String) -> Unit,
    onSendShortcut: (String) -> Unit,
    onUpdateKeyboardDraft: (String) -> Unit,
    onSendKeyboardDraft: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onToggleMute: () -> Unit,
    onOpenFullMedia: () -> Unit,
    onOpenPresentationMode: () -> Unit,
    onToggleConnection: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onNavigateToScreen: (LinkScreen) -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    var localTouchPosition by remember { mutableStateOf<Offset?>(null) }
    val pointerTrail = remember { mutableStateListOf<Offset>() }

    // Extra-large pointer area: 410.dp when expanded (default), 310.dp when compact
    val trackpadHeight = if (uiState.remoteState.isExpandedTrackpad) 410.dp else 310.dp

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("remote_control_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StateFeedbackBanner(
                stateMode = uiState.stateMode,
                bannerMessage = uiState.statusBannerMessage,
                onReconnect = onToggleConnection,
                onRestoreSampleData = onRestoreSampleData,
                onOpenSecurity = { onNavigateToScreen(LinkScreen.SECURITY) },
                onDismissMessage = onDismissBanner
            )
        }

        // Mode Switcher Pill Bar: Precision Mouse Trackpad <-> Presentation & Laser Pointer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.primaryGradient)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "XL Trackpad & Keys",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = palette.surfaceElevated,
                    border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onOpenPresentationMode)
                        .testTag("remote_switch_to_presentation_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CoPresent,
                            contentDescription = null,
                            tint = palette.secondary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Presentation & Laser",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // 1. Extra-Large Responsive Multi-Touch Pointer Surface + Vertical Scroll Strip + Tactile Mouse Buttons
        item {
            LinkSurfaceCard(
                useSubtleGradientGlow = true,
                borderColor = palette.primary.copy(alpha = 0.5f),
                contentPadding = PaddingValues(16.dp)
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
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = palette.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.touchpad_label),
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                            Text(
                                text = "X:${uiState.remoteState.cursorX.roundToInt()}  Y:${uiState.remoteState.cursorY.roundToInt()} • 4K Desktop Map",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.secondary
                            )
                        }
                    }

                    // Expand / Compact Trackpad Height Toggle
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.surfaceElevated,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable(onClick = onToggleExpandedTrackpad)
                            .testTag("remote_toggle_trackpad_size_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.remoteState.isExpandedTrackpad) {
                                    Icons.Default.FullscreenExit
                                } else {
                                    Icons.Default.Fullscreen
                                },
                                contentDescription = "Toggle Trackpad Size",
                                tint = palette.secondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (uiState.remoteState.isExpandedTrackpad) "XL 410dp" else "Expand XL",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Enlarged Trackpad + Scrollbar Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackpadHeight)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Main Extra-Large Multi-Touch Trackpad Surface
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(22.dp))
                            .background(palette.background)
                            .border(
                                width = 1.5.dp,
                                brush = palette.primaryGradient,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { offset ->
                                        localTouchPosition = offset
                                        onMouseButtonClick("TAP_LEFT_CLICK")
                                    },
                                    onDoubleTap = { offset ->
                                        localTouchPosition = offset
                                        onMouseButtonClick("DOUBLE_CLICK")
                                    },
                                    onLongPress = { offset ->
                                        localTouchPosition = offset
                                        onMouseButtonClick("LONG_PRESS_RIGHT_CLICK")
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        localTouchPosition = offset
                                        pointerTrail.clear()
                                        pointerTrail.add(offset)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        localTouchPosition = change.position
                                        pointerTrail.add(change.position)
                                        if (pointerTrail.size > 16) {
                                            pointerTrail.removeAt(0)
                                        }
                                        onTrackpadMove(dragAmount.x, dragAmount.y)
                                    }
                                )
                            }
                            .testTag("remote_touchpad_surface")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridColor = palette.border.copy(alpha = 0.55f)
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                            // Precision rule-of-thirds and center crosshairs
                            drawLine(
                                color = gridColor,
                                start = Offset(size.width / 2f, 0f),
                                end = Offset(size.width / 2f, size.height),
                                strokeWidth = 1f,
                                pathEffect = dashEffect
                            )
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, size.height / 2f),
                                end = Offset(size.width, size.height / 2f),
                                strokeWidth = 1f,
                                pathEffect = dashEffect
                            )

                            // Smooth luminous gradient trail when gliding finger
                            if (pointerTrail.size > 1) {
                                for (i in 1 until pointerTrail.size) {
                                    val alpha = (i.toFloat() / pointerTrail.size) * 0.65f
                                    drawLine(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                palette.primary.copy(alpha = alpha),
                                                palette.secondary.copy(alpha = alpha)
                                            )
                                        ),
                                        start = pointerTrail[i - 1],
                                        end = pointerTrail[i],
                                        strokeWidth = (i * 1.1f).coerceAtMost(12f),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }

                            val pointerPos = localTouchPosition ?: Offset(size.width / 2f, size.height / 2f)
                            val clampedX = pointerPos.x.coerceIn(24f, size.width - 24f)
                            val clampedY = pointerPos.y.coerceIn(24f, size.height - 24f)
                            val center = Offset(clampedX, clampedY)

                            // Glowing radial aura around touch point
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        palette.primary.copy(alpha = 0.42f),
                                        palette.secondary.copy(alpha = 0.14f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = 58.dp.toPx()
                                ),
                                radius = 58.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = palette.secondary,
                                radius = 10.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 4.5.dp.toPx(),
                                center = center
                            )
                        }

                        Text(
                            text = stringResource(R.string.touchpad_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }

                    // Dedicated Vertical Scroll Strip
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(48.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(22.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, palette.border, RoundedCornerShape(22.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    onVerticalScroll(dragAmount.y)
                                }
                            }
                            .testTag("remote_scroll_strip")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Vertical Scroll Strip",
                                tint = palette.secondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "S\nC\nR\nO\nL\nL",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Left-Click, Scroll Wheel Click, and Right-Click Tactile Pill Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1.25f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(palette.primaryGradient)
                            .clickable { onMouseButtonClick("LEFT_CLICK") }
                            .testTag("btn_left_click")
                    ) {
                        Text(
                            text = stringResource(R.string.btn_left_click),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onMouseButtonClick("MIDDLE_CLICK") },
                        colors = ButtonDefaults.buttonColors(containerColor = palette.background),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(0.75f)
                            .height(54.dp)
                            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                            .testTag("btn_middle_click")
                    ) {
                        Text(
                            text = stringResource(R.string.btn_middle_click),
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.secondary
                        )
                    }

                    Button(
                        onClick = { onMouseButtonClick("RIGHT_CLICK") },
                        colors = ButtonDefaults.buttonColors(containerColor = palette.surfaceElevated),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.25f)
                            .height(54.dp)
                            .border(1.dp, palette.primary.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                            .testTag("btn_right_click")
                    ) {
                        Text(
                            text = stringResource(R.string.btn_right_click),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gradient Level Slider for Pointer Velocity (matching the reference UI kit)
                GradientLevelSlider(
                    label = "Pointer Velocity & Acceleration",
                    valueText = "${(uiState.remoteState.sensitivity * 100).roundToInt()}%",
                    progress = ((uiState.remoteState.sensitivity - 0.5f) / 2.5f).coerceIn(0f, 1f),
                    onProgressChange = { frac ->
                        onSensitivityChange(0.5f + frac * 2.5f)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.background,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TELEMETRY: ${uiState.remoteState.lastDispatchedCommand}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // 2. Keyboard & Shortcut Controls
        item {
            LinkSurfaceCard(
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = palette.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.section_keyboard_controls),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Modifier Keys Row (Win, Ctrl, Alt, Shift)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Win", "Ctrl", "Alt", "Shift").forEach { modKey ->
                        val isActive = modKey in uiState.remoteState.activeModifiers
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isActive) palette.primary else palette.surfaceElevated,
                            border = BorderStroke(1.dp, if (isActive) palette.secondary else palette.border),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onToggleModifier(modKey) }
                                .testTag("key_mod_${modKey.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = modKey,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isActive) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Shortcut Deck
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Esc", "Tab", "Alt+Tab", "Win+D", "Backspace", "Enter", "Ctrl+C", "Ctrl+V").forEach { keyLabel ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = palette.background,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { onSendShortcut(keyLabel) }
                                .testTag("key_shortcut_${keyLabel.lowercase()}")
                        ) {
                            Text(
                                text = keyLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Direct Keystroke Input Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.remoteState.keyboardDraft,
                        onValueChange = onUpdateKeyboardDraft,
                        placeholder = {
                            Text(
                                "Type text to send as Windows keystrokes…",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = palette.primary,
                            unfocusedBorderColor = palette.border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = palette.background,
                            unfocusedContainerColor = palette.background
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("remote_keyboard_input")
                    )

                    IconButton(
                        onClick = onSendKeyboardDraft,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.primaryGradient)
                            .testTag("remote_keyboard_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send keystrokes",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // 3. Separate Media-Control Panel inside Remote Control
        item {
            LinkSurfaceCard(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.testTag("remote_media_panel")
            ) {
                SectionHeaderRow(
                    title = stringResource(R.string.section_quick_media),
                    actionLabel = "Full Player",
                    onActionClick = onOpenFullMedia
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uiState.mediaState.isAvailable) {
                                uiState.mediaState.trackTitle
                            } else {
                                "No Media Session Active"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${uiState.mediaState.sourceApp} • ${(uiState.mediaState.volumeLevel * 100).roundToInt()}% Vol",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = onSkipPrevious,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(palette.surfaceElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Track",
                                tint = TextPrimary
                            )
                        }
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(palette.primaryGradient)
                                .testTag("remote_media_play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (uiState.mediaState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play or Pause",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = onSkipNext,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(palette.surfaceElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = TextPrimary
                            )
                        }
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(palette.surfaceElevated)
                        ) {
                            Icon(
                                imageVector = if (uiState.mediaState.isMuted) {
                                    Icons.AutoMirrored.Filled.VolumeOff
                                } else {
                                    Icons.AutoMirrored.Filled.VolumeUp
                                },
                                contentDescription = "Mute Volume",
                                tint = if (uiState.mediaState.isMuted) StatusConnected else palette.secondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
