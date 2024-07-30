package com.example.prioritify

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var fromTimeEditText: EditText
    private lateinit var toTimeEditText: EditText
    private lateinit var priorityRadioGroup: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private var taskIndex: Int = -1

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

        taskIndex = intent.getIntExtra("taskIndex", -1)
        val task = CreateTaskActivity.taskList[taskIndex]

        titleEditText.setText(task.title)
        descriptionEditText.setText(task.description)
        fromTimeEditText.setText(task.fromTime)
        toTimeEditText.setText(task.toTime)

        when (task.priority) {
            "High" -> priorityRadioGroup.check(R.id.radioButtonHigh)
            "Medium" -> priorityRadioGroup.check(R.id.radioButtonMedium)
            "Low" -> priorityRadioGroup.check(R.id.radioButtonLow)
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
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val fromTime = fromTimeEditText.text.toString()
        val toTime = toTimeEditText.text.toString()
        val priority = when (priorityRadioGroup.checkedRadioButtonId) {
            R.id.radioButtonHigh -> "High"
            R.id.radioButtonMedium -> "Medium"
            R.id.radioButtonLow -> "Low"
            else -> ""
        }

        if (title.isEmpty() || description.isEmpty() || fromTime.isEmpty() || toTime.isEmpty() || priority.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val task = CreateTaskActivity.taskList[taskIndex]
        task.title = title
        task.description = description
        task.fromTime = fromTime
        task.toTime = toTime
        task.priority = priority

        finish()
    }

    private fun deleteTask() {
        CreateTaskActivity.taskList.removeAt(taskIndex)
        finish()
    }
}
