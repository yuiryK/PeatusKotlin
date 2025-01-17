package com.example.peatus

enum class ApiEndPoint(val baseUrl: String) {
    REGIONS("https://peatus.metaler.com.ua/regions"),
    STOPS("https://peatus.metaler.com.ua/stops"),
    BUSES("https://peatus.metaler.com.ua/buses"),
    GEOLOCATION("https://peatus.metaler.com.ua/geolocation"),
    BUSTIME("https://peatus.metaler.com.ua/bustime"),
    STOPTIME("https://peatus.metaler.com.ua/stoptime")
}