package com.fiteats.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatToString(format: String, locale: Locale = Locale.getDefault()): String? {
    return try {
        val simpleDateFormat = SimpleDateFormat(format, locale)
        simpleDateFormat.format(this)
    } catch (e: IllegalArgumentException) {
        // Handle invalid format string
        println("Invalid date format: ${e.message}") // Or log the error
        null
    }
}

fun Date.toDDMMM(): String?{
    return formatToString("dd, MMM")
}
fun Date.toDDMMMYYYY(): String?{
    return formatToString("dd, MMM YYYY")
}