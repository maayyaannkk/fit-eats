package com.fiteats.app.network


import android.content.Context
import com.fiteats.app.BuildConfig
import com.fiteats.app.utils.GsonUtil
import com.fiteats.app.utils.UserUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private var api: FitEatsApi? = null

    @Synchronized
    fun createApi(context: Context): FitEatsApi {
        if (api != null) return api!!

        val client = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                if (UserUtils.isLoggedIn(context)) {
                    val token = UserUtils.getAccessToken(context)
                    if (!token.isNullOrBlank()) {
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                    }
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonUtil.gson))
            .client(client)
            .build()
            .create(FitEatsApi::class.java)

        return api!!
    }
}
