package com.fiteats.app.ui.screens.goals

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.models.ActivityLevelTdee
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.models.WeeklyGoalModel
import com.fiteats.app.ui.custom.CustomDialog
import com.fiteats.app.ui.custom.DateFilters
import com.fiteats.app.ui.custom.LoadingDialog
import com.fiteats.app.ui.custom.OutlinedDatePicker
import com.fiteats.app.ui.custom.OutlinedNumberPicker
import com.fiteats.app.ui.custom.OutlinedSpinner
import com.fiteats.app.ui.screens.meal.NutritionInfo
import com.fiteats.app.ui.viewModel.AddWeeklyGoalViewModel
import com.fiteats.app.utils.removeTime
import java.util.Calendar
import java.util.Date

@Composable
fun AddWeeklyGoalScreen(navController: NavHostController, mainGoalModel: MainGoalModel) {
    val viewModel: AddWeeklyGoalViewModel = viewModel()
    val context = LocalContext.current

    val currentWeight: MutableState<Double?> =
        remember { mutableStateOf(mainGoalModel.startWeightInKg) }
    val currentFatPercentage: MutableState<Double?> =
        remember { mutableStateOf(mainGoalModel.startFatPercentage) }

    val startDate = remember { mutableStateOf<Date?>(null) }
    val endDate = remember { mutableStateOf<Date?>(null) }

    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val apiError by viewModel.apiError.observeAsState(initial = null)

    val tdee by viewModel.calorieExpenditure.observeAsState(initial = null)
    val macroGoal by viewModel.macroGoal.observeAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Weekly Goal") }, navigationIcon = {
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
            Text("Lets Calculate your Maintenance calories")

            OutlinedNumberPicker(
                label = "Current Weight (kg)",
                numberState = currentWeight,
                onNumberChanged = { newValue -> currentWeight.value = newValue },
                minValue = 40.0,
                maxValue = 200.0,
                precision = 1,
                modifier = Modifier.fillMaxWidth(),
                enabled = tdee == null && !isLoading
            )

            OutlinedNumberPicker(
                label = "Current Fat %",
                numberState = currentFatPercentage,
                onNumberChanged = { newValue -> currentFatPercentage.value = newValue },
                minValue = 15.0,
                maxValue = 80.0,
                precision = 1,
                modifier = Modifier.fillMaxWidth(),
                enabled = tdee == null && !isLoading
            )
            if (tdee == null) {
                Button(
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // Validate start fields
                        if (currentWeight.value != null || currentFatPercentage.value != null) {
                            viewModel.getTdee(
                                currentWeight.value!!,
                                currentFatPercentage.value!!,
                                mainGoalModel.targetWeightInKg!!,
                                mainGoalModel.targetFatPercentage!!,
                                mainGoalModel.goalType?.value!!
                            )
                        } else
                            Toast.makeText(
                                context,
                                "Please enter start weight and fat percentage.",
                                Toast.LENGTH_SHORT
                            ).show()
                    },
                ) {
                    Text("Next")
                }
            }

            val activityLevel = tdee.let { it?.tdee }
            val selectedOption: MutableState<ActivityLevelTdee?> =
                remember { mutableStateOf(null) }

            if (tdee != null) {
                Text("Your BMR is ${tdee!!.bmr} calories per day")
                Text("Select your energy expenditure according to your lifestyle")

                OutlinedSpinner(
                    label = "Select Lifestyle",
                    items = activityLevel!!,
                    modifier = Modifier.fillMaxWidth(),
                    selectedItem = selectedOption,
                    itemToString = { it ->
                        "${it.description} - ${it.tdee} Kcal"
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
                            add(Calendar.WEEK_OF_YEAR, 1)
                        }.time
                    },
                    minDate = DateFilters.today().first,
                    maxDate = DateFilters.today().second,
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
                if (macroGoal == null) {
                    Button(
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            try {
                                if (selectedOption.value == null) {
                                    Toast.makeText(
                                        context,
                                        "Please fill all fields correctly",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    viewModel.getGoalMacros(
                                        currentWeight.value!!,
                                        currentFatPercentage.value!!,
                                        mainGoalModel.targetWeightInKg!!,
                                        mainGoalModel.targetFatPercentage!!,
                                        mainGoalModel.goalType?.value!!,
                                        tdee!!.bmr,
                                        selectedOption.value!!.tdee,
                                        mainGoalModel.weeklyWeightChange!!
                                    )
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Invalid data format", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }) { Text("Next") }
                }
            }
            if (macroGoal != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        NutritionInfo(
                            macroGoal!!.dailyCalorieIntake,
                            macroGoal!!.macronutrientSplit.protein.totalGrams,
                            macroGoal!!.macronutrientSplit.carbohydrates.totalGrams,
                            macroGoal!!.macronutrientSplit.fat.totalGrams
                        )
                    }
                }
                Button(
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        try {
                            if (mainGoalModel.id == null ||
                                currentWeight.value == null || currentFatPercentage.value == null ||
                                startDate.value == null || endDate.value == null ||
                                macroGoal == null || selectedOption.value == null
                            ) {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields correctly",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val goal = WeeklyGoalModel(
                                    mainGoalId = mainGoalModel.id,
                                    currentWeightInKg = currentWeight.value,
                                    currentFatPercentage = currentFatPercentage.value,
                                    startDate = startDate.value?.removeTime(),
                                    endDate = endDate.value?.removeTime(),
                                    dailyMaintenanceCalories = selectedOption.value!!.tdee.toInt(),
                                    targetDailyCalories = macroGoal!!.dailyCalorieIntake.toInt(),
                                    targetDailyMacrosFats = macroGoal!!.macronutrientSplit.fat.totalGrams,
                                    targetDailyMacrosCarbs = macroGoal!!.macronutrientSplit.carbohydrates.totalGrams,
                                    targetDailyMacrosProtein = macroGoal!!.macronutrientSplit.protein.totalGrams
                                )
                                viewModel.registerWeeklyGoal(goal)
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
                LoadingDialog()
            }

            val finalSubmit by viewModel.finalSubmit.observeAsState(initial = null)
            if (finalSubmit == true) {
                CustomDialog(
                    title = "Goal Created",
                    message = "Lets Go! A weekly goal helps to track your daily calories. Lets create daily meal plans to further track nutrition.",
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
fun AddWeeklyGoalScreenPreview() {
    AddWeeklyGoalScreen(rememberNavController(), MainGoalModel())
}