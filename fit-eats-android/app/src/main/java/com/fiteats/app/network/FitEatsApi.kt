package com.fiteats.app.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FitEatsApi {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<JsonObject>

    @POST("register")
    suspend fun register(@Body requestBody: Map<String, String>): Response<JsonObject>

}