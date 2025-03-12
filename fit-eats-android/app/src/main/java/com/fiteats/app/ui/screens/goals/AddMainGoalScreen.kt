package com.fiteats.app.ui.screens.goals

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.models.GoalType
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.ui.viewModel.UserGoalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMainGoalScreen(navController: NavController) {
    val viewModel: UserGoalViewModel = viewModel()

    val context = LocalContext.current

    val startWeight = remember { mutableStateOf("") }
    val startFatPercentage = remember { mutableStateOf("") }
    val targetWeight = remember { mutableStateOf("") }
    val targetFatPercentage = remember { mutableStateOf("") }

    val startDate = remember { mutableStateOf("") }
    val endDate = remember { mutableStateOf("") }

    val goalType = remember { mutableStateOf(GoalType.FAT_LOSS) }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            startDate.value = dateFormatter.format(calendar.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            endDate.value = dateFormatter.format(calendar.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Your Goal") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = startWeight.value,
                onValueChange = { startWeight.value = it },
                label = { Text("Start Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = startFatPercentage.value,
                onValueChange = { startFatPercentage.value = it },
                label = { Text("Start Fat %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = targetWeight.value,
                onValueChange = { targetWeight.value = it },
                label = { Text("Target Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = targetFatPercentage.value,
                onValueChange = { targetFatPercentage.value = it },
                label = { Text("Target Fat %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = startDate.value,
                onValueChange = {},
                label = { Text("Start Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { startDatePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Start Date")
                    }
                })

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = endDate.value,
                onValueChange = {},
                label = { Text("End Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { endDatePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select End Date")
                    }
                })

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    try {
                        val goal = MainGoalModel(
                            startWeightInKg = startWeight.value.toDoubleOrNull(),
                            startFatPercentage = startFatPercentage.value.toDoubleOrNull(),
                            targetWeightInKg = targetWeight.value.toDoubleOrNull(),
                            targetFatPercentage = targetFatPercentage.value.toDoubleOrNull(),
                            goalStartDate = dateFormatter.parse(startDate.value),
                            goalEndDate = dateFormatter.parse(endDate.value),
                            goalType = goalType.value
                        )

                        if (goal.startWeightInKg == null || goal.startFatPercentage == null ||
                            goal.targetWeightInKg == null || goal.targetFatPercentage == null ||
                            goal.goalStartDate == null || goal.goalEndDate == null
                        ) {
                            Toast.makeText(
                                context,
                                "Please fill all fields correctly",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            //navController.popBackStack()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Invalid data format", Toast.LENGTH_LONG).show()
                    }
                }) {
                Text("Submit")
            }
        }
    }
}

@Preview
@Composable
fun AddMainGoalScreenPreview() {
    AddMainGoalScreen(rememberNavController())
}
