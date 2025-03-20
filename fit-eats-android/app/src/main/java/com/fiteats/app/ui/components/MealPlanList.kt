package com.fiteats.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fiteats.app.models.DayMeal
import com.fiteats.app.models.Meal
import com.fiteats.app.models.MealPlan
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MealPlanList(mealPlan: MealPlan) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(items = mealPlan.dayMeals, key = { it.id!! }) {
            DayMealItem(dayMeal = it)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DayMealItem(dayMeal: DayMeal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        //elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val dateFormatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            val formattedDate = dateFormatter.format(dayMeal.date)
            Text(
                text = "Date: $formattedDate",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            dayMeal.meals.forEach { meal -> //Safe call to prevent NPE
                MealItem(meal = meal)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
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