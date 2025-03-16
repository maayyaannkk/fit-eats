package com.fiteats.app.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiteats.app.ui.components.MainGoalCard
import com.fiteats.app.ui.viewModel.UserGoalViewModel

@Composable
fun MyGoalsScreen(onAddWeeklyGoal: () -> Unit, onAddMainGoal: () -> Unit) {
    val viewModel: UserGoalViewModel = viewModel()
    val mainGoal by viewModel.userGoals.observeAsState(initial = null)

    LaunchedEffect(Unit) { viewModel.getGoals() }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (mainGoal == null) onAddMainGoal()
                else onAddWeeklyGoal()
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