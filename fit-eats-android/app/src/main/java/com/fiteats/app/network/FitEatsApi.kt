package com.fiteats.app.network

import com.fiteats.app.models.UserModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT

interface FitEatsApi {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<JsonObject>

    @POST("register")
    suspend fun register(@Body requestBody: Map<String, String>): Response<JsonObject>

    @PUT("profile")
    suspend fun update(@Body userModel: UserModel): Response<JsonObject>

    @FormUrlEncoded
    @POST("requestAccessToken")
    suspend fun requestAccessToken(
        @Field("email") email: String,
        @Field("refreshToken") refreshToken: String
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST("logout")
    suspend fun logout(
        @Field("email") email: String
    ): Response<JsonObject>

}