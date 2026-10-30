package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FolderFileItem
import com.example.model.LinkScreen
import com.example.model.LinkUiState
import com.example.model.PrototypeStateMode
import com.example.model.SharedFolder
import com.example.model.TransferDirection
import com.example.ui.components.LinkSurfaceCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.StateFeedbackBanner
import com.example.ui.theme.LocalLinkPalette
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FileTransferScreen(
    uiState: LinkUiState,
    onSelectFolder: (String) -> Unit,
    onUploadSimulated: (String, String) -> Unit,
    onDownloadFolderFile: (FolderFileItem) -> Unit,
    onRetryFailedTransfers: () -> Unit,
    onToggleConnection: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onNavigateToScreen: (LinkScreen) -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalLinkPalette.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val name = uri?.lastPathSegment?.substringAfterLast('/') ?: "Galaxy_S24_Photo_${(100..999).random()}.jpg"
        onUploadSimulated(name, "9.4 MB")
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        val name = uri?.lastPathSegment?.substringAfterLast('/') ?: "Project_Notes_${(10..99).random()}.pdf"
        onUploadSimulated(name, "4.2 MB")
    }

    val selectedFolder = uiState.sharedFolders.find { it.id == uiState.selectedFolderId }
        ?: uiState.sharedFolders.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("file_transfer_screen_list"),
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

        // Upload & Download Action Bar Card
        item {
            LinkSurfaceCard(useSubtleGradientGlow = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bi-Directional LAN File Bridge",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = selectedFolder?.let { "Target: ${it.windowsPath}" }
                                ?: "Select a shared Windows 11 folder below",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.primaryGradient)
                            .clickable {
                                onUploadSimulated(
                                    "Samsung_Photo_${(100..999).random()}.jpg",
                                    "11.6 MB"
                                )
                            }
                            .testTag("files_upload_photo_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.action_upload_photo),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            onUploadSimulated(
                                "Galaxy_Report_${(10..99).random()}.pdf",
                                "18.4 MB"
                            )
                        },
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("files_upload_file_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = palette.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.action_upload_file),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Or pick from device media/files:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Photo Picker",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.secondary,
                            modifier = Modifier
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                }
                                .padding(4.dp)
                        )
                        Text(
                            text = "System Files",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.secondary,
                            modifier = Modifier
                                .clickable {
                                    documentPickerLauncher.launch(arrayOf("*/*"))
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        // Shared Folders Browser
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeaderRow(
                    title = stringResource(R.string.section_shared_folders),
                    actionLabel = "${uiState.sharedFolders.size} Mounted"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.sharedFolders.forEach { folder ->
                        SharedFolderChipCard(
                            folder = folder,
                            isSelected = folder.id == selectedFolder?.id,
                            onClick = { onSelectFolder(folder.id) }
                        )
                    }
                }
            }
        }

        // Selected Folder Contents (Downloadable to Phone)
        if (selectedFolder != null && uiState.stateMode != PrototypeStateMode.DISCONNECTED) {
            item {
                LinkSurfaceCard(
                    modifier = Modifier.testTag("selected_folder_files_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${selectedFolder.name} (${selectedFolder.files.size} files)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = selectedFolder.windowsPath,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = palette.secondary.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "TAP ↓ TO DOWNLOAD",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.secondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedFolder.files.forEach { fileItem ->
                            FolderFileRow(
                                file = fileItem,
                                onDownload = { onDownloadFolderFile(fileItem) }
                            )
                        }
                    }
                }
            }
        }

        // Active & Completed Transfer Queue with clear Upload/Download indicators
        item {
            val uploadCount = uiState.transfers.count { it.direction == TransferDirection.UPLOAD_TO_PC }
            val downloadCount = uiState.transfers.count { it.direction == TransferDirection.DOWNLOAD_TO_PHONE }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.section_transfer_queue),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "$uploadCount Up",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.primary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = palette.secondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "$downloadCount Down",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.secondary
                        )
                    }
                }
            }
        }

        if (uiState.transfers.isEmpty()) {
            item {
                LinkSurfaceCard(
                    modifier = Modifier.testTag("files_empty_queue_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = "Transfer Queue is Empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tap Upload Photo/File above or download any file from a shared Windows folder.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(
                items = uiState.transfers,
                key = { it.id }
            ) { transfer ->
                TransferItemRowCard(
                    item = transfer,
                    onRetry = onRetryFailedTransfers
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SharedFolderChipCard(
    folder: SharedFolder,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalLinkPalette.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) palette.primary.copy(alpha = 0.2f) else palette.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) palette.primary else palette.border
        ),
        modifier = Modifier
            .width(176.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("shared_folder_${folder.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.FolderOpen else Icons.Default.Folder,
                    contentDescription = folder.name,
                    tint = if (isSelected) palette.secondary else palette.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = folder.totalSizeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Column {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${folder.itemCount} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun FolderFileRow(
    file: FolderFileItem,
    onDownload: () -> Unit
) {
    val palette = LocalLinkPalette.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.surfaceElevated,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("folder_file_${file.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.primary.copy(alpha = 0.16f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = palette.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${file.sizeLabel} • ${file.modifiedLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            OutlinedButton(
                onClick = onDownload,
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.dp, palette.secondary.copy(alpha = 0.55f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("download_btn_${file.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download ${file.name} to phone",
                    tint = palette.secondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Download",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.secondary
                )
            }
        }
    }
}
