package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.local.CreationDao
import com.example.data.model.CreationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CreationRepository(private val creationDao: CreationDao) {

    val allCreations: Flow<List<CreationItem>> = creationDao.getAllCreations()

    suspend fun insertCreation(item: CreationItem) = withContext(Dispatchers.IO) {
        creationDao.insertCreation(item)
    }

    suspend fun deleteCreation(id: Int) = withContext(Dispatchers.IO) {
        creationDao.deleteCreationById(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        creationDao.clearAllCreations()
    }

    suspend fun generateAICreatorContent(
        prompt: String,
        contentType: String,
        systemInstruction: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(Exception("የGemini API ቁልፍ (API Key) አልተገኘም። እባክዎን በ AI Studio Secrets ውስጥ ያስገቡ።"))
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (generatedText != null) {
                Result.success(generatedText)
            } else {
                Result.failure(Exception("ምላሽ ማመንጨት አልተቻለም። እባክዎ እንደገና ይሞክሩ።"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
