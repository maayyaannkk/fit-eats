package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.UserModel
import com.fiteats.app.network.RetrofitClient
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun update(userModel: UserModel) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.update(userModel)
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success(response.body()?.toString() ?: "")
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Update failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Update failed"
                    }
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    //TODO get profile
}