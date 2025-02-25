package com.fiteats.app.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.UserModel
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.GsonUtil
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.login(email, password)
                if (response.isSuccessful) {
                    val loginResponse = response.body()!!
                    val accessToken = loginResponse.get("accessToken").asString
                    val refreshToken = loginResponse.get("refreshToken").asString
                    val user =
                        GsonUtil.gson.fromJson<UserModel>(
                            loginResponse.get("user"),
                            UserModel::class.java
                        )
                    UserUtils.saveUserProfile(context = getApplication(), user)
                    UserUtils.saveUserToken(context = getApplication(), accessToken, refreshToken)
                    _authState.value = AuthState.Success(response.body()?.toString() ?: "")
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Login failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Login failed"
                    }
                    _authState.value =
                        AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response =
                    api.register(mapOf("name" to name, "email" to email, "password" to password))

                if (response.isSuccessful) {
                    _authState.value = AuthState.Success(response.body()?.toString() ?: "")
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Registration failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Registration failed"
                    }
                    _authState.value =
                        AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}