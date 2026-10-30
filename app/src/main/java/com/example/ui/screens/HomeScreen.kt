package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LaptopWindows
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FileTransferItem
import com.example.model.LinkScreen
import com.example.model.LinkUiState
import com.example.model.PrototypeStateMode
import com.example.model.TransferDirection
import com.example.model.TransferStatus
import com.example.ui.components.GradientCircularRing
import com.example.ui.components.LinkSurfaceCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.StateFeedbackBanner
import com.example.ui.theme.LocalLinkPalette
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    uiState: LinkUiState,
    onToggleConnection: () -> Unit,
    onNavigateToScreen: (LinkScreen) -> Unit,
    onOpenThemeStudio: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onRetryFailedTransfers: () -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
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

        // 1. Windows 11 Laptop Connection Status Hero Card with Circular Gradient Ring (inspired by reference image)
        item {
            LaptopConnectionHeroCard(
                uiState = uiState,
                onToggleConnection = onToggleConnection,
                onOpenSecurity = { onNavigateToScreen(LinkScreen.SECURITY) },
                onOpenThemeStudio = onOpenThemeStudio
            )
        }

        // 2. Featured Presentation & Laser Pointer Banner Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.55f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onNavigateToScreen(LinkScreen.PRESENTATION) }
                    .testTag("shortcut_card_presentation")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    palette.primary.copy(alpha = 0.24f),
                                    palette.secondary.copy(alpha = 0.14f),
                                    palette.surface
                                )
                            )
                        )
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(palette.primaryGradient)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CoPresent,
                                contentDescription = "Presentation and Laser Pointer",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Presentation & Laser Pointer",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = palette.secondary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "SLIDE ${uiState.presentationState.currentSlide}/${uiState.presentationState.totalSlides}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.secondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Live laser beam, spotlight stage, slide deck transport & timer ring",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = palette.secondary
                    )
                }
            }
        }

        // 3. 2x2 Shortcuts Grid to Files, Remote Control, Media, and Clipboard
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeaderRow(
                    title = stringResource(R.string.section_shortcuts),
                    actionLabel = "Theme Colors",
                    onActionClick = onOpenThemeStudio
                )

                val activeTransfersCount = uiState.transfers.count { it.status == TransferStatus.IN_PROGRESS }
                val filesSubtitle = when {
                    uiState.stateMode == PrototypeStateMode.DISCONNECTED -> "Laptop offline"
                    activeTransfersCount > 0 -> "$activeTransfersCount active • ${uiState.sharedFolders.size} folders"
                    else -> "${uiState.sharedFolders.size} shared folders"
                }

                val mediaSubtitle = if (!uiState.mediaState.isAvailable || uiState.stateMode == PrototypeStateMode.EMPTY) {
                    "No active session"
                } else {
                    "${if (uiState.mediaState.isPlaying) "Playing" else "Paused"} • ${uiState.mediaState.sourceApp}"
                }

                val clipboardSubtitle = if (uiState.clipboardHistory.isEmpty()) {
                    "Ready to push text"
                } else {
                    "${uiState.clipboardHistory.size} synced clips"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShortcutModuleCard(
                        title = "Files",
                        subtitle = filesSubtitle,
                        badge = "LAN SMB/TLS",
                        icon = Icons.Default.FolderOpen,
                        accentColor = palette.primary,
                        onClick = { onNavigateToScreen(LinkScreen.FILES) },
                        testTag = "shortcut_card_files",
                        modifier = Modifier.weight(1f)
                    )
                    ShortcutModuleCard(
                        title = "Remote Pointer",
                        subtitle = "XL 410dp Trackpad",
                        badge = "${(uiState.remoteState.sensitivity * 100).roundToInt()}% SPD",
                        icon = Icons.Default.TouchApp,
                        accentColor = palette.secondary,
                        onClick = { onNavigateToScreen(LinkScreen.REMOTE) },
                        testTag = "shortcut_card_remote",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShortcutModuleCard(
                        title = "Media",
                        subtitle = mediaSubtitle,
                        badge = "${(uiState.mediaState.volumeLevel * 100).roundToInt()}% VOL",
                        icon = Icons.Default.MusicNote,
                        accentColor = StatusConnected,
                        onClick = { onNavigateToScreen(LinkScreen.MEDIA) },
                        testTag = "shortcut_card_media",
                        modifier = Modifier.weight(1f)
                    )
                    ShortcutModuleCard(
                        title = "Clipboard",
                        subtitle = clipboardSubtitle,
                        badge = "BI-DIR",
                        icon = Icons.Default.ContentPaste,
                        accentColor = palette.tertiary,
                        onClick = { onNavigateToScreen(LinkScreen.CLIPBOARD) },
                        testTag = "shortcut_card_clipboard",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Recent File Transfers & Status
        item {
            SectionHeaderRow(
                title = stringResource(R.string.section_recent_transfers),
                actionLabel = stringResource(R.string.action_view_all),
                onActionClick = { onNavigateToScreen(LinkScreen.FILES) }
            )
        }

        if (uiState.transfers.isEmpty()) {
            item {
                LinkSurfaceCard(
                    modifier = Modifier.testTag("home_empty_transfers_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "No Recent File Transfers",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Open File Transfer to browse Windows 11 shared folders or upload photos from your phone.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { onNavigateToScreen(LinkScreen.FILES) },
                            border = BorderStroke(1.dp, palette.primary)
                        ) {
                            Text("Open File Transfer", color = palette.secondary)
                        }
                    }
                }
            }
        } else {
            items(
                items = uiState.transfers.take(4),
                key = { it.id }
            ) { transfer ->
                TransferItemRowCard(
                    item = transfer,
                    onClick = { onNavigateToScreen(LinkScreen.FILES) },
                    onRetry = onRetryFailedTransfers
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun LaptopConnectionHeroCard(
    uiState: LinkUiState,
    onToggleConnection: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenThemeStudio: () -> Unit
) {
    val palette = LocalLinkPalette.current
    val info = uiState.laptopInfo
    val isConnected = uiState.stateMode == PrototypeStateMode.CONNECTED ||
        uiState.stateMode == PrototypeStateMode.EMPTY
    val isLoading = uiState.stateMode == PrototypeStateMode.LOADING
    val isError = uiState.stateMode == PrototypeStateMode.ERROR

    val borderColor = when {
        isError -> StatusError.copy(alpha = 0.7f)
        isLoading -> StatusWarning.copy(alpha = 0.7f)
        isConnected -> palette.primary.copy(alpha = 0.65f)
        else -> palette.border
    }

    LinkSurfaceCard(
        useSubtleGradientGlow = true,
        borderColor = borderColor,
        contentPadding = PaddingValues(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_laptop_status_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LaptopWindows,
                        contentDescription = null,
                        tint = palette.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = info.deviceName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                }

                Text(
                    text = info.modelName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Text(
                    text = "${info.ipAddress} • ${info.wifiNetwork}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isConnected) palette.secondary else TextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Quick Theme Palette Chip
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = palette.background.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onOpenThemeStudio)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Theme: ${palette.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Circular Gradient Telemetry Ring (inspired by the reference UI kit)
            GradientCircularRing(
                progress = if (isConnected) info.laptopBatteryPercent / 100f else 0.12f,
                centerTitle = if (isConnected) "${info.laptopBatteryPercent}%" else "OFF",
                centerSubtitle = if (isConnected) "${info.latencyMs}ms LINK" else "STANDBY",
                size = 112.dp,
                strokeWidth = 9.dp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Connect / Disconnect Pill Button + Security Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.25f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .then(
                        if (isConnected) {
                            Modifier
                                .background(palette.surfaceElevated)
                                .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                        } else {
                            Modifier.background(palette.primaryGradient)
                        }
                    )
                    .clickable(onClick = onToggleConnection)
                    .testTag("home_connect_disconnect_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.LinkOff else Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isConnected) StatusError else Color.White
                    )
                    Text(
                        text = when {
                            isLoading -> "Connecting…"
                            isConnected -> stringResource(R.string.action_disconnect)
                            isError -> stringResource(R.string.action_reconnect)
                            else -> stringResource(R.string.action_connect)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedButton(
                onClick = onOpenSecurity,
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .weight(0.95f)
                    .height(48.dp)
                    .testTag("home_security_button")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = palette.secondary,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.action_pair_device),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ShortcutModuleCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    LinkSurfaceCard(
        containerColor = palette.surface,
        borderColor = palette.border,
        onClick = onClick,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.16f))
                    .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(21.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = palette.surfaceElevated,
                border = BorderStroke(1.dp, palette.border)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TransferItemRowCard(
    item: FileTransferItem,
    onClick: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current
    val isUpload = item.direction == TransferDirection.UPLOAD_TO_PC
    val directionColor = if (isUpload) palette.primary else palette.secondary
    val statusColor = when (item.status) {
        TransferStatus.COMPLETED -> StatusConnected
        TransferStatus.IN_PROGRESS -> palette.primary
        TransferStatus.FAILED -> StatusError
        TransferStatus.PAUSED -> StatusWarning
    }

    LinkSurfaceCard(
        containerColor = palette.surface,
        borderColor = if (item.status == TransferStatus.FAILED) StatusError.copy(alpha = 0.6f) else palette.border,
        onClick = onClick,
        contentPadding = PaddingValues(15.dp),
        modifier = modifier.testTag("transfer_item_${item.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(directionColor.copy(alpha = 0.16f))
                    .border(1.dp, directionColor.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = if (isUpload) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = if (isUpload) "Upload to Laptop" else "Download to Phone",
                    tint = directionColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.fileName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.fileSizeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = directionColor.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = if (isUpload) "UPLOAD ↑" else "DOWNLOAD ↓",
                                style = MaterialTheme.typography.labelSmall,
                                color = directionColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = item.timestampLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (item.status == TransferStatus.FAILED) StatusError else TextSecondary
                        )
                    }

                    when (item.status) {
                        TransferStatus.COMPLETED -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = StatusConnected,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        TransferStatus.FAILED -> {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Failed",
                                tint = StatusError,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        else -> {
                            Text(
                                text = "${(item.progress * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = statusColor
                            )
                        }
                    }
                }

                if (item.status == TransferStatus.IN_PROGRESS || item.status == TransferStatus.FAILED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { item.progress },
                        color = statusColor,
                        trackColor = palette.surfaceElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                }

                if (item.errorReason != null && item.status == TransferStatus.FAILED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.errorReason,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError,
                            modifier = Modifier.weight(1f)
                        )
                        if (onRetry != null) {
                            OutlinedButton(
                                onClick = onRetry,
                                border = BorderStroke(1.dp, StatusError),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = StatusError,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Retry", style = MaterialTheme.typography.labelSmall, color = StatusError)
                            }
                        }
                    }
                }
            }
        }
    }
}
