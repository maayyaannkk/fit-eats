package com.fiteats.app.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiteats.app.ui.components.MacroCard
import com.fiteats.app.ui.components.MealPlanList
import com.fiteats.app.ui.viewModel.MealPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen() {
    val viewModel: MealPlanViewModel = viewModel()
    val context = LocalContext.current

    val mealPlan by viewModel.mealPlan.observeAsState(initial = null)
    val userGoal by viewModel.userGoal.observeAsState(initial = null)
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val apiError by viewModel.apiError.observeAsState(initial = null)

    LaunchedEffect(Unit) { viewModel.getActiveGoal() }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (userGoal != null && !userGoal!!.weeklyGoals.isNullOrEmpty()) {
            MacroCard(userGoal!!.weeklyGoals!![0])

            if (mealPlan != null) {
                MealPlanList(mealPlan!!) { dayMeal, userPrompt ->
                    viewModel.customizeMealPlan(mealPlan!!.id!!, dayMeal.id!!, userPrompt)
                }
            } else {
                Button(onClick = {
                    viewModel.createMealPlan(userGoal!!.id!!, userGoal!!.weeklyGoals!![0].id!!)
                }) {
                    Text("Create Meal Plan")
                }
            }

        } else {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = "Create a weekly goal before planning meals",
                fontSize = 18.sp
            )
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
    }
}

@Preview
@Composable
fun MealScreenPreview() {
    MealScreen()
}