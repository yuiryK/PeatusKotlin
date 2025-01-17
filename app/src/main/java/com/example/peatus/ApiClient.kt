package com.example.peatus

import kotlinx.coroutines.*
import java.net.URL

// Общий APIClient для всех платформ
class ApiClient(private val baseUrl: String) {

    // Асинхронный запрос
    suspend fun fetchData(): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(baseUrl)
                url.readText()  // Получаем текстовый ответ с сервера
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
        }
    }
}