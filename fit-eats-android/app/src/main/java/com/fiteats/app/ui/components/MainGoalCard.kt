package com.fiteats.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.models.WeeklyGoalModel
import com.fiteats.app.utils.toDDMMM
import com.fiteats.app.utils.toDDMMMYYYY
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun MainGoalCard(mainGoal: MainGoalModel) {
    val currentDate = Date()
    val startDate = mainGoal.goalStartDate ?: currentDate
    val endDate = mainGoal.goalEndDate ?: currentDate

    val daysPassed = TimeUnit.DAYS.convert(currentDate.time - startDate.time, TimeUnit.MILLISECONDS)
    val daysRemaining =
        TimeUnit.DAYS.convert(endDate.time - currentDate.time, TimeUnit.MILLISECONDS)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Current Goal Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                mainGoal.goalType?.let {
                    Text(
                        text = it.value + " Journey",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Start Weight",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "${mainGoal.startWeightInKg} kg", fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Target Weight",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "${mainGoal.targetWeightInKg} kg", fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Start Body Fat",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "${mainGoal.startFatPercentage}%", fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Target Body Fat",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "${mainGoal.targetFatPercentage}%", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Started: ${mainGoal.goalStartDate?.toDDMMMYYYY()}",
                            fontSize = 14.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Ends: ${mainGoal.goalEndDate?.toDDMMMYYYY()}",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekly Goal Card
        mainGoal.weeklyGoals?.firstOrNull()?.let { weeklyGoal ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Weekly Goal",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(50) // Circular background
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 2.dp
                                    ) // Adjust padding for circular effect
                            ) {
                                Text(
                                    text = "In Progress",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GoalBox(
                            modifier = Modifier.weight(1f),
                            "Calories",
                            "${weeklyGoal.targetDailyCalories}",
                            Color(0xFFE3EFFF)
                        )
                        GoalBox(
                            modifier = Modifier.weight(1f),
                            "Protein",
                            "${weeklyGoal.targetDailyMacrosProtein}g",
                            Color(0xFFE3FFEB)
                        )
                        GoalBox(
                            modifier = Modifier.weight(1f),
                            "Carbs",
                            "${weeklyGoal.targetDailyMacrosCarbs}g",
                            Color(0xFFFFF5E3)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Week of ${weeklyGoal.startDate?.toDDMMM()} - ${weeklyGoal.endDate?.toDDMMM()}",
                        fontSize = 14.sp
                    )

                }
            }
        }
    }
}

@Composable
fun GoalBox(modifier: Modifier, label: String, value: String, backgroundColor: Color) {
    Box(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview
@Composable
fun GoalsScreenPreview() {
    MainGoalCard(MainGoalModel(weeklyGoals = listOf(WeeklyGoalModel())))
}
