package com.example.prioritify

import SessionManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prioritify.api.TaskRequest
import com.example.prioritify.api.TaskResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var dateFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        sessionManager = SessionManager(this)  // Initialize SessionManager
        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        // Initialize EditTexts and other UI components
        val startDateTimeEditText: EditText = findViewById(R.id.editTextStartDateTime)
        val endDateTimeEditText: EditText = findViewById(R.id.editTextEndDateTime)
        val titleEditText: EditText = findViewById(R.id.editTextTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTextDescription)
        val priorityRadioGroup: RadioGroup = findViewById(R.id.radioGroupPriority)
        val createButton: Button = findViewById(R.id.buttonCreate)

        // Set up date-time picker for start date-time
        startDateTimeEditText.setOnClickListener {
            showDateTimePicker { dateTime ->
                startDateTimeEditText.setText(dateTime)
            }
        }

        // Set up date-time picker for end date-time
        endDateTimeEditText.setOnClickListener {
            showDateTimePicker { dateTime ->
                endDateTimeEditText.setText(dateTime)
            }
        }

        createButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val startTime = startDateTimeEditText.text.toString()
            val endTime = endDateTimeEditText.text.toString()
            val priority = when (priorityRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonHigh -> "HIGH"
                R.id.radioButtonMedium -> "MEDIUM"
                R.id.radioButtonLow -> "LOW"
                else -> ""
            }

            if (title.isEmpty() || description.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || priority.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields and select a priority", Toast.LENGTH_SHORT).show()
            } else {
                val reminder = getReminderTime(startTime)
                val taskRequest = TaskRequest(title, description, priority, startTime, endTime, reminder)
                createTask(taskRequest)
            }
        }
    }

    private fun showDateTimePicker(onDateTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        // Show date picker
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Show time picker after date has been selected
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                onDateTimeSelected(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun getReminderTime(startTime: String): String {
        // Placeholder logic for reminder time; you can customize this as needed
        return startTime // Use the same startTime as the reminder for simplicity
    }

    private fun createTask(taskRequest: TaskRequest) {
        val apiService = RetrofitClient.getInstance(sessionManager)
        val call = apiService.createTask(taskRequest)

        call.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@CreateTaskActivity, "Task Created Successfully", Toast.LENGTH_SHORT).show()

                    // Navigate back to LandingActivity
                    val intent = Intent(this@CreateTaskActivity, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CreateTaskActivity, "Task Creation Failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@CreateTaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
