
package com.example.prioritify

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

        var task = LandingActivity.taskList[taskIndex]
        task.title = title
        task.description = description
        task.startTime = fromTime
        task.endTime = toTime
        task.priority = priority

        (activity as LandingActivity).populateTasks()
        dismiss()
    }

    private fun deleteTask() {
        LandingActivity.taskList.removeAt(taskIndex)
        (activity as LandingActivity).populateTasks()
        dismiss()
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
