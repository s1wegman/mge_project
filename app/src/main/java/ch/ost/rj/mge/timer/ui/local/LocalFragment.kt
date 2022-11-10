package ch.ost.rj.mge.timer.ui.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ch.ost.rj.mge.timer.databinding.FragmentLocalBinding
import ch.ost.rj.mge.timer.ui.data.TimezoneData
import ch.ost.rj.mge.timer.ui.data.TimeData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.util.*


class LocalFragment : Fragment() {

    private var _binding: FragmentLocalBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private lateinit var queue: RequestQueue
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude : Double? = null
    private var longitude : Double? = null

    @SuppressLint("MissingPermission")
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getLocation()
        }
        else {
            latitude = null
            longitude = null
        }
    }

    companion object {
        private const val BASE_URL = "https://api.ipgeolocation.io/timezone?apiKey=1add865df0684bd8832a7cf740464a44"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        queue = Volley.newRequestQueue(requireContext())
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalBinding.inflate(inflater, container, false)
        getLocation()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        binding.buttonRefresh.setOnClickListener {
            setTime()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                latitude = location?.latitude
                longitude = location?.longitude
            }
    }
    private fun setTime() {
        val time = binding.textLocal
        val date = binding.textDate
        val zone = binding.textZone

        if(latitude != null && longitude != null){
            val stringRequest = StringRequest(
                Request.Method.GET, getApiUrl(latitude!!, longitude!!),
                { response ->
                    val (t, d, z) = getInformation(response)
                    time.text = t
                    date.text = d
                    zone.text = z
                },
                {
                    time.text = "There was a problem"
                })

            queue.add(stringRequest)
        } else {
            time.text = "There was a problem"
        }
    }

    private fun getApiUrl(latitude : Double, longitude : Double): String {
        return "$BASE_URL&lat=$latitude&long=$longitude"
    }

    private fun getInformation(json : String):  TimeData {
        val timezoneData = gson.fromJson(json, TimezoneData::class.java)
        val offset = timezoneData.timezoneOffset
        var timezone = ""

        timezone = if (offset > 0) {
            "UTC+%d".format(offset)
        } else {
            "UTC%d".format(offset)
        }
        return TimeData(timezoneData.time, timezoneData.date, timezone)
    }
}