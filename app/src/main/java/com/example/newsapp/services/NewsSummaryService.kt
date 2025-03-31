package com.example.newsapp.services

import com.example.newsapp.models.NewsSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class NewsSummaryService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val apiUrl = "https://ai.dreamapi.net/api/v1/summarize"
    private val mediaType = "application/json".toMediaType()

    suspend fun generateSummary(articleId: String, content: String, language: String = "en"): NewsSummary {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("text", content)
                    put("language", language)
                    put("max_length", 150) // Giới hạn độ dài tóm tắt
                }

                val request = Request.Builder()
                    .url(apiUrl)
                    .post(requestBody.toString().toRequestBody(mediaType))
                    .build()

                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    throw Exception("API request failed: ${response.code}")
                }

                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "")
                
                NewsSummary(
                    articleId = articleId,
                    summary = jsonResponse.getString("summary"),
                    language = language
                )
            } catch (e: Exception) {
                // Trong trường hợp lỗi, trả về tóm tắt đơn giản
                NewsSummary(
                    articleId = articleId,
                    summary = content.take(150) + "...",
                    language = language
                )
            }
        }
    }
} 