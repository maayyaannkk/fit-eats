package com.fiteats.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiteats.app.models.WeeklyGoalModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun WeeklyGoalCard(weeklyGoal: WeeklyGoalModel) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val startDate = weeklyGoal.startDate ?: Date()
    val endDate = weeklyGoal.endDate ?: Date()
    val startDateFormatted = dateFormatter.format(startDate)
    val endDateFormatted = dateFormatter.format(endDate)

    val currentDate = Date()
    val daysPassed = TimeUnit.DAYS.convert(currentDate.time - startDate.time, TimeUnit.MILLISECONDS)
    val daysRemaining =
        TimeUnit.DAYS.convert(endDate.time - currentDate.time, TimeUnit.MILLISECONDS)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Event,
                    contentDescription = "Weekly Goal Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Weekly Progress",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = {
                    (daysPassed.toFloat() / (daysPassed + daysRemaining).toFloat())
                        .coerceIn(0f, 1f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateInfo(label = "Start Date", date = startDateFormatted)
                DateInfo(label = "End Date", date = endDateFormatted)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GoalInfoItem(
                    label = "Current Weight",
                    value = "${weeklyGoal.currentWeightInKg ?: "-"} kg",
                    icon = Icons.Filled.Scale
                )
                GoalInfoItem(
                    label = "Current Fat %",
                    value = "${weeklyGoal.currentFatPercentage ?: "-"}%",
                    icon = Icons.Filled.PieChart
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GoalInfoItem(
                    label = "Maintenance Calories",
                    value = "${weeklyGoal.dailyMaintenanceCalories?.toInt() ?: "-"} kcal",
                    icon = Icons.Filled.Whatshot
                )
                GoalInfoItem(
                    label = "Target Calories",
                    value = "${weeklyGoal.targetDailyCalories?.toInt() ?: "-"} kcal",
                    icon = Icons.Filled.LocalFireDepartment
                )
            }

            MacrosRow(
                protein = weeklyGoal.targetDailyMacrosProtein,
                carbs = weeklyGoal.targetDailyMacrosCarbs,
                fats = weeklyGoal.targetDailyMacrosFats
            )
        }
    }
}

@Composable
fun MacrosRow(protein: Int?, carbs: Int?, fats: Int?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MacroItem(
            label = "Protein",
            value = "${protein?.toInt() ?: "-"}g",
            icon = Icons.Filled.FitnessCenter
        )
        MacroItem(
            label = "Carbs",
            value = "${carbs?.toInt() ?: "-"}g",
            icon = Icons.Filled.BakeryDining
        )
        MacroItem(label = "Fats", value = "${fats?.toInt() ?: "-"}g", icon = Icons.Filled.OilBarrel)
    }
}

@Composable
fun MacroItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
fun WeeklyGoalCardPreview() {
    WeeklyGoalCard(
        weeklyGoal = WeeklyGoalModel(
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)),
            currentWeightInKg = 75.5,
            currentFatPercentage = 23.2,
            dailyMaintenanceCalories = 2500,
            targetDailyCalories = 2000,
            targetDailyMacrosProtein = 150,
            targetDailyMacrosCarbs = 100,
            targetDailyMacrosFats = 70
        )
    )
}