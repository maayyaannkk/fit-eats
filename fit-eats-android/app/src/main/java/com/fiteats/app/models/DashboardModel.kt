package com.fiteats.app.models

data class DashboardModel(
    val user: User,
    val progressSummary: ProgressSummary,
    val calorieOverview: CalorieOverview,
    val todayMeals: List<Meal>
)

data class User(
    val name: String,
    val greeting: String
)

data class ProgressSummary(
    val weightInKg: ProgressItem,
    val bodyFatPercentage: ProgressItem
)

data class ProgressItem(
    val current: Float,
    val last: Float,
    val goal: Float,
    val start: Float
)

data class CalorieOverview(
    val total: CalorieTotal,
    val macros: Macros
)

data class CalorieTotal(
    val consumed: Int,
    val goal: Int,
)

data class Macros(
    val protein: MacroItem,
    val carbs: MacroItem,
    val fats: MacroItem
)

data class MacroItem(
    val consumed: Int,
    val goal: Int,
    val unit: String
)

fun ProgressItem.progressPercentage(): Float {
    val totalDelta = goal - start
    if (totalDelta == 0f) return 100f

    val currentDelta = current - start
    val progress = currentDelta / totalDelta

    return (progress * 100f).coerceIn(0f, 100f)
}
