package com.example.prioritify.api

// LoginRequest.kt
data class LoginRequest(
    val email: String,
    val password: String
)

// LoginResponse.kt
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)

data class UserData(
    val user: User,
    val token: String
)

