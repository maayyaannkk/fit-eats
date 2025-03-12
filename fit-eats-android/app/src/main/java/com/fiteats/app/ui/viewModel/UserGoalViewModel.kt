package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.GsonUtil
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class UserGoalViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _userGoals = MutableLiveData<ArrayList<MainGoalModel>>()
    val userGoals: MutableLiveData<ArrayList<MainGoalModel>> get() = _userGoals

    fun getGoals() {
        viewModelScope.launch {
            try {
                val response = api.getGoals(UserUtils.getUser(getApplication())!!.id!!)
                if (response.isSuccessful) {
                    val responseString = response.body()!!
                    val goals =
                        GsonUtil.gson.fromJson<ArrayList<MainGoalModel>>(
                            responseString.get("userGoals"),
                            object : TypeToken<ArrayList<MainGoalModel>>() {}.type
                        )
                    goals?.let { _userGoals.value = it }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Update failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Update failed"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun registerNewGoal(mainGoalModel: MainGoalModel) {
        viewModelScope.launch {
            try {
                val response = api.registerMainGoal(mainGoalModel)
                if (response.isSuccessful) {
                    val responseString = response.body()!!

                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Register failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Register failed"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteGoal(goalId)
                if (response.isSuccessful) {
                    val responseString = response.body()!!

                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Delete failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Delete failed"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // AI API methods
    fun getIdealWeight(currentWeightInKg: Double, currentBodyFatPercentage: Double) {
        viewModelScope.launch {
            try {
                val userId = UserUtils.getUser(getApplication())!!.id!!
                val response = api.getIdealWeight(userId, currentWeightInKg, currentBodyFatPercentage)
                if (response.isSuccessful) {
                    val responseString = response.body()!!
                    //_idealWeight.value = responseString.get("idealWeight").asDouble
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get ideal weight"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get ideal weight"
                    }
                    // Handle error (e.g., show a Toast)
                    //_idealWeight.value = null // Indicate failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //_idealWeight.value = null // Indicate failure
            }
        }
    }

    fun getGoalDuration(
        currentWeightInKg: Double,
        currentBodyFatPercentage: Double,
        goalWeightInKg: Double,
        goalBodyFatPercentage: Double
    ) {
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
                    val responseString = response.body()!!
                    //_goalDuration.value = responseString.get("goalDuration").asInt // Assuming the API returns duration in days
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Failed to get goal duration"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Failed to get goal duration"
                    }
                    // Handle error (e.g., show a Toast)
                    //_goalDuration.value = null // Indicate failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //_goalDuration.value = null // Indicate failure
            }
        }
    }
}