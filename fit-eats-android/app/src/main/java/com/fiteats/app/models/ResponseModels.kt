package com.fiteats.app.models

import com.google.gson.annotations.SerializedName

data class IdealWeight(
    @SerializedName("idealWeightRange") val idealWeightRange: IdealWeightRange
)

data class IdealWeightRange(
    @SerializedName("lowerBound") val lowerBound: Bound,
    @SerializedName("upperBound") val upperBound: Bound
)

data class GoalDuration(
    @SerializedName("type") val type: GoalType,
    @SerializedName("pace_options") val paceOptions: PaceOptions
)

data class CalorieExpenditure(
    @SerializedName("bmr") val bmr: Double,
    @SerializedName("tdee") val tdee: Tdee
)

data class CalorieGoal(
    @SerializedName("weekly_weight_loss_kg") val weeklyWeightLossKg: Double,
    @SerializedName("daily_calorie_deficit") val dailyCalorieDeficit: Int,
    @SerializedName("daily_calorie_intake") val dailyCalorieIntake: Int,
    @SerializedName("macronutrient_split") val macronutrientSplit: MacronutrientSplit
)

//region inner classes
data class Bound(
    @SerializedName("weight_in_kg") val weightInKg: Double,
    @SerializedName("fat_percentage") val fatPercentage: Double,
    @SerializedName("description") val description: String
)

data class PaceOptions(
    @SerializedName("slow") val slow: Pace,
    @SerializedName("medium") val medium: Pace,
    @SerializedName("fast") val fast: Pace
)

data class Pace(
    @SerializedName("weekly_weight_change_kg") val weeklyWeightChangeKg: Double,
    @SerializedName("duration_weeks") val durationWeeks: Int,
    @SerializedName("notes") val notes: String
)

data class Tdee(
    @SerializedName("sedentary_tdee") val sedentaryTdee: ActivityLevelTdee,
    @SerializedName("lightly_active_tdee") val lightlyActiveTdee: ActivityLevelTdee,
    @SerializedName("moderately_active_tdee") val moderatelyActiveTdee: ActivityLevelTdee,
    @SerializedName("very_active_tdee") val veryActiveTdee: ActivityLevelTdee,
    @SerializedName("extra_active_tdee") val extraActiveTdee: ActivityLevelTdee
)

data class ActivityLevelTdee(
    @SerializedName("description") val description: String,
    @SerializedName("tdee") val tdee: Double
)

data class MacronutrientSplit(
    @SerializedName("protein") val protein: Macronutrient,
    @SerializedName("fat") val fat: Macronutrient,
    @SerializedName("carbohydrates") val carbohydrates: Macronutrient
)

data class Macronutrient(
    @SerializedName("total_grams") val totalGrams: Int,
    @SerializedName("calories") val calories: Int
)
//endregion