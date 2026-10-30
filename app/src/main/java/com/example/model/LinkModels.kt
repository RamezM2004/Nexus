package com.example.model

enum class LinkScreen(val route: String, val title: String) {
    HOME("home", "Link"),
    FILES("files", "File Transfer"),
    REMOTE("remote", "Remote Control"),
    PRESENTATION("presentation", "Presentation & Pointer"),
    MEDIA("media", "Media Control"),
    CLIPBOARD("clipboard", "Clipboard Sync"),
    SECURITY("security", "Connection & Security")
}

/**
 * Interactive prototype state mode controlled via the State Switcher Bar
 * or natural user actions (Connect / Disconnect / Error Retry).
 */
enum class PrototypeStateMode(val label: String) {
    CONNECTED("Connected"),
    LOADING("Connecting"),
    DISCONNECTED("Disconnected"),
    EMPTY("Empty"),
    ERROR("Error")
}

data class LaptopConnectionInfo(
    val deviceName: String,
    val modelName: String,
    val osVersion: String,
    val ipAddress: String,
    val wifiNetwork: String,
    val linkSpeedMbps: Int,
    val latencyMs: Int,
    val laptopBatteryPercent: Int,
    val isCharging: Boolean,
    val tlsFingerprint: String,
    val pairedPhoneName: String,
    val lastVerifiedLabel: String
)

data class FolderFileItem(
    val id: String,
    val name: String,
    val sizeLabel: String,
    val sizeBytes: Long,
    val modifiedLabel: String,
    val fileType: String
)

data class SharedFolder(
    val id: String,
    val name: String,
    val windowsPath: String,
    val itemCount: Int,
    val totalSizeLabel: String,
    val files: List<FolderFileItem>
)

enum class TransferDirection {
    UPLOAD_TO_PC,
    DOWNLOAD_TO_PHONE
}

enum class TransferStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    PAUSED
}

data class FileTransferItem(
    val id: String,
    val fileName: String,
    val folderPath: String,
    val fileSizeLabel: String,
    val direction: TransferDirection,
    val progress: Float, // 0f..1f
    val status: TransferStatus,
    val timestampLabel: String,
    val speedLabel: String? = null,
    val errorReason: String? = null
)

data class RemoteControlState(
    val cursorX: Float = 960f,
    val cursorY: Float = 540f,
    val lastDeltaX: Float = 0f,
    val lastDeltaY: Float = 0f,
    val sensitivity: Float = 1.5f,
    val isExpandedTrackpad: Boolean = true,
    val activeModifiers: Set<String> = emptySet(),
    val keyboardDraft: String = "",
    val lastDispatchedCommand: String = "READY • Precision Trackpad Active",
    val commandHistory: List<String> = listOf(
        "MOUSE_MOVE (dx=+18, dy=-12) -> 960, 540",
        "LEFT_CLICK (button=1, state=DOWN_UP)",
        "KEY_COMBO (Win + D) -> Desktop Toggle"
    )
)

enum class PointerVisualMode(val label: String) {
    LASER_DOT("Laser Beam"),
    SPOTLIGHT("Spotlight"),
    HIGHLIGHT_PEN("Ink Trail")
}

enum class ScreenBlankMode(val label: String) {
    NONE("Live Slide"),
    BLACK("Blackout (B)"),
    WHITE("Whiteout (W)")
}

data class PresentationState(
    val deckTitle: String = "Q4 Robotics & Mechatronics Architecture.pptx",
    val currentSlide: Int = 8,
    val totalSlides: Int = 24,
    val elapsedSeconds: Int = 465, // 07:45
    val targetDurationSeconds: Int = 1200, // 20:00
    val isTimerRunning: Boolean = true,
    val isSlideshowActive: Boolean = true,
    val pointerMode: PointerVisualMode = PointerVisualMode.LASER_DOT,
    val blankMode: ScreenBlankMode = ScreenBlankMode.NONE,
    val pointerNormX: Float = 0.5f,
    val pointerNormY: Float = 0.5f,
    val isLaserPressed: Boolean = false,
    val pointerSize: Float = 1.2f,
    val speakerNotes: List<String> = listOf(
        "Slide 1: Introduce system-level architecture and real-time deterministic bus.",
        "Slide 8: Highlight latency benchmarks (4 ms Wi-Fi 6E transport vs. legacy Bluetooth).",
        "Slide 9: Walk through closed-loop actuator telemetry and safety interlocks."
    ),
    val lastPresentationCommand: String = "LASER_READY • Slide 8 of 24 active on Windows 11"
)

data class MediaSessionState(
    val isAvailable: Boolean = true,
    val sourceApp: String = "YouTube Music",
    val sourceSubtitle: String = "Windows 11 Media Session • Chrome PWA",
    val trackTitle: String = "Midnight City (Instrumental Mix)",
    val artistName: String = "M83 • Hurry Up, We're Dreaming",
    val albumTitle: String = "Hurry Up, We're Dreaming",
    val isPlaying: Boolean = true,
    val positionSeconds: Int = 114,
    val durationSeconds: Int = 243,
    val volumeLevel: Float = 0.68f,
    val isMuted: Boolean = false,
    val audioOutputDevice: String = "Galaxy Buds3 Pro (Windows Spatial)"
)

enum class ClipboardDirection {
    PHONE_TO_PC,
    PC_TO_PHONE
}

data class ClipboardEntry(
    val id: String,
    val content: String,
    val direction: ClipboardDirection,
    val timestampLabel: String,
    val charCount: Int,
    val isDelivered: Boolean = true
)

data class SecurityPermissions(
    val allowFileSystemAccess: Boolean = true,
    val allowMouseKeyboardControl: Boolean = true,
    val allowMediaSessionControl: Boolean = true,
    val autoSyncClipboard: Boolean = false
)

data class ThemeConfigState(
    val selectedPresetId: String = "neon_sunset",
    val useCustomHues: Boolean = false,
    val customPrimaryHue: Float = 325f,
    val customSecondaryHue: Float = 38f,
    val deepOledDark: Boolean = false,
    val isThemeStudioOpen: Boolean = false
)

data class LinkUiState(
    val currentScreen: LinkScreen = LinkScreen.HOME,
    val backStack: List<LinkScreen> = emptyList(),
    val stateMode: PrototypeStateMode = PrototypeStateMode.CONNECTED,
    val statusBannerMessage: String? = null,
    val laptopInfo: LaptopConnectionInfo,
    val sharedFolders: List<SharedFolder>,
    val selectedFolderId: String,
    val transfers: List<FileTransferItem>,
    val remoteState: RemoteControlState = RemoteControlState(),
    val presentationState: PresentationState = PresentationState(),
    val mediaState: MediaSessionState = MediaSessionState(),
    val clipboardDraft: String = "",
    val clipboardHistory: List<ClipboardEntry>,
    val permissions: SecurityPermissions = SecurityPermissions(),
    val themeConfig: ThemeConfigState = ThemeConfigState(),
    val pairingCodeInput: String = "LINK-8492-W11",
    val isQrScannerActive: Boolean = false,
    val lastPairingEvent: String = "Mutual TLS 1.3 certificate pinned on local LAN"
)
