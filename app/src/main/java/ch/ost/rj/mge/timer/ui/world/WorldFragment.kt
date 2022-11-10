package ch.ost.rj.mge.timer.ui.world

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import ch.ost.rj.mge.timer.R
import ch.ost.rj.mge.timer.databinding.FragmentWorldBinding
import ch.ost.rj.mge.timer.ui.data.ConvertData
import ch.ost.rj.mge.timer.ui.data.ConvertTimeData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson


class WorldFragment : Fragment(), AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentWorldBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private lateinit var queue: RequestQueue
    private var fromLocation : String? = null
    private var toLocation : String? = null
    private var selectedTime : String? = null
    private var selectedDate : String? = null

    companion object {
        private const val BASE_URL = "https://api.ipgeolocation.io/timezone/convert?apiKey=1add865df0684bd8832a7cf740464a44"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(requireContext())
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorldBinding.inflate(inflater, container, false)

        val fromSpinner = binding.countrySelectFrom
        val toSpinner = binding.countrySelectTo

        val fromSelection = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("default_location_from", "")
        val toSelection = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("default_location_to", "")

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.city_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fromSpinner.adapter = adapter
            toSpinner.adapter = adapter

            val fromPosition = adapter.getPosition(fromSelection)
            val toPosition: Int = adapter.getPosition(toSelection)

            fromSpinner.setSelection(fromPosition)
            toSpinner.setSelection(toPosition)
        }
        fromSpinner.onItemSelectedListener = this
        toSpinner.onItemSelectedListener = this

        fromLocation = fromSelection
        toLocation = toSelection

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTime.setOnClickListener {
            val timePicker: DialogFragment = TimePickerFragment()
            timePicker.show(childFragmentManager, "time picker")
        }

        binding.buttonDate.setOnClickListener {
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(childFragmentManager, "date picker")
        }

        binding.buttonCalculate.setOnClickListener {
            getLocation()
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        binding.textTime.text = String.format("Selected Time: %s", selectedTime)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        selectedDate = String.format("%d-%02d-%02d", year, month, day)
        binding.textDate.text = String.format("Selected Date: %s", selectedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLocation() {
        val textLocationFrom = binding.textLocationFrom
        val textDateFrom = binding.textDateFrom
        val textTimeFrom = binding.textTimeFrom

        val fromSpinner = binding.countrySelectFrom
        val toSpinner = binding.countrySelectTo

        val textLocationTo = binding.textLocationTo
        val textDateTo = binding.textDateTo
        val textTimeTo = binding.textTimeTo

        if(selectedTime != null && selectedDate != null){
            val stringRequest = StringRequest(
                Request.Method.GET, getApiUrl(fromLocation!!, toLocation!!, selectedTime!!, selectedDate!!),
                { response ->
                    textLocationFrom.text = fromSpinner.getItemAtPosition(fromSpinner.selectedItemPosition).toString()
                    textDateFrom.text = selectedDate
                    textTimeFrom.text = selectedTime

                    textLocationTo.text = toSpinner.getItemAtPosition(toSpinner.selectedItemPosition).toString()
                    val (date, time) = getInformation(response)
                    textDateTo.text = date
                    textTimeTo.text = time
                },
                {
                    textLocationTo.text = "There was a problem"
                })
        queue.add(stringRequest)
        } else {
            textLocationTo.text = "Please enter your input again"
        }
    }

    private fun getApiUrl(fromLocation : String?, toLocation : String?, selectedTime : String?, selectedDate: String?): String {
        return "$BASE_URL&location_from=$fromLocation&location_to=$toLocation&time=$selectedDate%20$selectedTime"
    }

    private fun getInformation(json: String): ConvertTimeData {
        val convertData = gson.fromJson(json, ConvertData::class.java)
        val split = convertData.convTime.split(" ")

        return ConvertTimeData(split[0], split[1])
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when (parent.id) {
            R.id.country_select_from -> {
                fromLocation = parent.getItemAtPosition(pos).toString().replace(" ", "%20")
            }
            R.id.country_select_to -> {
                toLocation = parent.getItemAtPosition(pos).toString().replace(" ", "%20")
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}