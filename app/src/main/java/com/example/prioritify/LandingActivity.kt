package com.example.prioritify

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val createTaskButton: FloatingActionButton = findViewById(R.id.buttonCreateTask)
        createTaskButton.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java)
            startActivity(intent)
        }

        populateTasks()
    }

    fun populateTasks() {
        val taskContainer: LinearLayout = findViewById(R.id.taskContainer)
        taskContainer.removeAllViews()

        for ((index, task) in CreateTaskActivity.taskList.withIndex()) {
            val taskView = layoutInflater.inflate(R.layout.task_item, taskContainer, false)

            val titleTextView: TextView = taskView.findViewById(R.id.textViewTaskTitle)
            val timeTextView: TextView = taskView.findViewById(R.id.textViewTaskTime)
            val priorityTextView: TextView = taskView.findViewById(R.id.textViewTaskPriority)
            val radioButtonComplete: RadioButton = taskView.findViewById(R.id.radioButtonComplete)
            val taskLayout: RelativeLayout = taskView.findViewById(R.id.taskLayout)

            titleTextView.text = task.title
            timeTextView.text = "${task.fromTime} - ${task.toTime}"
            priorityTextView.text = task.priority

            val backgroundColor = when (task.priority) {
                "High" -> ContextCompat.getColor(this, R.color.highPriorityColor)
                "Medium" -> ContextCompat.getColor(this, R.color.mediumPriorityColor)
                "Low" -> ContextCompat.getColor(this, R.color.lowPriorityColor)
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
        populateTasks()
    }
}
