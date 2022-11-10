package ch.ost.rj.mge.timer.ui.world

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ch.ost.rj.mge.timer.R

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        val parentFragment = parentFragment as DatePickerDialog.OnDateSetListener?
        return DatePickerDialog(requireContext(), R.style.TimePicker, parentFragment, year, month, day)

    }
}