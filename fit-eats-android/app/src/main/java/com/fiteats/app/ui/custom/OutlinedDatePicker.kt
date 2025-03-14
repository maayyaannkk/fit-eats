package com.fiteats.app.ui.custom

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun OutlinedDatePicker(
    label: String,
    dateState: MutableState<Date?>,
    onDateSelected: (Date) -> Unit,
    minDate: Date? = null,
    maxDate: Date? = null,
    isDisabled: Boolean = false
) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.time
            dateState.value = selectedDate
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    minDate?.let { datePickerDialog.datePicker.minDate = it.time }
    maxDate?.let { datePickerDialog.datePicker.maxDate = it.time }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = dateState.value?.let { dateFormatter.format(it) } ?: "",
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                if (isDisabled) return@IconButton
                // Set the DatePickerDialog's initial date to the current value of dateState
                dateState.value?.let {
                    val cal = Calendar.getInstance()
                    cal.time = it
                    datePickerDialog.updateDate(
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    )
                }
                datePickerDialog.show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }
        }
    )
}

object DateFilters {
    fun today(): Pair<Date, Date> {
        val today = Calendar.getInstance()
        val startOfDay = Calendar.getInstance().apply {
            time = today.time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            time = today.time
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        return Pair(startOfDay, endOfDay)
    }

    fun yesterday(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startOfDay = Calendar.getInstance().apply {
            time = calendar.time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            time = calendar.time
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
        return Pair(startOfDay, endOfDay)
    }

    fun lastWeek(): Pair<Date, Date> {
        val today = Calendar.getInstance()
        val endOfWeek = Calendar.getInstance().apply {
            time = today.time
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startOfWeek = Calendar.getInstance().apply {
            time = calendar.time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return Pair(startOfWeek, endOfWeek)
    }

    fun oneMonth(): Pair<Date, Date> {
        val future = Calendar.getInstance()
        future.add(Calendar.DAY_OF_YEAR, 15)
        val endOfWeek = Calendar.getInstance().apply {
            time = future.time
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -15)
        val startOfWeek = Calendar.getInstance().apply {
            time = calendar.time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return Pair(startOfWeek, endOfWeek)
    }
}