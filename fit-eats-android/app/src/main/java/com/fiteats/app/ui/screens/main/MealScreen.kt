package com.fiteats.app.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiteats.app.models.Meal
import com.fiteats.app.ui.components.MealPlanCard
import com.fiteats.app.ui.viewModel.MealPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

            LaunchedEffect(apiError) {
                if (apiError != null)
                    Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
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

}

@Preview
@Composable
fun MealScreenPreview() {
    MealScreen {}
}