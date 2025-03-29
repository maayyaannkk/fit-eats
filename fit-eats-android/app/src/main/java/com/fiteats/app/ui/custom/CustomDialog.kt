package com.fiteats.app.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomDialog(
    title: String,
    message: String,
    positiveButtonText: String = "OK",
    negativeButtonText: String = "Cancel",
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {}, // Optional negative click action
    showNegativeButton: Boolean = true, // Configurable option to hide negative button
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            // Use Column to reverse the button order
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (showNegativeButton) {
                        Button(
                            onClick = {
                                onNegativeClick()
                                onDismissRequest()
                            }
                        ) {
                            Text(negativeButtonText)
                        }
                        Spacer(modifier = Modifier.width(8.dp)) // Add space between buttons
                    }

                    Button(
                        onClick = {
                            onPositiveClick()
                            onDismissRequest()
                        }
                    ) {
                        Text(positiveButtonText)
                    }
                }
            }
        }
    )
}