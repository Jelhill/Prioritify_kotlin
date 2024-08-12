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
import com.example.prioritify.api.TaskData
import com.example.prioritify.api.TaskResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LandingActivity : AppCompatActivity() {

    companion object {
        val taskList = mutableListOf<TaskData>()
    }

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        apiService = RetrofitClient.getInstance(SessionManager(this))

        val createTaskButton: FloatingActionButton = findViewById(R.id.buttonCreateTask)
        createTaskButton.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.textViewTodayTask).setOnClickListener {
            filterTasksForToday()
        }

        findViewById<TextView>(R.id.textViewWeeklyTask).setOnClickListener {
            filterTasksForWeek()
        }

        findViewById<TextView>(R.id.textViewMonthlyTask).setOnClickListener {
            filterTasksForMonth()
        }

        fetchUserTasks() // Fetch tasks when the activity is created
    }

    private fun fetchUserTasks() {
        val call = apiService.getAllTasksByUser()
        call.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    taskList.clear()
                    response.body()?.data?.let { tasks ->
                        taskList.addAll(tasks) // tasks should be a list of TaskData
                    }
                    populateTasks() // Populate UI with tasks
                } else {
                    Toast.makeText(this@LandingActivity, "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@LandingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun populateTasks(filterDate: String? = null) {
        val taskContainer: LinearLayout = findViewById(R.id.taskContainer)
        taskContainer.removeAllViews()

        val filteredTaskList = if (filterDate != null) {
            taskList.filter { it.startTime.contains(filterDate) }
        } else {
            taskList
        }

        // Define the desired date format
        val displayDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        for ((index, task) in filteredTaskList.withIndex()) {
            val taskView = layoutInflater.inflate(R.layout.task_item, taskContainer, false)

            val titleTextView: TextView = taskView.findViewById(R.id.textViewTaskTitle)
            val timeTextView: TextView = taskView.findViewById(R.id.textViewTaskTime)
            val priorityTextView: TextView = taskView.findViewById(R.id.textViewTaskPriority)
            val radioButtonComplete: RadioButton = taskView.findViewById(R.id.radioButtonComplete)
            val taskLayout: RelativeLayout = taskView.findViewById(R.id.taskLayout)

            titleTextView.text = task.title

            // Parse the start and end times and reformat them
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

            // Handle task completion logic
            radioButtonComplete.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    drawable.setColor(ContextCompat.getColor(this, R.color.completedTaskColor))
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

    override fun onResume() {
        super.onResume()
        fetchUserTasks() // Fetch tasks when the activity is resumed
    }

    private fun filterTasksForToday() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        populateTasks(today)
    }

    private fun filterTasksForWeek() {
        // Implement your logic to filter tasks for the week
        populateTasks() // Placeholder
    }

    private fun filterTasksForMonth() {
        // Implement your logic to filter tasks for the month
        populateTasks() // Placeholder
    }
}
