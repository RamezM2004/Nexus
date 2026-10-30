package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LinkScreen
import com.example.model.LinkUiState
import com.example.model.PrototypeStateMode
import com.example.ui.components.GradientCircularRing
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
fun MediaScreen(
    uiState: LinkUiState,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onSwitchSource: (String, String) -> Unit,
    onToggleConnection: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onNavigateToScreen: (LinkScreen) -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    val media = uiState.mediaState
    val isSessionReady = media.isAvailable &&
        uiState.stateMode != PrototypeStateMode.EMPTY &&
        uiState.stateMode != PrototypeStateMode.DISCONNECTED

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("media_screen_list"),
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

        // Source Selector Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeaderRow(
                    title = stringResource(R.string.media_source_label)
                )

                val sources = listOf(
                    "YouTube Music" to "Windows 11 Media Session • Chrome PWA",
                    "Windows System Media" to "Global SMTC Transport • Master Audio",
                    "VLC Workstation" to "Local Media Player • DirectSound"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sources.forEach { (name, sub) ->
                        val isSelected = media.sourceApp == name
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (isSelected) palette.primary.copy(alpha = 0.22f) else palette.surface,
                            border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { onSwitchSource(name, sub) }
                                .testTag("media_source_${name.lowercase().replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = if (isSelected) palette.secondary else TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) TextPrimary else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!isSessionReady) {
            item {
                LinkSurfaceCard(
                    modifier = Modifier.testTag("media_empty_session_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "No Active Windows Media Playback",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Start playback in YouTube Music or any Windows 11 media player, or load a sample session below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Button(
                            onClick = onRestoreSampleData,
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primary),
                            modifier = Modifier.testTag("media_load_session_button")
                        ) {
                            Text("Load YouTube Music Session")
                        }
                    }
                }
            }
        } else {
            // Main Player Card with Circular Track Progress Ring + Spectrum Bars (inspired by reference image)
            item {
                LinkSurfaceCard(
                    useSubtleGradientGlow = true,
                    borderColor = palette.primary.copy(alpha = 0.55f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("media_player_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val trackFraction = (media.positionSeconds.toFloat() / media.durationSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
                        GradientCircularRing(
                            progress = trackFraction,
                            centerTitle = formatSeconds(media.positionSeconds),
                            centerSubtitle = "OF ${formatSeconds(media.durationSeconds)}",
                            size = 116.dp,
                            strokeWidth = 9.dp
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = palette.primary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = media.sourceApp.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.secondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = media.trackTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = media.artistName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = media.sourceSubtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vertical Gradient Bar Equalizer (matching the bar chart in the reference image)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        val barCount = 22
                        val spacing = 6.dp.toPx()
                        val barWidth = (size.width - spacing * (barCount - 1)) / barCount
                        val pattern = listOf(0.35f, 0.55f, 0.8f, 0.45f, 0.95f, 0.65f, 0.4f, 0.85f, 0.5f, 0.72f, 0.38f)
                        for (i in 0 until barCount) {
                            val baseRatio = pattern[i % pattern.size]
                            val ratio = if (media.isPlaying) baseRatio else 0.18f
                            val h = size.height * ratio
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    listOf(palette.secondary, palette.primary)
                                ),
                                topLeft = Offset(
                                    x = i * (barWidth + spacing),
                                    y = size.height - h
                                ),
                                size = Size(barWidth, h),
                                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Smooth Gradient Timeline Scrubber
                    GradientLevelSlider(
                        label = "Track Position",
                        valueText = "${formatSeconds(media.positionSeconds)} / ${formatSeconds(media.durationSeconds)}",
                        progress = (media.positionSeconds.toFloat() / media.durationSeconds.coerceAtLeast(1)).coerceIn(0f, 1f),
                        onProgressChange = { frac ->
                            onSeekTo((frac * media.durationSeconds).roundToInt())
                        },
                        modifier = Modifier.testTag("media_seek_slider")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Transport Controls (Previous / Play-Pause / Next)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onSkipPrevious,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(palette.surfaceElevated)
                                .border(1.dp, palette.border, RoundedCornerShape(18.dp))
                                .testTag("media_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Track",
                                tint = TextPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(palette.primaryGradient)
                                .testTag("media_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (media.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (media.isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        IconButton(
                            onClick = onSkipNext,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(palette.surfaceElevated)
                                .border(1.dp, palette.border, RoundedCornerShape(18.dp))
                                .testTag("media_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = TextPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }

        // Master Volume & Windows Audio Endpoint Card
        item {
            LinkSurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
                            .testTag("media_mute_button")
                    ) {
                        Icon(
                            imageVector = if (media.isMuted) {
                                Icons.AutoMirrored.Filled.VolumeOff
                            } else {
                                Icons.AutoMirrored.Filled.VolumeUp
                            },
                            contentDescription = "Toggle Mute",
                            tint = if (media.isMuted) TextMuted else palette.secondary
                        )
                    }

                    GradientLevelSlider(
                        label = stringResource(R.string.media_volume_label),
                        valueText = if (media.isMuted) "MUTED" else "${(media.volumeLevel * 100).roundToInt()}%",
                        progress = if (media.isMuted) 0f else media.volumeLevel,
                        onProgressChange = onVolumeChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("media_volume_slider")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surfaceElevated,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = StatusConnected,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Windows 11 Audio Output Endpoint",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = media.audioOutputDevice,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
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

private fun formatSeconds(totalSeconds: Int): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return "%d:%02d".format(mins, secs)
}
