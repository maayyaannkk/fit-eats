package com.fiteats.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fiteats.app.models.DayMeal
import com.fiteats.app.models.Meal
import com.fiteats.app.models.MealPlan
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MealPlanList(mealPlan: MealPlan, onDayMealEdit: (DayMeal, String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        items(items = mealPlan.dayMeals, key = { it.id!! }) {
            DayMealItem(dayMeal = it, onDayMealEdit)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayMealItem(dayMeal: DayMeal, onDayMealEdit: (DayMeal, String) -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    val dialogText = remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        //elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateFormatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
                val formattedDate = dateFormatter.format(dayMeal.date)
                Text(
                    text = "Date: $formattedDate",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f) // Expand to fill available space
                )

                IconButton(onClick = {
                    showDialog.value = true
                    dialogText.value = "" // Reset text when opening dialog
                }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Date")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            dayMeal.meals.forEach { meal -> //Safe call to prevent NPE
                MealItem(meal = meal)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }

    if (showDialog.value) {
        EditDayMealDialog(
            showDialog = showDialog,
            dialogText = dialogText,
            onConfirm = {
                onDayMealEdit(dayMeal, dialogText.value)
                showDialog.value = false
            },
            onDismiss = { showDialog.value = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDayMealDialog(
    showDialog: MutableState<Boolean>,
    dialogText: MutableState<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Day Meal") },
        text = {
            Column {
                Text("Enter new value:")
                TextField(
                    value = dialogText.value,
                    onValueChange = { dialogText.value = it },
                    label = { Text("New Value") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
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


@Composable
fun MealItem(meal: Meal) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = meal.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(text = "Time: ${meal.time}")
        Text(text = "Calories: ${meal.calories}")
        Text(text = "Carbs: ${meal.carbs}g")
        Text(text = "Protein: ${meal.protein}g")
        Text(text = "Fat: ${meal.fat}g")

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Ingredients:", fontWeight = FontWeight.Bold)

        meal.ingredients.forEach { ingredient ->
            Text(text = "- ${ingredient.name} (${ingredient.quantity})")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Recipe Steps:", fontWeight = FontWeight.Bold)
        meal.recipeSteps.forEachIndexed { index, step ->
            Text(text = "${index + 1}. $step")
        }
    }
}