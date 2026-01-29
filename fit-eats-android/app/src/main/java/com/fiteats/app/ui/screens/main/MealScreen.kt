package com.fiteats.app.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiteats.app.models.Meal
import com.fiteats.app.ui.components.MealPlanCard
import com.fiteats.app.ui.custom.LoadingDialog
import com.fiteats.app.ui.viewModel.MealPlanViewModel

@Composable
fun MealScreen(onMealClick: (Meal) -> Unit) {
    val viewModel: MealPlanViewModel = viewModel()
    val context = LocalContext.current

    val mealPlan by viewModel.mealPlan.collectAsState(initial = null)
    val userGoal by viewModel.userGoal.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val apiError by viewModel.apiError.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.getActiveGoal()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meals") },
                windowInsets = WindowInsets(0)
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize()
        ) {
            if (userGoal != null && !userGoal!!.weeklyGoals.isNullOrEmpty()) {
                if (mealPlan != null) {
                    MealPlanCard(
                        mealPlan!!,
                        onMealClick = {
                            onMealClick(it)
                        },
                        onDayMealEdit = { dayMeal, userPrompt ->
                            viewModel.customizeMealPlan(mealPlan!!.id!!, dayMeal.id!!, userPrompt)
                        },
                        onDayMealConsume = { mealId ->
                            viewModel.consumeMeal(mealId)
                        }
                    )
                } else {
                    val showDialog = remember { mutableStateOf(false) }
                    if (showDialog.value)
                        CreateMealDialog(onConfirm = {
                            showDialog.value = false
                            viewModel.createMealPlan(
                                userGoal!!.id!!,
                                userGoal!!.weeklyGoals!![0].id!!,
                                it
                            )
                        }) { showDialog.value = false }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally // Add this line
                    ) {
                        Button(
                            onClick = { showDialog.value = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Create Meal Plan")
                        }
                    }
                }

            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally // Add this line
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), // Use fillMaxWidth and add padding for better readability
                        text = "Create a weekly goal before planning meals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LaunchedEffect(apiError) {
                if (apiError != null)
                    Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
            }

            // Show Loading Indicator
            if (isLoading) {
                LoadingDialog()
            }
        }
    }
}

@Composable
fun CreateMealDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dialogText = remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(5.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Create Meals",
                fontSize = 16.sp
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    placeholder = {
                        Text(
                            "Leave blank, or add any special requests\n" +
                                    "I like mangoes.\n" +
                                    "I am allergic to nuts, dairy, gluten.\n" +
                                    "Use Air-fried, grilled, boiled, etc.\n" +
                                    "Use only affordable ingredients.",
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp
                        )
                    },
                    value = dialogText.value,
                    onValueChange = { input ->
                        val filteredText = input.filter { it.isLetter() || it.isWhitespace() }
                        dialogText.value = filteredText
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(dialogText.value)
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun MealScreenPreview() {
    MealScreen {}
}