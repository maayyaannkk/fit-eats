package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.DashboardModel
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.UserUtils
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    private val _dashboardData = MutableStateFlow<DashboardModel?>(null)
    val dashboardData: MutableStateFlow<DashboardModel?> get() = _dashboardData

    fun getDashboard() {
        viewModelScope.launch {
            try {
                val response = api.getDashboard(
                    UserUtils.getUser(getApplication())!!.id!!
                )
                if (response.isSuccessful) {
                    val responseString = response.body()!!
                    val dashboard = Gson().fromJson(responseString, DashboardModel::class.java)
                    _dashboardData.value = dashboard
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
