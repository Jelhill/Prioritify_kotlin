package com.example.prioritify

import SessionManager
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.prioritify.api.ApiService
import com.example.prioritify.api.EditTaskResponse
import com.example.prioritify.api.TaskData
import com.example.prioritify.api.TaskResponse
import com.example.prioritify.api.TaskStatusRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class LandingActivity : AppCompatActivity() {

    companion object {
        val taskList = mutableListOf<TaskData>()
    }

    private lateinit var apiService: ApiService
    private var currentStatus: String = "PENDING" // Track the currently selected tab status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        apiService = RetrofitClient.getInstance(SessionManager(this))

        val createTaskButton: FloatingActionButton = findViewById(R.id.buttonCreateTask)
        createTaskButton.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textViewPendingTasks).setOnClickListener {
            currentStatus = "PENDING"
            filterTasksByStatus(currentStatus)
        }

        findViewById<TextView>(R.id.textViewCompletedTasks).setOnClickListener {
            currentStatus = "COMPLETED"
            filterTasksByStatus(currentStatus)
        }

        fetchUserTasks() // Fetch tasks when the activity is created
    }

    private fun fetchUserTasks() {
        val call = if (currentStatus == "PENDING") {
            apiService.getAllTasksByUser() // Fetch all tasks
        } else {
            apiService.getTodosByStatus(currentStatus) // Fetch tasks by status
        }

        call.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    taskList.clear()
                    response.body()?.data?.let { tasks ->
                        taskList.addAll(tasks) // tasks should be a list of TaskData
                    }
                    populateTasks(currentStatus)
                } else {
                    Toast.makeText(this@LandingActivity, "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@LandingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun populateTasks(status: String? = null) {
        val taskContainer: LinearLayout = findViewById(R.id.taskContainer)
        taskContainer.removeAllViews()

        val filteredTaskList = if (status != null) {
            taskList.filter { it.status == status }
        } else {
            taskList
        }

        if (filteredTaskList.isEmpty()) {
            // Show a message or empty state if no tasks match the filter
            val emptyView = layoutInflater.inflate(R.layout.empty_task_item, taskContainer, false)
            taskContainer.addView(emptyView)
            return
        }

        val displayDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        for ((index, task) in filteredTaskList.withIndex()) {
            val taskView = layoutInflater.inflate(R.layout.task_item, taskContainer, false)

            val titleTextView: TextView = taskView.findViewById(R.id.textViewTaskTitle)
            val timeTextView: TextView = taskView.findViewById(R.id.textViewTaskTime)
            val priorityTextView: TextView = taskView.findViewById(R.id.textViewTaskPriority)
            val radioButtonComplete: RadioButton = taskView.findViewById(R.id.radioButtonComplete)
            val taskLayout: RelativeLayout = taskView.findViewById(R.id.taskLayout)

            titleTextView.text = task.title

            val startTimeFormatted = displayDateFormat.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(task.startTime))
            val endTimeFormatted = displayDateFormat.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(task.endTime))

            timeTextView.text = "$startTimeFormatted - $endTimeFormatted"
            priorityTextView.text = task.priority.capitalize()

            val backgroundColor = when (task.priority.uppercase()) {
                "HIGH" -> ContextCompat.getColor(this, R.color.highPriorityColor)
                "MEDIUM" -> ContextCompat.getColor(this, R.color.mediumPriorityColor)
                "LOW" -> ContextCompat.getColor(this, R.color.lowPriorityColor)
                else -> ContextCompat.getColor(this, R.color.lowPriorityColor)
            }

            val drawable = taskLayout.background as GradientDrawable
            drawable.setColor(backgroundColor)

            taskContainer.addView(taskView)

            radioButtonComplete.isChecked = task.status == "COMPLETED"
            radioButtonComplete.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    updateTaskStatus(task._id, "COMPLETED", index)
                } else {
                    drawable.setColor(backgroundColor)
                }
            }

            taskView.setOnClickListener {
                val dialog = EditTaskDialogFragment.newInstance(index)
                dialog.show(supportFragmentManager, "EditTaskDialogFragment")
            }
        }
    }

    private fun updateTaskStatus(taskId: String, status: String, index: Int) {
        val statusUpdate = TaskStatusRequest(status) // Create the request object
        val call = apiService.updateTaskStatus(taskId, statusUpdate)

        call.enqueue(object : Callback<EditTaskResponse> {
            override fun onResponse(call: Call<EditTaskResponse>, response: Response<EditTaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update the task status in the local list
                    LandingActivity.taskList[index].status = status
                    // Immediately remove the task from the UI
                    taskList.removeAt(index)
                    populateTasks("PENDING")
                } else {
                    Toast.makeText(this@LandingActivity, "Failed to update task status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EditTaskResponse>, t: Throwable) {
                Toast.makeText(this@LandingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterTasksByStatus(status: String) {
        currentStatus = status // Update the current status

        val call = apiService.getAllTasksByUser() // or fetch based on status

        call.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    taskList.clear()
                    response.body()?.data?.let { tasks ->
                        taskList.addAll(tasks.filter { it.status == status })
                    }
                    populateTasks(status)
                } else {
                    Toast.makeText(this@LandingActivity, "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@LandingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Update tab UI to show which tab is selected
        val pendingTab: TextView = findViewById(R.id.textViewPendingTasks)
        val completedTab: TextView = findViewById(R.id.textViewCompletedTasks)

        if (status == "PENDING") {
            pendingTab.isSelected = true
            completedTab.isSelected = false
        } else if (status == "COMPLETED") {
            completedTab.isSelected = true
            pendingTab.isSelected = false
        }
    }


    override fun onResume() {
        super.onResume()
        fetchUserTasks()
    }
}
