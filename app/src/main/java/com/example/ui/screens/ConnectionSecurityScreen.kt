package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LaptopWindows
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LinkScreen
import com.example.model.LinkUiState
import com.example.model.PrototypeStateMode
import com.example.ui.components.LinkSurfaceCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.StateFeedbackBanner
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceElevated
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ConnectionSecurityScreen(
    uiState: LinkUiState,
    onUpdatePairingCode: (String) -> Unit,
    onToggleQrScanner: () -> Unit,
    onSimulateQrPairSuccess: () -> Unit,
    onTogglePermission: (String) -> Unit,
    onDisconnectRevoke: () -> Unit,
    onToggleConnection: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onNavigateToScreen: (LinkScreen) -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val info = uiState.laptopInfo
    val isConnected = uiState.stateMode == PrototypeStateMode.CONNECTED ||
        uiState.stateMode == PrototypeStateMode.EMPTY

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("security_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            StateFeedbackBanner(
                stateMode = uiState.stateMode,
                bannerMessage = uiState.statusBannerMessage,
                onReconnect = onToggleConnection,
                onRestoreSampleData = onRestoreSampleData,
                onOpenSecurity = { },
                onDismissMessage = onDismissBanner
            )
        }

        // 1. Device Pairing & QR-Code Scanning Interface
        item {
            SectionHeaderRow(title = stringResource(R.string.section_qr_pairing))
            Spacer(modifier = Modifier.height(8.dp))

            LinkSurfaceCard(
                containerColor = CharcoalSurface,
                borderColor = HairlineBorder,
                modifier = Modifier.testTag("security_qr_pairing_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Windows 11 Companion Pairing",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Scan the QR code displayed by the Link Windows 11 tray app to pin its local TLS certificate.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stylized QR Scanner Viewfinder Interface
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CharcoalBackground)
                        .border(1.dp, CobaltPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("security_qr_scanner_box")
                ) {
                    Canvas(modifier = Modifier.size(156.dp)) {
                        val stroke = 3.dp.toPx()
                        val cornerLen = 28.dp.toPx()
                        val w = size.width
                        val h = size.height

                        // Top-left bracket
                        drawLine(CyanAccent, Offset(0f, 0f), Offset(cornerLen, 0f), stroke, StrokeCap.Round)
                        drawLine(CyanAccent, Offset(0f, 0f), Offset(0f, cornerLen), stroke, StrokeCap.Round)

                        // Top-right bracket
                        drawLine(CyanAccent, Offset(w, 0f), Offset(w - cornerLen, 0f), stroke, StrokeCap.Round)
                        drawLine(CyanAccent, Offset(w, 0f), Offset(w, cornerLen), stroke, StrokeCap.Round)

                        // Bottom-left bracket
                        drawLine(CyanAccent, Offset(0f, h), Offset(cornerLen, h), stroke, StrokeCap.Round)
                        drawLine(CyanAccent, Offset(0f, h), Offset(0f, h - cornerLen), stroke, StrokeCap.Round)

                        // Bottom-right bracket
                        drawLine(CyanAccent, Offset(w, h), Offset(w - cornerLen, h), stroke, StrokeCap.Round)
                        drawLine(CyanAccent, Offset(w, h), Offset(w, h - cornerLen), stroke, StrokeCap.Round)

                        // Simulated QR matrix blocks inside viewfinder
                        val cell = w / 7f
                        for (row in 1..5) {
                            for (col in 1..5) {
                                if ((row + col) % 2 == 0 || (row == 3 && col == 3)) {
                                    drawRect(
                                        color = CobaltPrimary.copy(alpha = 0.28f),
                                        topLeft = Offset(col * cell + 4f, row * cell + 4f),
                                        size = Size(cell - 8f, cell - 8f)
                                    )
                                }
                            }
                        }

                        // Laser scan line
                        drawLine(
                            color = StatusConnected,
                            start = Offset(12f, h * 0.52f),
                            end = Offset(w - 12f, h * 0.52f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "QR Scanner Reticle",
                            tint = CyanAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CharcoalSurface.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, HairlineBorder)
                        ) {
                            Text(
                                text = if (uiState.isQrScannerActive) {
                                    "SCANNING LOCAL LAN QR PAYLOAD…"
                                } else {
                                    "READY TO SCAN • WIN11-WORKSTATION"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSimulateQrPairSuccess,
                        colors = ButtonDefaults.buttonColors(containerColor = CobaltPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("security_simulate_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.action_simulate_qr),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onToggleQrScanner,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, HairlineBorder),
                        modifier = Modifier
                            .weight(0.8f)
                            .height(48.dp)
                            .testTag("security_toggle_reticle_button")
                    ) {
                        Text(
                            text = if (uiState.isQrScannerActive) "Stop Preview" else "Preview Mode",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Manual Pairing Key Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.pairingCodeInput,
                        onValueChange = onUpdatePairingCode,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CobaltPrimary,
                            unfocusedBorderColor = HairlineBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CharcoalBackground,
                            unfocusedContainerColor = CharcoalBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("security_pairing_code_input")
                    )

                    Button(
                        onClick = onSimulateQrPairSuccess,
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalSurfaceElevated),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .border(1.dp, CobaltPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .testTag("security_verify_code_button")
                    ) {
                        Text(
                            text = stringResource(R.string.action_verify_code),
                            style = MaterialTheme.typography.labelLarge,
                            color = CyanAccent
                        )
                    }
                }
            }
        }

        // 2. Connection Status & Trusted-Device Information
        item {
            SectionHeaderRow(title = stringResource(R.string.section_trusted_device))
            Spacer(modifier = Modifier.height(8.dp))

            LinkSurfaceCard(
                containerColor = CharcoalSurface,
                borderColor = HairlineBorder,
                modifier = Modifier.testTag("security_trusted_device_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CobaltPrimary.copy(alpha = 0.16f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.LaptopWindows,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = info.deviceName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = info.osVersion,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isConnected) StatusConnected.copy(alpha = 0.16f) else CharcoalSurfaceElevated
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = if (isConnected) StatusConnected else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isConnected) "TRUSTED" else "OFFLINE",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isConnected) StatusConnected else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                SecurityDetailRow("Paired Phone", info.pairedPhoneName)
                SecurityDetailRow("LAN Endpoint", info.ipAddress)
                SecurityDetailRow("Wi-Fi SSID", "${info.wifiNetwork} (${info.linkSpeedMbps} Mbps)")
                SecurityDetailRow("Encryption", "Mutual TLS 1.3 • Local Subnet Only")
                SecurityDetailRow("Certificate Pin", info.tlsFingerprint)
                SecurityDetailRow("Last Handshake", uiState.lastPairingEvent)
            }
        }

        // 3. Permission Controls & Disconnect Action
        item {
            SectionHeaderRow(title = stringResource(R.string.section_permissions))
            Spacer(modifier = Modifier.height(8.dp))

            LinkSurfaceCard(
                containerColor = CharcoalSurface,
                borderColor = HairlineBorder,
                modifier = Modifier.testTag("security_permissions_card")
            ) {
                PermissionToggleRow(
                    title = "Shared Folder File Transfer",
                    subtitle = "Allow browsing, uploading, and downloading files in mounted Windows folders",
                    checked = uiState.permissions.allowFileSystemAccess,
                    onToggle = { onTogglePermission("files") },
                    testTag = "perm_toggle_files"
                )
                Spacer(modifier = Modifier.height(10.dp))
                PermissionToggleRow(
                    title = "Remote Touchpad & Keyboard Injection",
                    subtitle = "Allow pointer movement, clicks, and shortcut keystrokes on Windows 11",
                    checked = uiState.permissions.allowMouseKeyboardControl,
                    onToggle = { onTogglePermission("input") },
                    testTag = "perm_toggle_input"
                )
                Spacer(modifier = Modifier.height(10.dp))
                PermissionToggleRow(
                    title = "Windows Media Session Control",
                    subtitle = "Read track metadata and control YouTube Music / system volume",
                    checked = uiState.permissions.allowMediaSessionControl,
                    onToggle = { onTogglePermission("media") },
                    testTag = "perm_toggle_media"
                )
                Spacer(modifier = Modifier.height(10.dp))
                PermissionToggleRow(
                    title = "Automatic Background Clipboard Sync",
                    subtitle = "Automatically push copied phone text without manual Send confirmation",
                    checked = uiState.permissions.autoSyncClipboard,
                    onToggle = { onTogglePermission("clipboard") },
                    testTag = "perm_toggle_clipboard"
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDisconnectRevoke,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusError.copy(alpha = 0.18f),
                        contentColor = StatusError
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, StatusError.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                        .testTag("security_disconnect_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LinkOff,
                        contentDescription = null,
                        tint = StatusError,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.action_disconnect_revoke),
                        style = MaterialTheme.typography.labelLarge,
                        color = StatusError
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SecurityDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.width(112.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PermissionToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CharcoalSurfaceElevated,
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (checked) StatusConnected else TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CobaltPrimary,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = CharcoalBackground,
                    uncheckedBorderColor = HairlineBorder
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
