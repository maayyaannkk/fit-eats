package com.fiteats.app.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiteats.app.ui.viewModel.UserGoalViewModel

@Composable
fun MyGoalsScreen(onAddWeeklyGoal: () -> Unit, onAddMainGoal: () -> Unit) {
    val viewModel: UserGoalViewModel = viewModel()
    val goals by viewModel.userGoals.observeAsState(initial = null)

    LaunchedEffect(Unit) { viewModel.getGoals() }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                //if no main goal is set?
                onAddMainGoal()
                //else start weekly
            }) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "My Goals Screen", modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun MyGoalsScreenPreview() {
    MyGoalsScreen({}, {})
}