package com.fiteats.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiteats.app.models.GoalType
import com.fiteats.app.models.MainGoalModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun MainGoalCard(goal: MainGoalModel) {
    val currentDate = Date()
    val startDate = goal.goalStartDate ?: currentDate
    val endDate = goal.goalEndDate ?: currentDate

    val daysPassed = TimeUnit.DAYS.convert(currentDate.time - startDate.time, TimeUnit.MILLISECONDS)
    val daysRemaining =
        TimeUnit.DAYS.convert(endDate.time - currentDate.time, TimeUnit.MILLISECONDS)

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val startDateFormatted = dateFormatter.format(startDate)
    val endDateFormatted = dateFormatter.format(endDate)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
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
                    imageVector = Icons.Filled.FitnessCenter,
                    contentDescription = "Goal Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                goal.goalType?.let {
                    Text(
                        text = it.value + " Journey",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                    label = "Start Weight",
                    value = "${goal.startWeightInKg ?: "-"} kg",
                    icon = Icons.Filled.CheckCircle
                )
                GoalInfoItem(
                    label = "Target Weight",
                    value = "${goal.targetWeightInKg ?: "-"} kg",
                    icon = Icons.Filled.CheckCircle
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GoalInfoItem(
                    label = "Start Fat %",
                    value = "${goal.startFatPercentage ?: "-"}%",
                    icon = Icons.Filled.CheckCircle
                )
                GoalInfoItem(
                    label = "Target Fat %",
                    value = "${goal.targetFatPercentage ?: "-"}%",
                    icon = Icons.Filled.CheckCircle
                )
            }
        }
    }

    if (!goal.weeklyGoals.isNullOrEmpty()) {
        Text(
            text = "Weekly Goals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 200.dp) // Limit the height to prevent excessive scrolling
        ) {
            items(goal.weeklyGoals) {
                WeeklyGoalCard(weeklyGoal = it)
            }
        }
    }
}

@Composable
fun DateInfo(
    label: String,
    date: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.CalendarToday,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun GoalInfoItem(label: String, value: String, icon: ImageVector) {
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
fun MainGoalCardPreview() {
    MainGoalCard(
        goal = MainGoalModel(
            goalType = GoalType.MUSCLE_GAIN,
            startWeightInKg = 80.0,
            targetWeightInKg = 70.0,
            startFatPercentage = 25.0,
            targetFatPercentage = 20.0,
            goalStartDate = Date(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)), // A week ago
            goalEndDate = Date(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000))  // A month from now
        )
    )
}