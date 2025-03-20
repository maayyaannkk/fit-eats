package com.fiteats.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiteats.app.models.WeeklyGoalModel

@Composable
fun MacroCard(weeklyGoalModel: WeeklyGoalModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GoalInfoItem(
                    label = "Maintenance Calories",
                    value = "${weeklyGoalModel.dailyMaintenanceCalories?.toInt()} kcal",
                    icon = Icons.Filled.LocalFireDepartment
                )
                GoalInfoItem(
                    label = "Target Calories",
                    value = "${weeklyGoalModel.targetDailyCalories?.toInt()} kcal",
                    icon = Icons.Filled.Whatshot
                )
            }

            MacrosRow(
                protein = weeklyGoalModel.targetDailyMacrosProtein,
                carbs = weeklyGoalModel.targetDailyMacrosCarbs,
                fats = weeklyGoalModel.targetDailyMacrosFats,
            )
        }
    }
}

@Composable
@Preview
fun MacroCardPreview() {
    MacroCard(WeeklyGoalModel())
}