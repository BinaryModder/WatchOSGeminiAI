package com.example.neurochat.presentation

import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

object GeminiApi {
    private val client = OkHttpClient()
    private const val apiKey = "yourGeminiApi"

    fun sendMessage(userMessage: String, onResult: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

                val json = """
                {
                  "contents": [
                    {"parts":[{"text":"$userMessage"}]}
                  ]
                }
                """

                val request = Request.Builder()
                    .url(url)
                    .post(RequestBody.create("application/json".toMediaType(), json))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                val text = JSONObject(body)
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                withContext(Dispatchers.Main) {
                    onResult(text)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult("Ошибка: ${e.message}")
                }
            }
        }
    }
}
