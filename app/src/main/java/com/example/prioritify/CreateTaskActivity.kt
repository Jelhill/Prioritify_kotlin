package com.example.prioritify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateTaskActivity : AppCompatActivity() {

    companion object {
        val taskList = mutableListOf<Task>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        val titleEditText: EditText = findViewById(R.id.editTextTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTextDescription)
        val fromTimeEditText: EditText = findViewById(R.id.editTextFromTime)
        val toTimeEditText: EditText = findViewById(R.id.editTextToTime)
        val priorityRadioGroup: RadioGroup = findViewById(R.id.radioGroupPriority)
        val createButton: Button = findViewById(R.id.buttonCreate)

        createButton.setOnClickListener {
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
                Toast.makeText(this, "Please fill in all fields and select a priority", Toast.LENGTH_SHORT).show()
            } else {
                val task = Task(title, description, fromTime, toTime, priority)
                taskList.add(task)
                Toast.makeText(this, "Task Created", Toast.LENGTH_SHORT).show()

                // Navigate back to LandingActivity
                val intent = Intent(this, LandingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
