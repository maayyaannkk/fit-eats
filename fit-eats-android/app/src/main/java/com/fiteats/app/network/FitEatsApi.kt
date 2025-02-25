package com.fiteats.app.network

import com.fiteats.app.models.UserGoalModel
import com.fiteats.app.models.UserModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface FitEatsApi {

    //region login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<JsonObject>

    @POST("register")
    suspend fun register(@Body requestBody: Map<String, String>): Response<JsonObject>

    @FormUrlEncoded
    @POST("requestAccessToken")
    suspend fun requestAccessToken(
        @Field("email") email: String,
        @Field("refreshToken") refreshToken: String
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST("logout")
    suspend fun logout(
        @Field("emailId") email: String
    ): Response<JsonObject>
    //endregion

    //region user
    @PUT("profile")
    suspend fun update(@Body userModel: UserModel): Response<JsonObject>

    @GET("profile")
    suspend fun getProfile(@Query("emailId") emailId: String): Response<JsonObject>
    //endregion

    //region goals
    @POST("goals")
    suspend fun registerGoal(@Body goalModel: UserGoalModel): Response<JsonObject>

    @GET("goals")
    suspend fun getGoals(@Query("userId") userId: String): Response<JsonObject>

    @DELETE("goals")
    suspend fun deleteGoal(@Query("goalId") goalId: String): Response<JsonObject>
    //endregion
}