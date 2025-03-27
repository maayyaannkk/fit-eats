package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.models.MealPlan
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealPlanViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _userGoal = MutableStateFlow<MainGoalModel?>(null)
    val userGoal: StateFlow<MainGoalModel?> = _userGoal.asStateFlow()

    private val _mealPlan = MutableStateFlow<MealPlan?>(null)
    val mealPlan: StateFlow<MealPlan?> = _mealPlan.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    private var firstLoad = false

    fun createMealPlan(mainGoalId: String, weeklyGoalId: String, prompt: String) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true at the beginning
            try {
                val response = api.createMealPlan(
                    UserUtils.getUser(getApplication())!!.id!!,
                    mainGoalId,
                    weeklyGoalId,
                    prompt
                )
                if (response.isSuccessful) {
                    val mealPlanResponse = response.body()
                    _mealPlan.value = mealPlanResponse
                    _apiError.value = null
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goals"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goals"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Failed to get goals: ${e.message}"
            } finally {
                _isLoading.value = false // Set loading to false in finally block
            }
        }
    }

    fun getMealPlan(mainGoalId: String, weeklyGoalId: String) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true at the beginning
            try {
                val response = api.getMealPlan(
                    UserUtils.getUser(getApplication())!!.id!!,
                    mainGoalId,
                    weeklyGoalId
                )
                if (response.isSuccessful) {
                    val mealPlanResponse = response.body()
                    _mealPlan.value = mealPlanResponse
                    _apiError.value = null
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goals"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goals"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Failed to get goals: ${e.message}"
            } finally {
                _isLoading.value = false // Set loading to false in finally block
            }
        }
    }

    fun getActiveGoal() {
        if (firstLoad) return
        firstLoad = true

        viewModelScope.launch {
            _isLoading.value = true // Set loading to true at the beginning
            try {
                val response = api.getActiveGoal(UserUtils.getUser(getApplication())!!.id!!)
                if (response.isSuccessful) {
                    val activeGoal = response.body()
                    _userGoal.value = activeGoal

                    getMealPlan(activeGoal!!.id!!, activeGoal.weeklyGoals?.get(0)!!.id!!)

                    _apiError.value = null // Clear any previous error
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goals"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goals"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Failed to get goals: ${e.message}"
            } finally {
                _isLoading.value = false // Set loading to false in finally block
            }
        }
    }

    fun customizeMealPlan(mealPlanId: String, dayMealId: String, userPrompt: String) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true at the beginning
            try {
                val response = api.customizeMealPlan(
                    mealPlanId,
                    dayMealId,
                    userPrompt
                )
                if (response.isSuccessful) {
                    val mealPlanResponse = response.body()!!
                    if (mealPlanResponse.has("mainGoalId") && mealPlanResponse.has("weeklyGoalId"))
                        getMealPlan(
                            mealPlanResponse.get("mainGoalId").asString,
                            mealPlanResponse.get("weeklyGoalId").asString
                        )
                    _apiError.value = null
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goals"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goals"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Failed to get goals: ${e.message}"
            } finally {
                _isLoading.value = false // Set loading to false in finally block
            }
        }
    }

    fun consumeMeal(mealId: String) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true at the beginning
            try {
                val response = api.consumeMeal(mealId)
                if (response.isSuccessful) {
                    _apiError.value = null

                    _mealPlan.value?.let { currentPlan ->
                        toggleMealConsumed(mealId)
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goals"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goals"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Failed to get goals: ${e.message}"
            } finally {
                _isLoading.value = false // Set loading to false in finally block
            }
        }
    }

    fun toggleMealConsumed(mealId: String) {
        _mealPlan.value?.let { currentPlan ->
            val dayIndex =
                currentPlan.dayMeals.indexOfFirst { it.meals.any { meal -> meal.id == mealId } }
            if (dayIndex == -1) return // Meal not found, do nothing

            val mealIndex = currentPlan.dayMeals[dayIndex].meals.indexOfFirst { it.id == mealId }
            if (mealIndex == -1) return // Meal not found, do nothing

            // Create a new meal with updated isConsumed
            val updatedMeal = currentPlan.dayMeals[dayIndex].meals[mealIndex].copy(
                isConsumed = !currentPlan.dayMeals[dayIndex].meals[mealIndex].isConsumed,
                carbs = currentPlan.dayMeals[dayIndex].meals[mealIndex].carbs + 10
            )

            // Create a new meal list with the updated meal
            val updatedMeals = currentPlan.dayMeals[dayIndex].meals.toMutableList().apply {
                this[mealIndex] = updatedMeal
            }

            // Create a new DayMeal list with the updated meals
            val updatedDayMeals = currentPlan.dayMeals.toMutableList().apply {
                this[dayIndex] = this[dayIndex].copy(meals = updatedMeals)
            }

            // Create a new MealPlan with the updated DayMeal list
            val updatedPlan = currentPlan.copy(dayMeals = updatedDayMeals)

            _mealPlan.value = updatedPlan // Trigger LiveData update
        }
    }

}