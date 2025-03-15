package com.fiteats.app.utils

import com.fiteats.app.models.GoalType
import com.google.gson.*
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private class GoalTypeAdapter : JsonDeserializer<GoalType>, JsonSerializer<GoalType> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): GoalType {
        return try {
            when (json?.asString) {
                "Fat loss" -> GoalType.FAT_LOSS
                "Muscle gain" -> GoalType.MUSCLE_GAIN
                else -> throw JsonParseException("Invalid GoalType: ${json?.asString}")
            }
        } catch (e: Exception) {
            throw JsonParseException("Error parsing GoalType", e)
        }
    }

    override fun serialize(
        src: GoalType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(
            when (src) {
                GoalType.FAT_LOSS -> "Fat loss"
                GoalType.MUSCLE_GAIN -> "Muscle gain"
                else -> "Unknown"
            }
        )
    }
}

private class DateAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(toUTCDateString(src))
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        val dateString = json?.asString ?: throw JsonParseException("Date string is null")
        return try {
            fromUTCDateString(dateString)
        } catch (e: ParseException) {
            throw JsonParseException("Invalid date format: $dateString", e)
        }
    }

    private fun toUTCDateString(date: Date?): String? {
        if (date == null) return null
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }

    private fun fromUTCDateString(dateString: String): Date {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.parse(dateString)
    }
}
object GsonUtil {
    val gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateAdapter())
        .registerTypeAdapter(GoalType::class.java, GoalTypeAdapter())
        .create()
}