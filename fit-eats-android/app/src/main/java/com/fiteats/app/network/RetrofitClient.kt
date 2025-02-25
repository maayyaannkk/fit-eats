package com.fiteats.app.network


import android.content.Context
import com.fiteats.app.BuildConfig
import com.fiteats.app.utils.UserUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private var api: FitEatsApi? = null
    fun createApi(context: Context): FitEatsApi {
        if (api != null) return api!!
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                if (UserUtils.isLoggedIn(context)) {
                    val token = UserUtils.getAccessToken(context)
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                }
                chain.proceed(request = chain.request())
            }
            .build()

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(FitEatsApi::class.java)
        return api!!
    }
}
