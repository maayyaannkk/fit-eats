package com.fiteats.app.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MealPlan(
    val id: String? = null,
    val mainGoalId: String,
    val weeklyGoalId: String,
    val dayMeals: List<DayMeal>
)

data class DayMeal(
    val id: String? = null,
    val date: Date,
    val meals: List<Meal>
)

data class Meal(
    val id: String? = null,
    val name: String,
    val description: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val time: String,
    val ingredients: List<Ingredient>,
    @SerializedName("recipe_steps")
    val recipeSteps: List<String>,
    val isConsumed: Boolean = false
)

data class Ingredient(
    val name: String,
    val quantity: String
)