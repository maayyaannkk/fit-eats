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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _apiError = MutableLiveData<String?>()
    val apiError: MutableLiveData<String?> get() = _apiError

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

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteGoal(goalId)
                if (response.isSuccessful) {
                    val responseString = response.body()!!
                    _apiError.value = null // Clear any previous error

                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Delete failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Delete failed"
                    }
                    _apiError.value = errorMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Delete failed: ${e.message}"
            }
        }
    }
}