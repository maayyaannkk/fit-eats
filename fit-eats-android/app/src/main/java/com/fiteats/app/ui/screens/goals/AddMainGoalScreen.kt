package com.fiteats.app.ui.screens.goals

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.models.Pace
import com.fiteats.app.ui.custom.CustomDialog
import com.fiteats.app.ui.custom.DateFilters
import com.fiteats.app.ui.custom.OutlinedDatePicker
import com.fiteats.app.ui.custom.OutlinedNumberPicker
import com.fiteats.app.ui.custom.OutlinedSpinner
import com.fiteats.app.ui.viewModel.AddMainGoalViewModel
import com.fiteats.app.utils.UserUtils
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMainGoalScreen(navController: NavController) {
    val viewModel: AddMainGoalViewModel = viewModel()
    val context = LocalContext.current

    val currentWeight: MutableState<Double?> = remember { mutableStateOf(null) }
    val currentFatPercentage: MutableState<Double?> = remember { mutableStateOf(null) }
    val targetWeight: MutableState<Double?> = remember { mutableStateOf(null) }
    val targetFatPercentage: MutableState<Double?> = remember { mutableStateOf(null) }

    val startDate = remember { mutableStateOf<Date?>(null) }
    val endDate = remember { mutableStateOf<Date?>(null) }

    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val apiError by viewModel.apiError.observeAsState(initial = null)

    val idealWeight by viewModel.idealWeight.observeAsState(initial = null)
    val goalDuration by viewModel.goalDuration.observeAsState(initial = null)

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
            Text("Lets Calculate your ideal weight")

            OutlinedNumberPicker(
                label = "Current Weight (kg)",
                numberState = currentWeight,
                onNumberChanged = { newValue -> currentWeight.value = newValue },
                minValue = 40.0,
                maxValue = 200.0,
                precision = 1,
                modifier = Modifier.fillMaxWidth(),
                enabled = idealWeight == null && !isLoading
            )

            OutlinedNumberPicker(
                label = "Current Fat %",
                numberState = currentFatPercentage,
                onNumberChanged = { newValue -> currentFatPercentage.value = newValue },
                minValue = 15.0,
                maxValue = 80.0,
                precision = 1,
                modifier = Modifier.fillMaxWidth(),
                enabled = idealWeight == null && !isLoading
            )

            if (idealWeight == null) {
                Button(
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // Validate start fields
                        if (currentWeight.value == null || currentFatPercentage.value == null ||
                            currentWeight.value!! !in (40.0..200.0) || currentFatPercentage.value!! !in (15.0..80.0)
                        ) {
                            Toast.makeText(
                                context,
                                "Please enter start weight and fat percentage.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Call API to get ideal weight
                            val weight = currentWeight.value
                            val fat = currentFatPercentage.value
                            if (weight != null && fat != null) {
                                viewModel.getIdealWeight(
                                    weight,
                                    fat
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter valid start weight and fat percentage.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                ) {
                    Text("Next")
                }
            }
            if (idealWeight != null) {
                Text("Your ideal weight should be between ${idealWeight?.lowerBound?.weightInKg!!}kg and ${idealWeight?.upperBound?.weightInKg!!}kg")

                OutlinedNumberPicker(
                    label = "Target Weight (kg)",
                    numberState = targetWeight,
                    onNumberChanged = { newValue -> targetWeight.value = newValue },
                    minValue = idealWeight?.lowerBound?.weightInKg!!,
                    maxValue = idealWeight?.upperBound?.weightInKg!!,
                    precision = 1,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = goalDuration == null && !isLoading
                )

                OutlinedNumberPicker(
                    label = "Target Fat %",
                    numberState = targetFatPercentage,
                    onNumberChanged = { newValue -> targetFatPercentage.value = newValue },
                    minValue = idealWeight?.lowerBound?.fatPercentage!!,
                    maxValue = idealWeight?.upperBound?.fatPercentage!!,
                    precision = 1,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = goalDuration == null && !isLoading
                )
                if (goalDuration == null) {
                    Button(
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (currentWeight.value != null && targetWeight.value != null &&
                                currentFatPercentage.value != null && targetFatPercentage.value != null &&
                                targetWeight.value!! in (idealWeight?.lowerBound?.weightInKg!!..idealWeight?.upperBound?.weightInKg!!) &&
                                targetFatPercentage.value!! in (idealWeight?.lowerBound?.fatPercentage!!..idealWeight?.upperBound?.fatPercentage!!)
                            ) {
                                viewModel.getGoalDuration(
                                    currentWeight.value!!,
                                    currentFatPercentage.value!!,
                                    targetWeight.value!!,
                                    targetFatPercentage.value!!
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter valid start or target weight and fat percentage.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    ) {
                        Text("Next")
                    }
                }
            }

            if (goalDuration != null) {
                Text("Lets set a duration to get to your healthy weight")

                val durationOptions = mutableListOf<Pace>()
                goalDuration?.let {
                    durationOptions.add(
                        Pace(
                            weeklyWeightChangeKg = it.paceOptions.slow.weeklyWeightChangeKg,
                            durationWeeks = it.paceOptions.slow.durationWeeks,
                            notes = it.paceOptions.slow.notes,
                            type = "Slow",
                            goalType = it.type
                        )
                    )
                    durationOptions.add(
                        Pace(
                            weeklyWeightChangeKg = it.paceOptions.medium.weeklyWeightChangeKg,
                            durationWeeks = it.paceOptions.medium.durationWeeks,
                            notes = it.paceOptions.medium.notes,
                            type = "Medium",
                            goalType = it.type
                        )
                    )
                    durationOptions.add(
                        Pace(
                            weeklyWeightChangeKg = it.paceOptions.fast.weeklyWeightChangeKg,
                            durationWeeks = it.paceOptions.fast.durationWeeks,
                            notes = it.paceOptions.fast.notes,
                            type = "Fast",
                            goalType = it.type
                        )
                    )
                }
                val selectedOption: MutableState<Pace?> = remember { mutableStateOf(null) }

                OutlinedSpinner(
                    label = "Select Goal Duration",
                    items = durationOptions,
                    modifier = Modifier.fillMaxWidth(),
                    selectedItem = selectedOption,
                    itemToString = { it ->
                        "${it.type} - ${it.durationWeeks} weeks, ${it.weeklyWeightChangeKg}kg per week, ${it.notes}"
                    },
                    onItemSelected = { item ->
                        selectedOption.value = item
                        startDate.value = null
                        endDate.value = null
                    }
                )

                OutlinedDatePicker(
                    label = "Start Date",
                    dateState = startDate,
                    onDateSelected = { it ->
                        endDate.value = Calendar.getInstance().apply {
                            time = it
                            add(Calendar.WEEK_OF_YEAR, selectedOption.value?.durationWeeks ?: 0)
                        }.time
                    },
                    minDate = DateFilters.oneMonth().first,
                    maxDate = DateFilters.oneMonth().second,
                    isDisabled = selectedOption.value == null
                )

                OutlinedDatePicker(
                    label = "End Date",
                    dateState = endDate,
                    onDateSelected = { },
                    minDate = DateFilters.oneMonth().first,
                    maxDate = DateFilters.oneMonth().second,
                    isDisabled = true
                )
                Button(
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        try {
                            val goal = MainGoalModel(
                                userId = UserUtils.getUser(context)?.id,
                                startWeightInKg = currentWeight.value,
                                startFatPercentage = currentFatPercentage.value,
                                targetWeightInKg = targetWeight.value,
                                targetFatPercentage = targetFatPercentage.value,
                                goalStartDate = startDate.value,
                                goalEndDate = endDate.value,
                                goalType = selectedOption.value?.goalType,
                                weeklyWeightChange = selectedOption.value?.weeklyWeightChangeKg!!
                            )

                            if (goal.startWeightInKg == null || goal.startFatPercentage == null ||
                                goal.targetWeightInKg == null || goal.targetFatPercentage == null ||
                                goal.goalStartDate == null || goal.goalEndDate == null || goal.goalType == null
                            ) {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields correctly",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                viewModel.registerNewGoal(goal)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid data format", Toast.LENGTH_LONG)
                                .show()
                        }
                    }) { Text("Submit") }
            }

            // Observe API Error
            apiError?.let { error ->
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }

            // Show Loading Indicator
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            val finalSubmit by viewModel.finalSubmit.observeAsState(initial = null)
            if (finalSubmit == true) {
                CustomDialog(
                    title = "Goal Created",
                    message = "Lets Go! A main goal helps to track your progress. Lets create weekly goals to further track meals and progress",
                    showNegativeButton = false,
                    onDismissRequest = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun AddMainGoalScreenPreview() {
    AddMainGoalScreen(rememberNavController())
}