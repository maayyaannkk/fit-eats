package com.fiteats.app.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.models.UserGoalModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//TODO remove this
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(navController: NavController) {
    val context = LocalContext.current
    val goalDuration = remember { mutableStateOf("") }
    val currentWeight = remember { mutableStateOf("") }
    val currentFatPercentage = remember { mutableStateOf("") }
    val dailyMaintenanceCalories = remember { mutableStateOf("") }
    val targetDailyCalories = remember { mutableStateOf("") }
    val targetProtein = remember { mutableStateOf("") }
    val targetCarbs = remember { mutableStateOf("") }
    val targetFats = remember { mutableStateOf("") }
    val workoutRoutine = remember { mutableStateOf("") }

    val startDate = remember { mutableStateOf("") }
    val endDate = remember { mutableStateOf("") }

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
                value = goalDuration.value,
                onValueChange = { goalDuration.value = it },
                label = { Text("Goal Duration (weeks)") })
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = currentWeight.value,
                onValueChange = { currentWeight.value = it },
                label = { Text("Current Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = currentFatPercentage.value,
                onValueChange = { currentFatPercentage.value = it },
                label = { Text("Current Fat %") },
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

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dailyMaintenanceCalories.value,
                onValueChange = { dailyMaintenanceCalories.value = it },
                label = { Text("Daily Maintenance Calories") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = targetDailyCalories.value,
                onValueChange = { targetDailyCalories.value = it },
                label = { Text("Target Daily Calories") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = targetProtein.value,
                onValueChange = { targetProtein.value = it },
                label = { Text("Protein (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = targetCarbs.value,
                onValueChange = { targetCarbs.value = it },
                label = { Text("Carbs (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = targetFats.value,
                onValueChange = { targetFats.value = it },
                label = { Text("Fats (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5,
                value = workoutRoutine.value,
                onValueChange = { workoutRoutine.value = it },
                label = { Text("Workout Routine") }
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    try {
                        val goal = UserGoalModel(
                            goalDuration = goalDuration.value,
                            currentWeightInKg = currentWeight.value.toDoubleOrNull(),
                            currentFatPercentage = currentFatPercentage.value.toDoubleOrNull(),
                            goalStartDate = dateFormatter.parse(startDate.value),
                            goalEndDate = dateFormatter.parse(endDate.value),
                            dailyMaintenanceCalories = dailyMaintenanceCalories.value.toDoubleOrNull(),
                            targetDailyCalories = targetDailyCalories.value.toDoubleOrNull(),
                            targetDailyMacrosProtein = targetProtein.value.toDoubleOrNull(),
                            targetDailyMacrosCarbs = targetCarbs.value.toDoubleOrNull(),
                            targetDailyMacrosFats = targetFats.value.toDoubleOrNull(),
                            workoutRoutine = workoutRoutine.value
                        )

                        if (goal.currentWeightInKg == null || goal.currentFatPercentage == null ||
                            goal.goalStartDate == null || goal.goalEndDate == null ||
                            goal.dailyMaintenanceCalories == null || goal.targetDailyCalories == null ||
                            goal.targetDailyMacrosProtein == null || goal.targetDailyMacrosCarbs == null ||
                            goal.targetDailyMacrosFats == null || workoutRoutine.value.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "Please fill all fields correctly",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            navController.popBackStack()
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
fun AddGoalScreenPreview() {
    AddGoalScreen(rememberNavController())
}