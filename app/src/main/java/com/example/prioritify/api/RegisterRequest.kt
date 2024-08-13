package com.example.prioritify.api

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password: String
)



data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: Data
)
data class User(
    val full_name: String,
    val email: String,
    val password: String,
    val _id: String,
//    val __v: Int
)
