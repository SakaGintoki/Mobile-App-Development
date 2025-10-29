package com.filkom.designimplementation
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

sealed interface BotDock {
    data object TopBar : BotDock
    data object HeaderLeft : BotDock
    data object FloatingEnd : BotDock
    data object InputLeading : BotDock

    companion object {
        fun next(of: BotDock): BotDock = when (of) {
            is TopBar -> HeaderLeft
            is HeaderLeft -> FloatingEnd
            is FloatingEnd -> InputLeading
            is InputLeading -> TopBar
        }

        // Saver: serialisasikan ke String
        fun saver(): Saver<BotDock, String> = Saver(
            save = { when (it) {
                is TopBar -> "TopBar"
                is HeaderLeft -> "HeaderLeft"
                is FloatingEnd -> "FloatingEnd"
                is InputLeading -> "InputLeading"
            }},
            restore = { when (it) {
                "TopBar" -> TopBar
                "HeaderLeft" -> HeaderLeft
                "FloatingEnd" -> FloatingEnd
                "InputLeading" -> InputLeading
                else -> TopBar
            }}
        )
    }
}
