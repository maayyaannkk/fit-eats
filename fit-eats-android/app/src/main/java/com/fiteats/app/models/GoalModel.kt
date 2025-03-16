package com.fiteats.app.models

import java.util.Date

enum class GoalType(val value: String) {
    FAT_LOSS("Fat loss"),
    MUSCLE_GAIN("Muscle gain");

    companion object {
        private val map = GoalType.entries.associateBy(GoalType::value)
        fun fromString(value: String) = map[value]
    }
}

data class MainGoalModel(
    val id: String? = null,
    val userId: String? = null,

    val startWeightInKg: Double? = null,
    val startFatPercentage: Double? = null,

    val targetWeightInKg: Double? = null,
    val targetFatPercentage: Double? = null,

    val goalStartDate: Date? = null,
    val goalEndDate: Date? = null,

    val goalType: GoalType? = null,

    val weeklyGoals: List<WeeklyGoalModel>? = null
)

data class WeeklyGoalModel(
    val id: String? = null,
    val mainGoalId: String? = null,

    val startDate: Date? = null,
    val endDate: Date? = null,

    val currentWeightInKg: Double? = null,
    val currentFatPercentage: Double? = null,

    val dailyMaintenanceCalories: Double? = null,
    val targetDailyCalories: Double? = null,

    val targetDailyMacrosProtein: Double? = null,
    val targetDailyMacrosCarbs: Double? = null,
    val targetDailyMacrosFats: Double? = null,

    val workoutRoutine: String? = null
)
