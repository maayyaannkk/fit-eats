package com.fiteats.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
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
                        )
                        Text(
                            text = "${mainGoal.startWeightInKg} kg", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Target Weight",
                            fontSize = 14.sp,
                        )
                        Text(
                            text = "${mainGoal.targetWeightInKg} kg", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
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
                        )
                        Text(
                            text = "${mainGoal.startFatPercentage}%", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Target Body Fat",
                            fontSize = 14.sp,
                        )
                        Text(
                            text = "${mainGoal.targetFatPercentage}%", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
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
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Ends: ${mainGoal.goalEndDate?.toDDMMMYYYY()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!mainGoal.weeklyGoals.isNullOrEmpty())
            LazyColumn {
                items(mainGoal.weeklyGoals.sortedByDescending { it.startDate }, key = { it.id!! }) {
                    WeeklyGoalCard(it)
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
