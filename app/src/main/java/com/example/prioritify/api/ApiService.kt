package com.example.prioritify.api

import retrofit2.Call
import retrofit2.http.*

data class Data(
    val user: User,
    val token: String
)

interface ApiService {
    @POST("users/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("todos")
    fun createTask(@Body task: TaskRequest): Call<TaskResponse>

    @GET("todos/user/all")
    fun getAllTasksByUser(): Call<TaskResponse>

    @GET("todos/user/completed")
    fun getCompletedTasksByUser(): Call<TaskResponse>

    @GET("todos/{id}")
    fun getTaskById(@Path("id") id: String): Call<TaskResponse>

    @PUT("todos/{id}")
    fun updateTask(@Path("id") id: String, @Body task: TaskRequest): Call<TaskResponse>

    @DELETE("todos/{id}")
    fun deleteTask(@Path("id") id: String): Call<TaskResponse>

    @DELETE("todos/delete/all")
    fun deleteAllTasks(): Call<TaskResponse>
}
