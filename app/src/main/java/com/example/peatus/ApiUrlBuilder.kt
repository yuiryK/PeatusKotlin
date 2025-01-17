package com.example.peatus

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object APIUrlBuilder {
    private var baseUrl: String = ""
    private var route: String = ""
    private val pathParams = mutableListOf<String>()

    // Устанавливаем базовый URL
    fun setBaseUrl(url: String): APIUrlBuilder {
        baseUrl = url
        return this
    }

    // Устанавливаем маршрут (роут) с параметрами через слэш
    fun setRoute(routeTemplate: String, vararg params: String): APIUrlBuilder {
        route = routeTemplate
        // Добавляем параметры как части пути (через слэш)
        pathParams.clear()
        pathParams.addAll(params)
        return this
    }

    fun encodeUrl(url: String): String {
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
        return encodedUrl.replace("+", "%20").replace("%2F", "/").replace("%3A", ":")
    }

    // Формирование конечного URL
    fun build(): String {
        // Формируем базовый URL с маршрутом
        val url = StringBuilder(baseUrl).append(route)

        // Добавляем параметры пути, разделённые слэшами
        if (pathParams.isNotEmpty()) {
            pathParams.joinToString("/").also { url.append("/$it") }
        }

        return encodeUrl (url.toString())
    }

    // Очистка всех параметров и сброс состояния
    fun reset() {
        baseUrl = ""
        route = ""
        pathParams.clear()
    }
}