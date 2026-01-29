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
    @SerializedName("bmr") val bmr: Int,
    @SerializedName("tdee") val tdee: ArrayList<ActivityLevelTdee>
)

data class MacroGoal(
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
    @SerializedName("weekly_weight_change_kg") val weeklyWeightChangeKg: Float,
    @SerializedName("duration_weeks") val durationWeeks: Int,
    @SerializedName("notes") val notes: String,
    val type: String? = null,
    val goalType: GoalType? = null
)

data class ActivityLevelTdee(
    @SerializedName("description") val description: String,
    @SerializedName("lifestyle") val lifestyle: String,
    @SerializedName("tdee") val tdee: Int
)

enum class LifestyleType(val value: String) {
    SEDENTARY("Sedentary"),
    LIGHT("Light"),
    MODERATE("Moderate"),
    VERY_ACTIVE("Very Active"),
    EXTRA_ACTIVE("Extra Active");

    companion object {
        private val map = LifestyleType.entries.associateBy(LifestyleType::value)
        fun fromString(value: String) = map[value]
    }
}

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