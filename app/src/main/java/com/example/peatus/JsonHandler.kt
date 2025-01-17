package com.example.peatus

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonHandler (private val gson: Gson = Gson()){
    fun deserializeDynamic(json: String): List<Map<String, Any>> {
        val type = object : TypeToken<List<Map<String, Any>>>() {}.type
        return gson.fromJson(json, type)
    }

    fun displayDynamicItems(items: List<Map<String, Any>>) {
        items.forEach { item ->
            println("Item:")
            item.forEach { (key, value) ->
                println("  $key: $value")
            }
        }
    }
}