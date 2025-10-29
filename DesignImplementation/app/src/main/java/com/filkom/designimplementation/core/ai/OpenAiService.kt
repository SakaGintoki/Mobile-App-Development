package com.filkom.designimplementation.core.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class OpenAiService(
    private val apiKey: String = "REDACTED",
    private val model: String = "gpt-4o-mini"
) : AiService {

    @Serializable
    private data class Req(val model: String,
                           val messages: List<Msg>,
                           val temperature: Double = 0.85,
                           @SerialName("presence_penalty") val presencePenalty: Double = 0.7,
                           @SerialName("frequency_penalty") val frequencyPenalty: Double = 0.6) {
        @Serializable data class Msg(val role: String, val content: String)
    }
    @Serializable
    private data class Res(val choices: List<Choice>) {
        @Serializable data class Choice(val message: Msg) {
            @Serializable data class Msg(val role: String, val content: String)
        }
    }

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generateReply(systemPrompt: String, history: List<ChatMsg>): String {
        val msgs = listOf(Req.Msg("system", systemPrompt)) +
                history.map { Req.Msg(it.role, it.content) }
        val body = json.encodeToString(Req.serializer(), Req(model, msgs))
            .let { RequestBody.create("application/json".toMediaType(), it) }

        val req = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer REDACTED")
            .post(body)
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("OpenAI ${resp.code}: ${resp.message}")
            val parsed = json.decodeFromString(Res.serializer(), resp.body!!.string())
            return parsed.choices.firstOrNull()?.message?.content?.trim()
                ?: "Maaf, aku belum bisa menjawab sekarang."
        }
    }
}
