package com.fiteats.app.ui.custom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun <T> OutlinedSpinner(
    label: String,
    items: List<T>,
    selectedItem: MutableState<T?>,
    onItemSelected: (T?) -> Unit,
    itemToString: (T) -> String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = selectedItem.value?.let { itemToString(it) } ?: "",
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Open dropdown",
                    modifier = Modifier.clickable { showDialog = true }
                )
            }
        )

        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn {
                            items(items) { item ->
                                Text(
                                    text = itemToString(item),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem.value = item
                                            onItemSelected(item)
                                            showDialog = false
                                        }
                                        .padding(vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OutlinedSpinnerPreview() {
    val items = listOf("Option 1", "Option 2", "Option 3")
    val selectedItem = remember { mutableStateOf<String?>("Option 1") }

    Column {
        OutlinedSpinner(
            label = "Select an Option",
            items = items,
            selectedItem = selectedItem,
            itemToString = { it ->
                it
            },
            onItemSelected = { item ->
                println("Selected item: $item")
            }
        )
    }
}