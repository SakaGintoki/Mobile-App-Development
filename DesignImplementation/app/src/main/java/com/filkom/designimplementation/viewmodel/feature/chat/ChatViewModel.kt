package com.filkom.designimplementation.viewmodel.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.ChatRepository
import com.filkom.designimplementation.model.core.ai.AiService
import com.filkom.designimplementation.model.core.ai.ChatMsg
import com.filkom.designimplementation.model.data.ai.ChatMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    val messages: StateFlow<List<ChatMessage>> = repository.getMessagesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureWelcomeMessage()
        }
    }

    fun send(text: String) {
        val t = text.trim()
        if (t.isEmpty() || _isSending.value) return

        _isSending.value = true

        viewModelScope.launch {
            try {
                val userMsg = ChatMessage(
                    text = t,
                    fromUser = true,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveMessageToFirestore(userMsg)

                val currentHistory = messages.value
                val aiResponseText = repository.askAi(currentHistory, t)

                val aiMsg = ChatMessage(
                    text = aiResponseText,
                    fromUser = false,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveMessageToFirestore(aiMsg)

            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = ChatMessage(
                    text = "Maaf, terjadi kesalahan jaringan. Coba lagi ya.",
                    fromUser = false,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveMessageToFirestore(errorMsg)
            } finally {
                _isSending.value = false
            }
        }
    }
}
