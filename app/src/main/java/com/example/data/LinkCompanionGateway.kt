package com.example.data

import com.example.model.ClipboardDirection
import com.example.model.ClipboardEntry
import com.example.model.FileTransferItem
import com.example.model.FolderFileItem
import com.example.model.LaptopConnectionInfo
import com.example.model.MediaSessionState
import com.example.model.SharedFolder
import com.example.model.TransferDirection
import com.example.model.TransferStatus

/**
 * Modular gateway contract separating the Android UI from the underlying
 * Windows 11 companion transport (LAN WebSocket / TLS 1.3 daemon to be integrated later).
 */
interface LinkCompanionGateway {
    fun getDefaultLaptopInfo(): LaptopConnectionInfo
    fun getDefaultSharedFolders(): List<SharedFolder>
    fun getDefaultTransfers(): List<FileTransferItem>
    fun getErrorStateTransfers(): List<FileTransferItem>
    fun getDefaultMediaSession(): MediaSessionState
    fun getDefaultClipboardHistory(): List<ClipboardEntry>
}

class PrototypeLinkGateway : LinkCompanionGateway {

    override fun getDefaultLaptopInfo(): LaptopConnectionInfo {
        return LaptopConnectionInfo(
            deviceName = "WIN11-WORKSTATION",
            modelName = "Windows 11 Pro • Ryzen 9 / RTX 4070 Laptop",
            osVersion = "Windows 11 Pro 24H2 (Build 26100)",
            ipAddress = "192.168.1.42:9443",
            wifiNetwork = "HomeLab-5G-WiFi6E",
            linkSpeedMbps = 1200,
            latencyMs = 4,
            laptopBatteryPercent = 86,
            isCharging = true,
            tlsFingerprint = "SHA-256: 9F:4A:2C:81:7E:B0:19:4D:6A:CC:08:3F:52:E1",
            pairedPhoneName = "Samsung Galaxy S24 Ultra",
            lastVerifiedLabel = "Verified today • Local LAN only"
        )
    }

    override fun getDefaultSharedFolders(): List<SharedFolder> {
        return listOf(
            SharedFolder(
                id = "folder_desktop",
                name = "Desktop",
                windowsPath = "C:\\Users\\Ramez\\Desktop",
                itemCount = 4,
                totalSizeLabel = "148.2 MB",
                files = listOf(
                    FolderFileItem(
                        id = "f_desk_1",
                        name = "System_Architecture_v3.pdf",
                        sizeLabel = "14.8 MB",
                        sizeBytes = 14_800_000L,
                        modifiedLabel = "10 mins ago",
                        fileType = "PDF"
                    ),
                    FolderFileItem(
                        id = "f_desk_2",
                        name = "Board_Schematic_RevB.png",
                        sizeLabel = "6.4 MB",
                        sizeBytes = 6_400_000L,
                        modifiedLabel = "2 hours ago",
                        fileType = "IMAGE"
                    ),
                    FolderFileItem(
                        id = "f_desk_3",
                        name = "Release_Notes_WindowsCompanion.md",
                        sizeLabel = "42 KB",
                        sizeBytes = 42_000L,
                        modifiedLabel = "Yesterday",
                        fileType = "DOC"
                    ),
                    FolderFileItem(
                        id = "f_desk_4",
                        name = "Telemetry_Capture_1030.zip",
                        sizeLabel = "127.0 MB",
                        sizeBytes = 127_000_000L,
                        modifiedLabel = "Yesterday",
                        fileType = "ARCHIVE"
                    )
                )
            ),
            SharedFolder(
                id = "folder_downloads",
                name = "Downloads",
                windowsPath = "C:\\Users\\Ramez\\Downloads",
                itemCount = 3,
                totalSizeLabel = "512.6 MB",
                files = listOf(
                    FolderFileItem(
                        id = "f_dl_1",
                        name = "Win11_Companion_Daemon_Setup.msi",
                        sizeLabel = "48.2 MB",
                        sizeBytes = 48_200_000L,
                        modifiedLabel = "Today, 09:40",
                        fileType = "BINARY"
                    ),
                    FolderFileItem(
                        id = "f_dl_2",
                        name = "Camera_RAW_Export_Batch.zip",
                        sizeLabel = "442.0 MB",
                        sizeBytes = 442_000_000L,
                        modifiedLabel = "Yesterday",
                        fileType = "ARCHIVE"
                    ),
                    FolderFileItem(
                        id = "f_dl_3",
                        name = "Datasheet_STM32H7.pdf",
                        sizeLabel = "22.4 MB",
                        sizeBytes = 22_400_000L,
                        modifiedLabel = "Oct 28",
                        fileType = "PDF"
                    )
                )
            ),
            SharedFolder(
                id = "folder_pictures",
                name = "Pictures & Screenshots",
                windowsPath = "C:\\Users\\Ramez\\Pictures\\LinkDrop",
                itemCount = 3,
                totalSizeLabel = "38.9 MB",
                files = listOf(
                    FolderFileItem(
                        id = "f_pic_1",
                        name = "Samsung_S24_LabBench_01.jpg",
                        sizeLabel = "12.1 MB",
                        sizeBytes = 12_100_000L,
                        modifiedLabel = "Today, 11:15",
                        fileType = "IMAGE"
                    ),
                    FolderFileItem(
                        id = "f_pic_2",
                        name = "Oscilloscope_Capture_PWM.png",
                        sizeLabel = "3.8 MB",
                        sizeBytes = 3_800_000L,
                        modifiedLabel = "Today, 10:02",
                        fileType = "IMAGE"
                    ),
                    FolderFileItem(
                        id = "f_pic_3",
                        name = "Demo_Walkthrough_Clip.mp4",
                        sizeLabel = "23.0 MB",
                        sizeBytes = 23_000_000L,
                        modifiedLabel = "Oct 29",
                        fileType = "VIDEO"
                    )
                )
            ),
            SharedFolder(
                id = "folder_projects",
                name = "Workspace",
                windowsPath = "D:\\Workspace\\LinkProtocol",
                itemCount = 2,
                totalSizeLabel = "19.5 MB",
                files = listOf(
                    FolderFileItem(
                        id = "f_proj_1",
                        name = "protocol_spec_v1.json",
                        sizeLabel = "128 KB",
                        sizeBytes = 128_000L,
                        modifiedLabel = "Just now",
                        fileType = "CODE"
                    ),
                    FolderFileItem(
                        id = "f_proj_2",
                        name = "benchmark_results_lan.csv",
                        sizeLabel = "19.4 MB",
                        sizeBytes = 19_400_000L,
                        modifiedLabel = "3 hours ago",
                        fileType = "DATA"
                    )
                )
            )
        )
    }

    override fun getDefaultTransfers(): List<FileTransferItem> {
        return listOf(
            FileTransferItem(
                id = "tx_1",
                fileName = "Samsung_S24_Camera_4K.mp4",
                folderPath = "C:\\Users\\Ramez\\Pictures\\LinkDrop",
                fileSizeLabel = "184.0 MB",
                direction = TransferDirection.UPLOAD_TO_PC,
                progress = 0.68f,
                status = TransferStatus.IN_PROGRESS,
                timestampLabel = " transferring • 82 MB/s",
                speedLabel = "82 MB/s"
            ),
            FileTransferItem(
                id = "tx_2",
                fileName = "System_Architecture_v3.pdf",
                folderPath = "Phone / Download / Link",
                fileSizeLabel = "14.8 MB",
                direction = TransferDirection.DOWNLOAD_TO_PHONE,
                progress = 1.0f,
                status = TransferStatus.COMPLETED,
                timestampLabel = "Completed • 4m ago"
            ),
            FileTransferItem(
                id = "tx_3",
                fileName = "Samsung_S24_LabBench_01.jpg",
                folderPath = "C:\\Users\\Ramez\\Desktop",
                fileSizeLabel = "12.1 MB",
                direction = TransferDirection.UPLOAD_TO_PC,
                progress = 1.0f,
                status = TransferStatus.COMPLETED,
                timestampLabel = "Completed • 18m ago"
            ),
            FileTransferItem(
                id = "tx_4",
                fileName = "Board_Schematic_RevB.png",
                folderPath = "Phone / Pictures / Link",
                fileSizeLabel = "6.4 MB",
                direction = TransferDirection.DOWNLOAD_TO_PHONE,
                progress = 1.0f,
                status = TransferStatus.COMPLETED,
                timestampLabel = "Completed • 1h ago"
            )
        )
    }

    override fun getErrorStateTransfers(): List<FileTransferItem> {
        return listOf(
            FileTransferItem(
                id = "tx_err_1",
                fileName = "Telemetry_Capture_1030.zip",
                folderPath = "C:\\Users\\Ramez\\Desktop",
                fileSizeLabel = "127.0 MB",
                direction = TransferDirection.UPLOAD_TO_PC,
                progress = 0.42f,
                status = TransferStatus.FAILED,
                timestampLabel = "Interrupted at 42%",
                errorReason = "Socket timeout on 192.168.1.42:9443 (Simulated Error State)"
            ),
            FileTransferItem(
                id = "tx_err_2",
                fileName = "System_Architecture_v3.pdf",
                folderPath = "Phone / Download / Link",
                fileSizeLabel = "14.8 MB",
                direction = TransferDirection.DOWNLOAD_TO_PHONE,
                progress = 1.0f,
                status = TransferStatus.COMPLETED,
                timestampLabel = "Completed • 4m ago"
            )
        )
    }

    override fun getDefaultMediaSession(): MediaSessionState {
        return MediaSessionState()
    }

    override fun getDefaultClipboardHistory(): List<ClipboardEntry> {
        return listOf(
            ClipboardEntry(
                id = "clip_1",
                content = "ssh ramez@192.168.1.42 -p 2222",
                direction = ClipboardDirection.PC_TO_PHONE,
                timestampLabel = "2 mins ago",
                charCount = 30
            ),
            ClipboardEntry(
                id = "clip_2",
                content = "https://developer.android.com/jetpack/compose/mental-model",
                direction = ClipboardDirection.PHONE_TO_PC,
                timestampLabel = "14 mins ago",
                charCount = 58
            ),
            ClipboardEntry(
                id = "clip_3",
                content = "C:\\Users\\Ramez\\Desktop\\System_Architecture_v3.pdf",
                direction = ClipboardDirection.PC_TO_PHONE,
                timestampLabel = "1 hour ago",
                charCount = 49
            )
        )
    }
}
