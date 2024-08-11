package com.example.prioritify.api

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password: String
)


// Define the data class for the response
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: Data
)
// Define the data class for the user object inside the response
data class User(
    val full_name: String,
    val email: String,
    val password: String,
    val _id: String,
//    val __v: Int
)
