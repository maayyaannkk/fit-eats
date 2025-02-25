package com.fiteats.app.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

private class SafeDateDeserializer : JsonDeserializer<Date?> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Date? {
        return try {
            val dateStr = json.asString
            if (dateStr.isNullOrBlank()) null else dateFormat.parse(dateStr)
        } catch (e: Exception) {
            null // Skip invalid date formats
        }
    }
}

private class SafeStringDeserializer : JsonDeserializer<String?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String? {
        val str = json.asString
        return if (str.isNullOrBlank()) null else str
    }
}

object GsonUtil {
    val gson = GsonBuilder()
        .registerTypeAdapter(
            Date::class.java,
            SafeDateDeserializer()
        )    // Skips empty or invalid dates
        .registerTypeAdapter(String::class.java, SafeStringDeserializer()) // Skips empty strings
        .create()
}