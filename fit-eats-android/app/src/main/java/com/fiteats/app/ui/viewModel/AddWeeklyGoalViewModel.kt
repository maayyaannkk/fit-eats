package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.CalorieExpenditure
import com.fiteats.app.models.MacroGoal
import com.fiteats.app.models.WeeklyGoalModel
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class AddWeeklyGoalViewModel(application: Application) :
    AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _calorieExpenditure = MutableLiveData<CalorieExpenditure?>()
    val calorieExpenditure: MutableLiveData<CalorieExpenditure?> get() = _calorieExpenditure

    private val _macroGoal = MutableLiveData<MacroGoal?>()
    val macroGoal: MutableLiveData<MacroGoal?> get() = _macroGoal

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _apiError = MutableLiveData<String?>()
    val apiError: MutableLiveData<String?> get() = _apiError

    private val _finalSubmit = MutableLiveData<Boolean>()
    val finalSubmit: MutableLiveData<Boolean> get() = _finalSubmit

    fun registerWeeklyGoal(weeklyGoalModel: WeeklyGoalModel) {
        viewModelScope.launch {
            try {
                val response = api.registerWeeklyGoal(weeklyGoalModel, weeklyGoalModel.mainGoalId!!)
                if (response.isSuccessful) {
                    val responseString = response.body()!!
                    _apiError.value = null // Clear any previous error
                    _finalSubmit.value = true
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Register failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Register failed"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Register failed: ${e.message}"
            }
        }
    }

    //region AI API methods
    fun getTdee(
        currentWeightInKg: Double,
        currentBodyFatPercentage: Double,
        goalWeightInKg: Double,
        goalBodyFatPercentage: Double,
        goalType: String
    ) {
        _isLoading.value = true
        _apiError.value = null // Clear any previous error
        viewModelScope.launch {
            try {
                val userId = UserUtils.getUser(getApplication())!!.id!!
                val response =
                    api.getTdee(
                        userId,
                        currentWeightInKg,
                        currentBodyFatPercentage,
                        goalWeightInKg,
                        goalBodyFatPercentage,
                        goalType
                    )
                if (response.isSuccessful) {
                    _calorieExpenditure.value = response.body()
                    _apiError.value = null
                } else {
                    _calorieExpenditure.value = null
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get tdee"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get tdee"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _calorieExpenditure.value = null
                _apiError.value = "Failed to get tdee: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGoalMacros(
        currentWeightInKg: Double,
        currentBodyFatPercentage: Double,
        goalWeightInKg: Double,
        goalBodyFatPercentage: Double,
        goalType: String,
        bmr: Int,
        tdee: Int,
        weightChange: Float
    ) {
        _isLoading.value = true
        _apiError.value = null // Clear any previous error
        viewModelScope.launch {
            try {
                val userId = UserUtils.getUser(getApplication())!!.id!!
                val response = api.getMacros(
                    userId,
                    currentWeightInKg,
                    currentBodyFatPercentage,
                    goalWeightInKg,
                    goalBodyFatPercentage,
                    goalType, bmr, tdee, weightChange
                )
                if (response.isSuccessful) {
                    _macroGoal.value = response.body()
                    _apiError.value = null
                } else {
                    _macroGoal.value = null
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goal calories"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goal calories"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _macroGoal.value = null
                _apiError.value = "Failed to get goal calories: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    //endregion
}