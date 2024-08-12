package com.example.prioritify

import SessionManager
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.example.prioritify.api.EditTaskResponse
import com.example.prioritify.api.TaskRequest
import com.example.prioritify.api.TaskResponse
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class EditTaskDialogFragment : DialogFragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var fromTimeEditText: EditText
    private lateinit var toTimeEditText: EditText
    private lateinit var priorityRadioGroup: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private var taskIndex: Int = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_edit_task_dialog, null)

        taskIndex = arguments?.getInt("taskIndex") ?: -1
        val task = LandingActivity.taskList[taskIndex]

        titleEditText = view.findViewById(R.id.editTextTitle)
        descriptionEditText = view.findViewById(R.id.editTextDescription)
        fromTimeEditText = view.findViewById(R.id.editTextFromTime)
        toTimeEditText = view.findViewById(R.id.editTextToTime)
        priorityRadioGroup = view.findViewById(R.id.radioGroupPriority)
        saveButton = view.findViewById(R.id.buttonSave)
        deleteButton = view.findViewById(R.id.buttonDelete)

        titleEditText.setText(task.title)
        descriptionEditText.setText(task.description)
        fromTimeEditText.setText(task.startTime)
        toTimeEditText.setText(task.endTime)

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

        builder.setView(view)
        return builder.create()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val currentTime = editText.text.toString().split(":")
        val hour = if (currentTime.isNotEmpty()) currentTime[0].toInt() else 0
        val minute = if (currentTime.isNotEmpty()) currentTime[1].split(" ")[0].toInt() else 0

        val timePickerDialog = TimePickerDialog(requireContext(), { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
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
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val task = LandingActivity.taskList[taskIndex]

        // Prepare the task request for updating
        val taskRequest = TaskRequest(
            title = title,
            description = description,
            priority = priority,
            startTime = fromTime,
            endTime = toTime,
            reminder = fromTime
        )

        val apiService = RetrofitClient.getInstance(SessionManager(requireContext()))
        val call = apiService.updateTask(task._id, taskRequest)

        call.enqueue(object : Callback<EditTaskResponse> {
            override fun onResponse(call: Call<EditTaskResponse>, response: Response<EditTaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedTask = response.body()?.data
                    if (updatedTask != null) {
                        LandingActivity.taskList[taskIndex] = updatedTask
                        (activity as LandingActivity).populateTasks("PENDING") // Specify the status
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "No task data returned from server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Task update failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EditTaskResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteTask() {
        val task = LandingActivity.taskList[taskIndex]
        val taskId = task._id

        val apiService = RetrofitClient.getInstance(SessionManager(requireContext()))
        val call = apiService.deleteTask(taskId)

        call.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    // Remove task from the local list since deletion was successful
                    LandingActivity.taskList.removeAt(taskIndex)
                    // Refresh the UI for the correct task status
                    val currentStatus = if (task.status == "PENDING") "PENDING" else "COMPLETED"
                    (activity as LandingActivity).populateTasks(currentStatus)

                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete task", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        fun newInstance(taskIndex: Int): EditTaskDialogFragment {
            val fragment = EditTaskDialogFragment()
            val args = Bundle()
            args.putInt("taskIndex", taskIndex)
            fragment.arguments = args
            return fragment
        }
    }
}
