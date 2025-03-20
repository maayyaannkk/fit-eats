package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.models.MealPlan
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class MealPlanViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _userGoal = MutableLiveData<MainGoalModel>()
    val userGoal: MutableLiveData<MainGoalModel> get() = _userGoal

    private val _mealPlan = MutableLiveData<MealPlan>()
    val mealPlan: MutableLiveData<MealPlan> get() = _mealPlan

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _apiError = MutableLiveData<String?>()
    val apiError: MutableLiveData<String?> get() = _apiError

    fun createMealPlan(mainGoalId: String, weeklyGoalId: String) {
        viewModelScope.launch {
            try {
                val response = api.createMealPlan(
                    UserUtils.getUser(getApplication())!!.id!!,
                    mainGoalId,
                    weeklyGoalId
                )
                if (response.isSuccessful) {
                    val mealPlanResponse = response.body()
                    mealPlanResponse.let { _mealPlan.value = it }
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
            }
        }
    }

    fun getMealPlan(mainGoalId: String, weeklyGoalId: String) {
        viewModelScope.launch {
            try {
                val response = api.getMealPlan(
                    UserUtils.getUser(getApplication())!!.id!!,
                    mainGoalId,
                    weeklyGoalId
                )
                if (response.isSuccessful) {
                    val mealPlanResponse = response.body()
                    mealPlanResponse.let { _mealPlan.value = it }
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
            }
        }
    }

    fun getActiveGoal() {
        viewModelScope.launch {
            try {
                val response = api.getActiveGoal(UserUtils.getUser(getApplication())!!.id!!)
                if (response.isSuccessful) {
                    val activeGoal = response.body()
                    activeGoal.let { _userGoal.value = it }

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
            }
        }
    }
}