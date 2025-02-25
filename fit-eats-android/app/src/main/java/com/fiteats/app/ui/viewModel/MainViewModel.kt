package com.fiteats.app.ui.viewModel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fiteats.app.models.UserModel
import com.fiteats.app.network.RetrofitClient
import com.fiteats.app.utils.GsonUtil
import com.fiteats.app.utils.UserUtils
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application = application) {
    private val api = RetrofitClient.createApi(context = application)

    private val _user = MutableLiveData<UserModel?>(loadUser())
    val user: MutableLiveData<UserModel?> get() = _user

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "key_user") {
                _user.value = loadUser()
            }
        }

    init {
        UserUtils.getUserPreference(getApplication())
            .registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun loadUser(): UserModel? {
        return UserUtils.getUser(getApplication())
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun update(userModel: UserModel) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.update(userModel)
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success(response.body()?.toString() ?: "")
                    getProfile()
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

    fun getProfile() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.getProfile(emailId = UserUtils.getUser(getApplication())!!.email)
                if (response.isSuccessful) {
                    val loginResponse = response.body()!!
                    val user =
                        GsonUtil.gson.fromJson<UserModel>(
                            loginResponse.get("user"),
                            UserModel::class.java
                        )
                    UserUtils.saveUserProfile(context = getApplication(), user)
                    _authState.value = AuthState.Success(response.body()?.toString() ?: "")
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                        jsonObject.get("error")?.asString ?: "Get failed"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Get failed"
                    }
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        UserUtils.getUserPreference(getApplication())
            .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                api.logout(email = UserUtils.getUser(getApplication())!!.email)
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error("Network error: ${e.message}")
            } finally {
                UserUtils.clearUser(getApplication())
            }
        }
    }
}