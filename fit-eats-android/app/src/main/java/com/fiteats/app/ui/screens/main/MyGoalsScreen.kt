package com.fiteats.app.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.ui.components.MainGoalCard
import com.fiteats.app.ui.viewModel.UserGoalViewModel
import java.util.Date

@Composable
fun MyGoalsScreen(onAddWeeklyGoal: (MainGoalModel) -> Unit, onAddMainGoal: () -> Unit) {
    val viewModel: UserGoalViewModel = viewModel()
    val mainGoal by viewModel.userGoals.observeAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.getGoals() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals") },
                windowInsets = WindowInsets(0)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (mainGoal == null) onAddMainGoal()
                else {
                    if (!mainGoal?.weeklyGoals.isNullOrEmpty())
                        for (weeklyGoal in mainGoal?.weeklyGoals!!) {
                            if (Date() <= weeklyGoal.endDate) {
                                Toast.makeText(
                                    context,
                                    "You already have an active weekly goal",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@FloatingActionButton
                            }
                        }
                    onAddWeeklyGoal(mainGoal!!)
                }
            }) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (mainGoal != null)
                MainGoalCard(mainGoal!!)
        }
    }
}

@Preview
@Composable
fun MyGoalsScreenPreview() {
    MyGoalsScreen({}, {})
}