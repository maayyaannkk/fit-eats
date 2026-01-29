package com.fiteats.app.network

import com.fiteats.app.models.CalorieExpenditure
import com.fiteats.app.models.GoalDuration
import com.fiteats.app.models.IdealWeight
import com.fiteats.app.models.MacroGoal
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.models.MealPlan
import com.fiteats.app.models.UserModel
import com.fiteats.app.models.WeeklyGoalModel
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

    //region dashboard
    @GET("getDashboard")
    suspend fun getDashboard(@Query("userId") userId: String): Response<JsonObject>
    //endregion

    //region goals
    @POST("registerMainGoal")
    suspend fun registerMainGoal(@Body goalModel: MainGoalModel): Response<JsonObject>

    @POST("registerWeeklyGoal")
    suspend fun registerWeeklyGoal(
        @Body goalModel: WeeklyGoalModel,
        @Query("mainGoalId") mainGoalId: String
    ): Response<JsonObject>

    @GET("getGoals")
    suspend fun getGoals(@Query("userId") userId: String): Response<JsonObject>

    @GET("getActiveGoal")
    suspend fun getActiveGoal(@Query("userId") userId: String): Response<MainGoalModel>

    @DELETE("goals")
    suspend fun deleteGoal(@Query("goalId") goalId: String): Response<JsonObject>
    //endregion

    //region meal Plan
    @GET("getMealPlan")
    suspend fun getMealPlan(
        @Query("userId") userId: String,
        @Query("mainGoalId") mainGoalId: String,
        @Query("weeklyGoalId") weeklyGoalId: String
    ): Response<MealPlan>

    @PUT("consumeMeal")
    suspend fun consumeMeal(
        @Query("mealId") mealId: String
    ): Response<JsonObject>
    //endregion

    //region AI routes
    @GET("getIdealWeight")
    suspend fun getIdealWeight(
        @Query("userId") userId: String,
        @Query("currentWeightInKg") currentWeightInKg: Double,
        @Query("currentBodyFatPercentage") currentBodyFatPercentage: Double
    ): Response<IdealWeight>

    @GET("getGoalDuration")
    suspend fun getGoalDuration(
        @Query("userId") userId: String,
        @Query("currentWeightInKg") currentWeightInKg: Double,
        @Query("currentBodyFatPercentage") currentBodyFatPercentage: Double,
        @Query("goalWeightInKg") goalWeightInKg: Double,
        @Query("goalBodyFatPercentage") goalBodyFatPercentage: Double
    ): Response<GoalDuration>

    @GET("getTdee")
    suspend fun getTdee(
        @Query("userId") userId: String,
        @Query("currentWeightInKg") currentWeightInKg: Double,
        @Query("currentBodyFatPercentage") currentBodyFatPercentage: Double,
        @Query("goalWeightInKg") goalWeightInKg: Double,
        @Query("goalBodyFatPercentage") goalBodyFatPercentage: Double,
        @Query("goalType") goalType: String
    ): Response<CalorieExpenditure>

    @GET("getMacros")
    suspend fun getMacros(
        @Query("userId") userId: String,
        @Query("currentWeightInKg") currentWeightInKg: Double,
        @Query("currentBodyFatPercentage") currentBodyFatPercentage: Double,
        @Query("goalWeightInKg") goalWeightInKg: Double,
        @Query("goalBodyFatPercentage") goalBodyFatPercentage: Double,
        @Query("goalType") goalType: String,
        @Query("currentBmr") currentBmr: Int,
        @Query("currentTdee") currentTdee: Int,
        @Query("weightChange") weightChange: Float,
    ): Response<MacroGoal>

    @POST("createMealPlan")
    suspend fun createMealPlan(
        @Query("userId") userId: String,
        @Query("mainGoalId") mainGoalId: String,
        @Query("weeklyGoalId") weeklyGoalId: String,
        @Query("prompt") prompt: String
    ): Response<MealPlan>

    @PUT("customizeMealPlan")
    suspend fun customizeMealPlan(
        @Query("mealPlanId") mealPlanId: String,
        @Query("dayMealId") dayMealId: String,
        @Query("userPrompt") userPrompt: String
    ): Response<JsonObject>
    //endregion
}