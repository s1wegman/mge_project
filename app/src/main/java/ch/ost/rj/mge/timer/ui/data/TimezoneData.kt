package ch.ost.rj.mge.timer.ui.data

import com.google.gson.annotations.SerializedName

data class TimezoneData(
    @SerializedName("timezone_offset")
    val timezoneOffset : Int,

    @SerializedName("date")
    val date : String,

    @SerializedName("time_24")
    val time : String,

)

data class ConvertData(
    @SerializedName("original_time")
    val origTime : String,

    @SerializedName("converted_time")
    val convTime : String,

    @SerializedName("diff_hour")
    val diffHour : Int,

)

data class TimeData(
    val time : String,
    val date : String,
    val offset : String
)

data class ConvertTimeData(
    val date : String,
    val time : String
)