package com.example.prioritify.api

//data class TaskData(
//    var title: String,
//    var description: String,
//    var startTime: String,
//    var endTime: String,
//    var priority: String
//)

data class TaskData(
    val id: String,
    var title: String,
    var description: String,
    var priority: String,
    var startTime: String,
    var endTime: String,
    val reminder: String
)
data class TaskRequest(
    val title: String,
    val description: String,
    val priority: String,
    val startTime: String,
    val endTime: String,
    val reminder: String
)
data class TaskResponse(
    val success: Boolean,
    val message: String,
    val data: List<TaskData>
)
