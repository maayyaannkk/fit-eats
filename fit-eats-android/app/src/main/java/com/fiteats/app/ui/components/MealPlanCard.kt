package com.fiteats.app.ui.components

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.fiteats.app.models.DayMeal
import com.fiteats.app.models.Meal
import com.fiteats.app.models.MealPlan
import com.fiteats.app.ui.screens.meal.NutritionInfo
import com.fiteats.app.utils.removeTime
import com.fiteats.app.utils.toDDMMM
import com.fiteats.app.utils.toEEEEMMMdd
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@Composable
fun MealPlanCard(
    mealPlan: MealPlan,
    onMealClick: (Meal) -> Unit,
    onDayMealEdit: (DayMeal, String) -> Unit,
    onDayMealConsume: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { TODO() }
                )
            }

            Text(
                text = "${mealPlan.dayMeals[0].date.toDDMMM()} - ${mealPlan.dayMeals.last().date.toDDMMM()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { TODO() }
                )
            }
        }
        HorizontalDivider()
        DayMealCard(mealPlan, onMealClick, onDayMealEdit, onDayMealConsume)
    }
}

@Composable
fun DayMealCard(
    mealPlan: MealPlan,
    onMealClick: (Meal) -> Unit,
    onDayMealEdit: (DayMeal, String) -> Unit,
    onDayMealConsume: (String) -> Unit
) {
    val timeDifferenceMillis =
        Date().removeTime().time - mealPlan.dayMeals[0].date.removeTime().time
    val timeDifferenceDays = timeDifferenceMillis.toDouble() / 86400000.0

    val current = if (timeDifferenceMillis > 0)
        ceil(timeDifferenceDays).toInt() // Use ceil to round up to the next integer
    else 0

    var currentDay by rememberSaveable { mutableIntStateOf(if (current in 0..mealPlan.dayMeals.size - 1) current else 0) }

    val currentDayMeal = mealPlan.dayMeals[currentDay]

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(15.dp)
                    .clickable { if (currentDay > 0) currentDay -= 1 }
            )
        }

        Text(
            text = "${currentDayMeal.date.toEEEEMMMdd()}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Column(horizontalAlignment = Alignment.End) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(15.dp)
                    .clickable { if (currentDay < mealPlan.dayMeals.size - 1) currentDay += 1 }
            )
        }
    }
    HorizontalDivider()

    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                if (currentDayMeal.meals.count { it.isConsumed == true } == 0)
                    showDialog.value = true
                else Toast.makeText(
                    context,
                    "You can only customize un-consumed meals",
                    LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(5.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Customize", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Customize", color = Color.White)
        }
    }

    NutritionInfo(
        calories = currentDayMeal.meals.sumOf { it.calories },
        protein = currentDayMeal.meals.sumOf { it.protein },
        carbs = currentDayMeal.meals.sumOf { it.carbs },
        fat = currentDayMeal.meals.sumOf { it.fat }
    )

    LazyColumn {
        items(currentDayMeal.meals.sortedBy {
            LocalTime.parse(it.time.trim().replace(Regex("(?i)am"), "AM").replace(Regex("(?i)pm"), "PM"), DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH))
        }, key = { it.id!! }) {
            MealCard(it, onMealClick, onDayMealConsume)
        }
    }

    if (showDialog.value) {
        EditDayMealDialog(
            dayMeal = currentDayMeal,
            onConfirm = {
                onDayMealEdit(currentDayMeal, it)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false }
        )
    }

}

@Composable
fun MealCard(meal: Meal, onMealClick: (Meal) -> Unit, onDayMealConsume: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = { onMealClick(meal) }),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(0.1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (meal.isConsumed) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(onClick = { if (!meal.isConsumed) onDayMealConsume(meal.id!!) })
                )
            }
            Column(modifier = Modifier.weight(0.5f)) {
                Text(
                    text = meal.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (meal.isConsumed) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.time,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = if (meal.isConsumed) TextDecoration.LineThrough else null
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${meal.calories} kcal",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (meal.isConsumed) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "P: ${meal.protein}g • C: ${meal.carbs}g • F: ${meal.fat}g",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = if (meal.isConsumed) TextDecoration.LineThrough else null
                )
            }
            Column(
                modifier = Modifier.weight(0.1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                    contentDescription = "Details",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EditDayMealDialog(
    dayMeal: DayMeal,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dialogText = remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(5.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Customize Meals for ${dayMeal.date.toEEEEMMMdd()}",
                fontSize = 16.sp
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    placeholder = {
                        Text(
                            "Change my eating window from 2 PM to 10 PM.\n" +
                                    "I am allergic to nuts, dairy, gluten.\n" +
                                    "Use Air-fried, grilled, boiled, etc.\n" +
                                    "Replace chicken with paneer.\n" +
                                    "Use only affordable ingredients.",
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp
                        )
                    },
                    value = dialogText.value,
                    onValueChange = { input ->
                        val filteredText = input.filter { it.isLetter() || it.isWhitespace() }
                        dialogText.value = filteredText
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (dialogText.value.isNotEmpty())
                    onConfirm(dialogText.value)
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun MealPlanCardPreview() {
    MealPlanCard(
        MealPlan(
            dayMeals = listOf(),
            id = "1",
            mainGoalId = "2",
            weeklyGoalId = "3"
        ), {}, { _, _ -> }, {}
    )
}
