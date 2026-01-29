package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.GoalDuration
import com.fiteats.app.models.IdealWeightRange
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class AddMainGoalViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _idealWeight = MutableLiveData<IdealWeightRange?>()
    val idealWeight: MutableLiveData<IdealWeightRange?> get() = _idealWeight

    private val _goalDuration = MutableLiveData<GoalDuration?>()
    val goalDuration: MutableLiveData<GoalDuration?> get() = _goalDuration

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _apiError = MutableLiveData<String?>()
    val apiError: MutableLiveData<String?> get() = _apiError

    private val _finalSubmit = MutableLiveData<Boolean>()
    val finalSubmit: MutableLiveData<Boolean> get() = _finalSubmit

    fun registerNewGoal(mainGoalModel: MainGoalModel) {
        viewModelScope.launch {
            try {
                val response = api.registerMainGoal(mainGoalModel)
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
    fun getIdealWeight(currentWeightInKg: Double, currentBodyFatPercentage: Double) {
        _isLoading.value = true
        _apiError.value = null // Clear any previous error
        viewModelScope.launch {
            try {
                val userId = UserUtils.getUser(getApplication())!!.id!!
                val response =
                    api.getIdealWeight(userId, currentWeightInKg, currentBodyFatPercentage)
                if (response.isSuccessful) {
                    _idealWeight.value = response.body()?.idealWeightRange
                    _apiError.value = null
                } else {
                    _idealWeight.value = null
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get ideal weight"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get ideal weight"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _idealWeight.value = null
                _apiError.value = "Failed to get ideal weight: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGoalDuration(
        currentWeightInKg: Double,
        currentBodyFatPercentage: Double,
        goalWeightInKg: Double,
        goalBodyFatPercentage: Double
    ) {
        _isLoading.value = true
        _apiError.value = null // Clear any previous error
        viewModelScope.launch {
            try {
                val userId = UserUtils.getUser(getApplication())!!.id!!
                val response = api.getGoalDuration(
                    userId,
                    currentWeightInKg,
                    currentBodyFatPercentage,
                    goalWeightInKg,
                    goalBodyFatPercentage
                )
                if (response.isSuccessful) {
                    _goalDuration.value = response.body()
                    _apiError.value = null
                } else {
                    _goalDuration.value = null
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goal duration"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goal duration"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _goalDuration.value = null
                _apiError.value = "Failed to get goal duration: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    //endregion
}