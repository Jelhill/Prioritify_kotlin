package com.example.prioritify.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class Data(
    val user: User,
    val token: String
)

interface ApiService {
    @POST("users/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}
