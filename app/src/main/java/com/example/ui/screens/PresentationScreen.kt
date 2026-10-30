package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LinkScreen
import com.example.model.LinkUiState
import com.example.model.PointerVisualMode
import com.example.model.ScreenBlankMode
import com.example.ui.components.GradientCircularRing
import com.example.ui.components.GradientLevelSlider
import com.example.ui.components.GradientPillButton
import com.example.ui.components.LinkSurfaceCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.StateFeedbackBanner
import com.example.ui.theme.LocalLinkPalette
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun PresentationScreen(
    uiState: LinkUiState,
    onNextSlide: () -> Unit,
    onPreviousSlide: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSelectPointerMode: (PointerVisualMode) -> Unit,
    onUpdateLaserPosition: (Float, Float, Boolean) -> Unit,
    onUpdatePointerSize: (Float) -> Unit,
    onToggleBlankMode: (ScreenBlankMode) -> Unit,
    onToggleSlideshowF5: () -> Unit,
    onOpenRemoteTrackpad: () -> Unit,
    onToggleConnection: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onNavigateToScreen: (LinkScreen) -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    val pres = uiState.presentationState
    val inkTrailPoints = remember { mutableStateListOf<Offset>() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("presentation_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
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

        // 1. Presentation Timer Ring & Slide Deck Progress Card (inspired by reference image circular ring cards)
        item {
            LinkSurfaceCard(
                useSubtleGradientGlow = true,
                borderColor = palette.primary.copy(alpha = 0.45f),
                modifier = Modifier.testTag("presentation_timer_hero_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.presentation_timer_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = palette.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pres.deckTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (pres.isSlideshowActive) StatusConnected.copy(alpha = 0.18f) else palette.surfaceElevated,
                        border = BorderStroke(1.dp, if (pres.isSlideshowActive) StatusConnected else palette.border),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onToggleSlideshowF5() }
                            .testTag("presentation_f5_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Slideshow,
                                contentDescription = null,
                                tint = if (pres.isSlideshowActive) StatusConnected else TextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (pres.isSlideshowActive) "LIVE (F5)" else "START F5",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Circular Gradient Timer Ring
                    val timerProgress = (pres.elapsedSeconds.toFloat() / pres.targetDurationSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
                    GradientCircularRing(
                        progress = timerProgress,
                        centerTitle = formatTimerMinutes(pres.elapsedSeconds),
                        centerSubtitle = "ELAPSED",
                        size = 138.dp,
                        strokeWidth = 11.dp
                    )

                    // Slide Progress & Timer Controls Column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = palette.background.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, palette.border)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${pres.currentSlide} / ${pres.totalSlides}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "CURRENT SLIDE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.secondary
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GradientPillButton(
                                text = if (pres.isTimerRunning) "Pause" else "Resume",
                                onClick = onToggleTimer,
                                icon = {
                                    Icon(
                                        imageVector = if (pres.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.height(42.dp),
                                testTag = "presentation_timer_toggle_btn"
                            )

                            IconButton(
                                onClick = onResetTimer,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(palette.surfaceElevated)
                                    .border(1.dp, palette.border, CircleShape)
                                    .testTag("presentation_timer_reset_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay,
                                    contentDescription = "Reset Timer",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Extra-Large Interactive Laser Pointer & Spotlight Pad
        item {
            LinkSurfaceCard(
                borderColor = palette.primary.copy(alpha = 0.55f),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.testTag("presentation_laser_pad_card")
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
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.presentation_pointer_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${(pres.pointerNormX * 1920).roundToInt()}, ${(pres.pointerNormY * 1080).roundToInt()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.secondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pointer Mode Selector Pills (Laser Beam / Spotlight / Ink Trail)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PointerVisualMode.entries.forEach { mode ->
                        val isSelected = pres.pointerMode == mode
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (isSelected) palette.primary.copy(alpha = 0.24f) else palette.surfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(999.dp))
                                .clickable {
                                    if (mode != PointerVisualMode.HIGHLIGHT_PEN) {
                                        inkTrailPoints.clear()
                                    }
                                    onSelectPointerMode(mode)
                                }
                                .testTag("pointer_mode_${mode.name.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 9.dp)
                            ) {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) TextPrimary else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Large 350.dp Interactive Laser Pointer & Slide Stage Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(palette.background)
                        .border(
                            width = 1.5.dp,
                            brush = palette.primaryGradient,
                            shape = RoundedCornerShape(22.dp)
                        )
                        .pointerInput(pres.pointerMode) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val nx = (offset.x / size.width).coerceIn(0f, 1f)
                                    val ny = (offset.y / size.height).coerceIn(0f, 1f)
                                    if (pres.pointerMode == PointerVisualMode.HIGHLIGHT_PEN) {
                                        inkTrailPoints.add(Offset(nx, ny))
                                    }
                                    onUpdateLaserPosition(nx, ny, true)
                                    tryAwaitRelease()
                                    onUpdateLaserPosition(nx, ny, false)
                                }
                            )
                        }
                        .pointerInput(pres.pointerMode) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val nx = (offset.x / size.width).coerceIn(0f, 1f)
                                    val ny = (offset.y / size.height).coerceIn(0f, 1f)
                                    if (pres.pointerMode == PointerVisualMode.HIGHLIGHT_PEN) {
                                        inkTrailPoints.clear()
                                        inkTrailPoints.add(Offset(nx, ny))
                                    }
                                    onUpdateLaserPosition(nx, ny, true)
                                },
                                onDragEnd = {
                                    onUpdateLaserPosition(pres.pointerNormX, pres.pointerNormY, false)
                                },
                                onDragCancel = {
                                    onUpdateLaserPosition(pres.pointerNormX, pres.pointerNormY, false)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val nx = (change.position.x / size.width).coerceIn(0f, 1f)
                                    val ny = (change.position.y / size.height).coerceIn(0f, 1f)
                                    if (pres.pointerMode == PointerVisualMode.HIGHLIGHT_PEN) {
                                        inkTrailPoints.add(Offset(nx, ny))
                                    }
                                    onUpdateLaserPosition(nx, ny, true)
                                }
                            )
                        }
                        .testTag("presentation_laser_stage_canvas")
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Simulated 16:9 Slide Wireframe Background on Stage
                        val pad = 22.dp.toPx()
                        drawRoundRect(
                            color = palette.surface.copy(alpha = 0.7f),
                            topLeft = Offset(pad, pad),
                            size = Size(w - pad * 2, h - pad * 2),
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                        )

                        // Simulated slide header & chart bars for visual context
                        drawRoundRect(
                            color = palette.surfaceElevated,
                            topLeft = Offset(pad * 2, pad * 2),
                            size = Size(w * 0.48f, 14.dp.toPx()),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                        val barCount = 5
                        val chartBaseY = h - pad * 2.2f
                        for (i in 0 until barCount) {
                            val barH = (h * 0.16f) + (i % 3) * (h * 0.08f)
                            val bx = pad * 2 + i * (w * 0.13f)
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        palette.secondary.copy(alpha = 0.45f),
                                        palette.primary.copy(alpha = 0.25f)
                                    )
                                ),
                                topLeft = Offset(bx, chartBaseY - barH),
                                size = Size(22.dp.toPx(), barH),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }

                        val px = (pres.pointerNormX * w).coerceIn(24f, w - 24f)
                        val py = (pres.pointerNormY * h).coerceIn(24f, h - 24f)
                        val pointerCenter = Offset(px, py)
                        val scale = pres.pointerSize

                        when (pres.pointerMode) {
                            PointerVisualMode.LASER_DOT -> {
                                // Glowing multi-ring laser beam
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            palette.primary.copy(alpha = 0.55f),
                                            palette.secondary.copy(alpha = 0.18f),
                                            Color.Transparent
                                        ),
                                        center = pointerCenter,
                                        radius = 56.dp.toPx() * scale
                                    ),
                                    radius = 56.dp.toPx() * scale,
                                    center = pointerCenter
                                )
                                drawCircle(
                                    color = palette.primary,
                                    radius = 14.dp.toPx() * scale,
                                    center = pointerCenter
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 6.dp.toPx() * scale,
                                    center = pointerCenter
                                )
                            }

                            PointerVisualMode.SPOTLIGHT -> {
                                // Dim surrounding stage and highlight spotlight circle
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.52f),
                                    size = size
                                )
                                val spotRadius = 64.dp.toPx() * scale
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            palette.secondary.copy(alpha = 0.38f),
                                            palette.primary.copy(alpha = 0.12f),
                                            Color.Transparent
                                        ),
                                        center = pointerCenter,
                                        radius = spotRadius * 1.25f
                                    ),
                                    radius = spotRadius * 1.25f,
                                    center = pointerCenter
                                )
                                drawCircle(
                                    brush = Brush.linearGradient(listOf(palette.primary, palette.secondary)),
                                    radius = spotRadius,
                                    center = pointerCenter,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }

                            PointerVisualMode.HIGHLIGHT_PEN -> {
                                if (inkTrailPoints.size > 1) {
                                    val path = Path().apply {
                                        val first = inkTrailPoints.first()
                                        moveTo(first.x * w, first.y * h)
                                        for (pt in inkTrailPoints.drop(1)) {
                                            lineTo(pt.x * w, pt.y * h)
                                        }
                                    }
                                    drawPath(
                                        path = path,
                                        brush = Brush.linearGradient(listOf(palette.primary, palette.secondary)),
                                        style = Stroke(width = 6.dp.toPx() * scale, cap = StrokeCap.Round)
                                    )
                                }
                                drawCircle(
                                    color = palette.secondary,
                                    radius = 10.dp.toPx() * scale,
                                    center = pointerCenter
                                )
                            }
                        }
                    }

                    // Slide watermark & hint overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = palette.background.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, palette.border)
                        ) {
                            Text(
                                text = "SLIDE ${pres.currentSlide} • ${pres.blankMode.label.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.secondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (pres.pointerMode == PointerVisualMode.HIGHLIGHT_PEN && inkTrailPoints.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceElevated,
                                modifier = Modifier.clickable { inkTrailPoints.clear() }
                            ) {
                                Text(
                                    text = "Clear Ink",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Touch & drag anywhere on this stage to project ${pres.pointerMode.label} on Windows 11",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Smooth Gradient Slider for Laser / Spotlight Beam Size
                GradientLevelSlider(
                    label = "Beam & Spotlight Radius",
                    valueText = "${(pres.pointerSize * 100).roundToInt()}%",
                    progress = ((pres.pointerSize - 0.6f) / 1.9f).coerceIn(0f, 1f),
                    onProgressChange = { frac ->
                        onUpdatePointerSize(0.6f + frac * 1.9f)
                    }
                )
            }
        }

        // 3. Tactile Slide Navigation Deck + Screen Blank Controls
        item {
            LinkSurfaceCard(
                modifier = Modifier.testTag("presentation_slide_controls_card")
            ) {
                SectionHeaderRow(
                    title = stringResource(R.string.presentation_slide_controls),
                    actionLabel = "Switch to Trackpad",
                    onActionClick = onOpenRemoteTrackpad
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Extra-Large Previous & Next Slide Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onPreviousSlide,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.surfaceElevated),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
                            .testTag("presentation_prev_slide_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Slide",
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prev Slide",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(palette.primaryGradient)
                            .clickable(onClick = onNextSlide)
                            .testTag("presentation_next_slide_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Next Slide",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Slide",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Blackout (B), Whiteout (W), and Trackpad Switch Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onToggleBlankMode(ScreenBlankMode.BLACK) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (pres.blankMode == ScreenBlankMode.BLACK) palette.primary else palette.border
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("presentation_blackout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = if (pres.blankMode == ScreenBlankMode.BLACK) palette.primary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Black (B)", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    }

                    OutlinedButton(
                        onClick = { onToggleBlankMode(ScreenBlankMode.WHITE) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (pres.blankMode == ScreenBlankMode.WHITE) palette.secondary else palette.border
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("presentation_whiteout_btn")
                    ) {
                        Text("White (W)", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    }

                    OutlinedButton(
                        onClick = onOpenRemoteTrackpad,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = palette.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Trackpad", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Speaker Notes Preview
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.background,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CoPresent,
                                contentDescription = null,
                                tint = palette.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "SPEAKER NOTES • SLIDE ${pres.currentSlide}",
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.secondary
                            )
                        }
                        val noteText = pres.speakerNotes[(pres.currentSlide - 1) % pres.speakerNotes.size]
                        Text(
                            text = noteText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatTimerMinutes(totalSeconds: Int): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return "%02d:%02d".format(mins, secs)
}
