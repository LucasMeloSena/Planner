package br.dev.lucasena.planner.ui.component

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import br.dev.lucasena.planner.R

class ActivityTimePickerDialogFragment(
    private val initialValue: Calendar? = null,
    private val onConfirm: (Int, Int) -> Unit,
    private val onCancel: () -> Unit
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = initialValue ?: Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext())
        )
    }

    fun TimePickerDialog.setupTimePicker(): TimePickerDialog =
        this.apply {
            window?.decorView?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.lime_950
                )
            )
            setButton(
                DialogInterface.BUTTON_POSITIVE,
                getString(R.string.confirmar)
            ) { _, _ -> }
            setButton(
                DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancelar)
            ) { _, _ -> onCancel() }
        }


    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        onConfirm(hour, minute)
    }

    companion object {
        const val TAG = "ActivityTimePickerDialogFragment"
    }
}