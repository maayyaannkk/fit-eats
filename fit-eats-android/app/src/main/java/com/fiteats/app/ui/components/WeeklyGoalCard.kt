package com.fiteats.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiteats.app.models.WeeklyGoalModel
import com.fiteats.app.utils.removeTime
import com.fiteats.app.utils.toDDMMM
import java.util.Date

@Composable
fun WeeklyGoalCard(weeklyGoal: WeeklyGoalModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (weeklyGoal.startDate?.time!! <= Date().removeTime().time && weeklyGoal.endDate?.time!! >= Date().removeTime().time)
                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(50) // Circular background
                                )
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                ) // Adjust padding for circular effect
                        ) {
                            Text(
                                text = "Active",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
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
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}