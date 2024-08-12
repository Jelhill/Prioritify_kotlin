package com.example.prioritify.api

//data class TaskData(
//    var title: String,
//    var description: String,
//    var startTime: String,
//    var endTime: String,
//    var priority: String
//)

data class TaskData(
    val _id: String,
    val title: String,
    val description: String,
    val priority: String,
    val startTime: String,
    val endTime: String,
    val reminder: String,
    var status: String
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


data class CreateTaskResponse(
    val success: Boolean,
    val message: String,
    val data: TaskData
)

data class EditTaskResponse(
    val success: Boolean,
    val message: String,
    val data: TaskData // Assuming it returns a single TaskData object
)

data class TaskStatusRequest (
    val status: String
)