package com.filkom.designimplementation.model.common
import com.filkom.designimplementation.BotDock

data class ChatUiState(
    val botName: String = "Little AI",
    val botAvatarRes: Int,
    val dock: BotDock = BotDock.TopBar
)