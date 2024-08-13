package com.example.prioritify

import SessionManager
import android.util.Log
import com.example.prioritify.api.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(private val sessionManager: SessionManager) {

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = sessionManager.fetchAuthToken()
        Log.d("INTERCEPTING TOKEN", token.toString())
        val requestBuilder: Request.Builder = original.newBuilder()
            .header("Authorization", "Bearer $token")

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    companion object {
        fun getInstance(sessionManager: SessionManager): ApiService {
            val retrofitClient = RetrofitClient(sessionManager)
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/api/")
                .client(retrofitClient.okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
