package com.fiteats.app.models

import java.util.Date

data class UserGoalModel(
    val id: String? = null,

    val goalDuration: String? = null,
    val currentWeightInKg: Double? = null,
    val currentFatPercentage: Double? = null,

    val goalStartDate: Date? = null,
    val goalEndDate: Date? = null,

    val dailyMaintenanceCalories: Double? = null,

    val targetDailyCalories: Double? = null,
    val targetDailyMacrosProtein: Double? = null,
    val targetDailyMacrosCarbs: Double? = null,
    val targetDailyMacrosFats: Double? = null,

    val workoutRoutine: String? = null
)