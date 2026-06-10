package com.moviles.eventsync.data.network

import com.moviles.eventsync.core.AppConstants
import com.moviles.eventsync.data.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var apiServiceInstance: EventSyncApi? = null

    fun getApiService(tokenManager: TokenManager): EventSyncApi {
        return apiServiceInstance ?: synchronized(this) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthInterceptor(tokenManager))
                .build()

            Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(EventSyncApi::class.java)
                .also { apiServiceInstance = it }
        }
    }
}
