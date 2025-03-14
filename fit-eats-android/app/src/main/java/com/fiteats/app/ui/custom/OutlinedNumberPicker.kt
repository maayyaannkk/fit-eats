package com.fiteats.app.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedNumberPicker(
    label: String,
    numberState: MutableState<Double?>,
    onNumberChanged: (Double?) -> Unit,
    minValue: Double,
    maxValue: Double,
    precision: Int = 2,
    modifier: Modifier = Modifier
) {
    var errorText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = numberState.value?.let {
                //Format only if there are decimal places, otherwise remove trailing zeros
                if (it == it.toLong().toDouble()) {
                    it.toLong().toString()
                } else {
                    String.format("%.${precision}f", it)
                }
            } ?: "",
            onValueChange = { newValue ->
                try {
                    if (newValue.isEmpty()) {
                        numberState.value = null
                        onNumberChanged(null)
                        errorText = null
                    } else {
                        val parsedValue = newValue.toDouble()
                        if (parsedValue in minValue..maxValue) {
                            numberState.value = parsedValue
                            onNumberChanged(parsedValue)
                            errorText = null
                        } else {
                            numberState.value = parsedValue //Still keep the value on number state
                            onNumberChanged(parsedValue) //Still keep the value on number state
                            errorText = "Value must be between ${
                                String.format(
                                    "%.${precision}f",
                                    minValue
                                )
                            } and ${String.format("%.${precision}f", maxValue)}"
                        }
                    }
                } catch (e: NumberFormatException) {
                    numberState.value = null
                    onNumberChanged(null)
                    errorText = "Invalid number"
                }
            },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = errorText != null
        )

        if (errorText != null) {
            Text(
                text = errorText!!,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error
            )
        }
    }
}