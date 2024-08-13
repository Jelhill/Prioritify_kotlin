package com.example.prioritify

import SessionManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prioritify.api.CreateTaskResponse
import com.example.prioritify.api.TaskRequest
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

        val startDateTimeEditText: EditText = findViewById(R.id.editTextStartDateTime)
        val endDateTimeEditText: EditText = findViewById(R.id.editTextEndDateTime)
        val titleEditText: EditText = findViewById(R.id.editTextTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTextDescription)
        val priorityRadioGroup: RadioGroup = findViewById(R.id.radioGroupPriority)
        val createButton: Button = findViewById(R.id.buttonCreate)

        startDateTimeEditText.setOnClickListener {
            showDateTimePicker { dateTime ->
                startDateTimeEditText.setText(dateTime)
            }
        }

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

            val currentTime = dateFormat.format(Calendar.getInstance().time)

            if (title.isEmpty() || description.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || priority.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields and select a priority", Toast.LENGTH_SHORT).show()
            } else if (!isEndDateValid(startTime, endTime)) {
                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()
            } else if (!isStartTimeValid(startTime, currentTime)) {
                Toast.makeText(this, "Start time cannot be in the past", Toast.LENGTH_SHORT).show()
            } else {
                val reminder = getReminderTime(startTime)
                val taskRequest = TaskRequest(title, description, priority, startTime, endTime, reminder)
                createTask(taskRequest)
            }
        }
    }

    private fun isStartTimeValid(startTime: String, currentTime: String): Boolean {
        return try {
            val startDate = dateFormat.parse(startTime)
            val currentDate = dateFormat.parse(currentTime)
            startDate.after(currentDate) || startDate == currentDate
        } catch (e: Exception) {
            false
        }
    }
    private fun showDateTimePicker(onDateTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                onDateTimeSelected(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun getReminderTime(startTime: String): String {
        return startTime
    }

    private fun isEndDateValid(startTime: String, endTime: String): Boolean {
        return try {
            val startDate = dateFormat.parse(startTime)
            val endDate = dateFormat.parse(endTime)
            endDate.after(startDate)
        } catch (e: Exception) {
            false
        }
    }

    private fun createTask(taskRequest: TaskRequest) {
        val apiService = RetrofitClient.getInstance(sessionManager)
        val call = apiService.createTask(taskRequest)

        call.enqueue(object : Callback<CreateTaskResponse> {
            override fun onResponse(call: Call<CreateTaskResponse>, response: Response<CreateTaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@CreateTaskActivity, "Task Created Successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@CreateTaskActivity, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CreateTaskActivity, "Task Creation Failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateTaskResponse>, t: Throwable) {
                Log.d("ERROR FROM CREATING", t.message.toString())
                Toast.makeText(this@CreateTaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
