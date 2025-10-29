package com.filkom.designimplementation.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.core.ai.AiService
import com.filkom.designimplementation.core.ai.ChatMsg
import com.filkom.designimplementation.data.ChatMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(
    private val ai: AiService
) : ViewModel() {

    // ===== UI State =====
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    // cegah pengulangan jawaban
    private val lastBotReplies = ArrayDeque<String>(5)

    // ===== Public API =====

    /** Tambah sapaan awal dari bot, panggil sekali saat layar dibuka. */
    fun seedWelcome(text: String = "Halo, Saya Little-Ai 👋 Siap bantu. Ceritakan kebutuhanmu ya 💗") {
        if (_messages.value.isEmpty()) {
            _messages.update { it + ChatMessage(id = nanoId(), fromUser = false, text = text) }
            keep(text)
        }
    }

    /** Kirim pesan user dan minta balasan AI. */
    fun send(text: String) {
        val t = text.trim()
        if (t.isEmpty() || _isSending.value) return
        _messages.update { it + ChatMessage(id = nanoId(), fromUser = true, text = t) }
        generateReply()
    }

    /** Regenerasi balasan terakhir bot (tanpa menghapus history). */
    fun regenerateLast() {
        if (_messages.value.isEmpty() || _isSending.value) return
        // hapus balasan bot terakhir jika pesan terakhir memang dari bot
        val cur = _messages.value
        val idx = cur.indexOfLast { !it.fromUser }
        if (idx >= 0 && idx == cur.lastIndex) {
            _messages.update { it.dropLast(1) }
        }
        generateReply()
    }

    /** Hapus semua percakapan. */
    fun clearAll() {
        _messages.value = emptyList()
        lastBotReplies.clear()
    }

    // ===== Internal =====

    private fun generateReply() {
        _isSending.value = true
        viewModelScope.launch {
            val historyAsMsgs = _messages.value.map {
                ChatMsg(role = if (it.fromUser) "user" else "assistant", content = it.text)
            }

            val raw: String = withContext(Dispatchers.IO) {
                runCatching {
                    ai.generateReply(systemPrompt(), historyAsMsgs)
                }.recover { e ->
                    if (e is CancellationException) throw e
                    // fallback pesan error yang ramah
                    "Maaf, aku mengalami kendala jaringan. Coba kirim ulang sebentar lagi ya."
                }.getOrThrow()
            }

            val reply = dedupe(raw)
            _messages.update { it + ChatMessage(id = nanoId(), fromUser = false, text = reply) }
            keep(reply)
            _isSending.value = false
        }
    }

    private fun systemPrompt() = """
        Kamu adalah asisten ramah bernama Little AI untuk orang tua dan bayi.
        Balas ringkas, spesifik, dan actionable, hindari pengulangan.
        Gunakan Bahasa Indonesia yang jelas. Untuk topik kesehatan bayi,
        beri langkah praktis dan tanda bahaya, tanpa mendiagnosis.
    """.trimIndent()

    private fun dedupe(text: String): String {
        val t = text.trim()
        val duplicated = lastBotReplies.any { it.equals(t, ignoreCase = true) }
        return if (duplicated) "$t\n\nTambahan: coba langkah lain yang belum dicoba." else t
    }

    private fun keep(t: String) {
        lastBotReplies.addFirst(t)
        while (lastBotReplies.size > 5) lastBotReplies.removeLast()
    }

    private fun nanoId(): Long = System.nanoTime()
}
