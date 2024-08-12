package com.example.prioritify

import SessionManager
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prioritify.api.ApiService
import com.example.prioritify.api.EditTaskResponse
import com.example.prioritify.api.TaskRequest
import com.example.prioritify.api.TaskResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var fromTimeEditText: EditText
    private lateinit var toTimeEditText: EditText
    private lateinit var priorityRadioGroup: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private var taskIndex: Int = -1
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)
        fromTimeEditText = findViewById(R.id.editTextFromTime)
        toTimeEditText = findViewById(R.id.editTextToTime)
        priorityRadioGroup = findViewById(R.id.radioGroupPriority)
        saveButton = findViewById(R.id.buttonSave)
        deleteButton = findViewById(R.id.buttonDelete)

        apiService = RetrofitClient.getInstance(SessionManager(this))

        taskIndex = intent.getIntExtra("taskIndex", -1)
        val task = LandingActivity.taskList[taskIndex]

        titleEditText.setText(task.title)
        descriptionEditText.setText(task.description)
        fromTimeEditText.setText(task.startTime)
        toTimeEditText.setText(task.endTime)

        when (task.priority.uppercase()) {
            "HIGH" -> priorityRadioGroup.check(R.id.radioButtonHigh)
            "MEDIUM" -> priorityRadioGroup.check(R.id.radioButtonMedium)
            "LOW" -> priorityRadioGroup.check(R.id.radioButtonLow)
        }

        fromTimeEditText.setOnClickListener {
            showTimePickerDialog(fromTimeEditText)
        }

        toTimeEditText.setOnClickListener {
            showTimePickerDialog(toTimeEditText)
        }

        saveButton.setOnClickListener {
            saveTask()
        }

        deleteButton.setOnClickListener {
            deleteTask()
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val currentTime = editText.text.toString().split(":")
        val hour = if (currentTime.isNotEmpty()) currentTime[0].toInt() else 0
        val minute = if (currentTime.isNotEmpty()) currentTime[1].split(" ")[0].toInt() else 0

        val timePickerDialog = TimePickerDialog(this, { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            val amPm = if (selectedHour >= 12) "PM" else "AM"
            val hourIn12Format = if (selectedHour > 12) selectedHour - 12 else selectedHour
            editText.setText(String.format("%02d:%02d %s", hourIn12Format, selectedMinute, amPm))
        }, hour, minute, false)

        timePickerDialog.show()
    }

    private fun saveTask() {
        Log.d("STARTED SAVING TASK", "VALIDATION OKAY")

        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val fromTime = fromTimeEditText.text.toString()
        val toTime = toTimeEditText.text.toString()
        val priority = when (priorityRadioGroup.checkedRadioButtonId) {
            R.id.radioButtonHigh -> "HIGH"
            R.id.radioButtonMedium -> "MEDIUM"
            R.id.radioButtonLow -> "LOW"
            else -> ""
        }

        if (title.isEmpty() || description.isEmpty() || fromTime.isEmpty() || toTime.isEmpty() || priority.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("VALIDATION OKAY", "VALIDATION OKAY")

        // Assuming the reminder time is the same as the start time
        val reminderTime = fromTime
        val task = LandingActivity.taskList[taskIndex]

        Log.d("TASK ID CHECK", "Task ID: ${task._id}")  // Debugging: check if task ID is null
        Log.d("TASK ===> CHECK", task.toString())  // Debugging: check if task ID is null

        if (task._id == null) {
            Toast.makeText(this, "Task ID is missing. Cannot update task.", Toast.LENGTH_SHORT).show()
            return
        }
        val taskRequest = TaskRequest(
            title = title,
            description = description,
            priority = priority,
            startTime = fromTime,
            endTime = toTime,
            reminder = reminderTime
        )
        Log.d("TASK REQUEST", taskRequest.toString())

        val taskId = LandingActivity.taskList[taskIndex]._id
        Log.d("ABOUT TO CALL API SERVICE", "Hello")
        val call = apiService.updateTask(taskId, taskRequest)

        call.enqueue(object : Callback<EditTaskResponse> {
            override fun onResponse(call: Call<EditTaskResponse>, response: Response<EditTaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedTask = response.body()?.data
                    if (updatedTask != null) {
                        LandingActivity.taskList[taskIndex] = updatedTask
                        Toast.makeText(this@EditTaskActivity, "Task Updated Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditTaskActivity, "No task data returned", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditTaskActivity, "Failed to Update Task", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EditTaskResponse>, t: Throwable) {
                Toast.makeText(this@EditTaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteTask() {
        val taskId = LandingActivity.taskList[taskIndex]._id

        val call = apiService.deleteTask(taskId)
        call.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val tasks = response.body()?.data
                    if (!tasks.isNullOrEmpty()) {
                        LandingActivity.taskList[taskIndex] = tasks[0]
                        Toast.makeText(this@EditTaskActivity, "Task Updated Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditTaskActivity, "No task data returned", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditTaskActivity, "Failed to Update Task", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@EditTaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

