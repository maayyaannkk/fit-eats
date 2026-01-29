package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiteats.app.network.RetrofitClient
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

class MealDetailViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _apiError = MutableLiveData<String?>()
    val apiError: MutableLiveData<String?> get() = _apiError

    fun consumeMeal(mealId: String) {
        viewModelScope.launch {
            try {
                val response = api.consumeMeal(mealId)
                if (response.isSuccessful) {
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
}