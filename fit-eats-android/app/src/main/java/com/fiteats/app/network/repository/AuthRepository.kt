package com.fiteats.app.network.repository

import android.content.Context
import com.fiteats.app.network.RetrofitClient

class AuthRepository(context: Context) {
    private val api = RetrofitClient.createApi(context = context)

    suspend fun login(email: String, password: String) = api.login(email, password)

    suspend fun register(name: String, email: String, password: String) =
        api.register(mapOf("email" to email, "password" to password, "name" to name))
}
