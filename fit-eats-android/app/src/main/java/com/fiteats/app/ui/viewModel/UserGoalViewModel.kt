package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.UserGoalModel
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.GsonUtil
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class UserGoalViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _userGoals = MutableLiveData<ArrayList<UserGoalModel>>()
    val userGoals: MutableLiveData<ArrayList<UserGoalModel>> get() = _userGoals

    fun getGoals() {
        viewModelScope.launch {
            try {
                val response = api.getGoals(UserUtils.getUser(getApplication())!!.id!!)
                if (response.isSuccessful) {
                    val responseString = response.body()!!
                    val goals =
                        GsonUtil.gson.fromJson<ArrayList<UserGoalModel>>(
                            responseString.get("userGoals"),
                            object : TypeToken<ArrayList<UserGoalModel>>() {}.type
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

    fun registerNewGoal(userGoalModel: UserGoalModel) {
        viewModelScope.launch {
            try {
                val response = api.registerGoal(userGoalModel)
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
}