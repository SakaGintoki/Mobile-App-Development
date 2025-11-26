package com.filkom.designimplementation.model.core.ai

import android.util.Log // Import Log Android
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class OpenAiService(
    protected val apiKey: String = "REDACTED",
    protected val model: String = "gpt-4o-mini"
) : AiService {

    @Serializable
    private data class Req(val model: String, val messages: List<Msg>) {
        @Serializable data class Msg(val role: String, val content: String)
    }
    @Serializable
    private data class Res(val choices: List<Choice>) {
        @Serializable data class Choice(val message: Msg) {
            @Serializable data class Msg(val role: String, val content: String)
        }
    }

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun generateReply(systemPrompt: String, history: List<ChatMsg>): String {
        return withContext(Dispatchers.IO) {
            try {
                val msgs = listOf(Req.Msg("system", systemPrompt)) +
                        history.map { Req.Msg(it.role, it.content) }

                val body = json.encodeToString(Req.serializer(), Req(model, msgs))
                    .let { RequestBody.create("application/json".toMediaType(), it) }

                val req = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .post(body)
                    .build()

                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        val errorBody = resp.body?.string() ?: "Unknown Error"
                        Log.e("OpenAI_Error", "Code: ${resp.code}, Body: $errorBody")
                        return@withContext "Maaf, Little AI sedang mengalami gangguan teknis. Silakan coba lagi beberapa saat lagi ya. \uD83D\uDE4F"
                    }

                    val responseBody = resp.body!!.string()
                    val parsed = json.decodeFromString(Res.serializer(), responseBody)

                    parsed.choices.firstOrNull()?.message?.content?.trim()
                        ?: "Maaf, aku bingung harus menjawab apa."
                }
            } catch (e: Exception) {
                Log.e("OpenAI_Exception", "Error: ${e.message}", e)

                "Sepertinya koneksi internetmu sedang tidak stabil. Cek sinyalmu dulu ya \uD83D\uDCE1"
            }
        }
    }
}