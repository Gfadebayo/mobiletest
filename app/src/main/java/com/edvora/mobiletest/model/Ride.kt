package com.edvora.mobiletest.model

import com.google.gson.annotations.SerializedName

data class Ride(val id: Int,
                @SerializedName("origin_station_code") val originStationCode: Int,
                @SerializedName("station_path") val stationPath: Array<Int>,
                @SerializedName("destination_station_code") val destinationStationCode: Int,
                val date: String,
                @SerializedName("map_url") val mapUrl: String,
                val state: String,
                val city: String){

    //the distance between the user station code and the closest station code in the station path
    var distance = Int.MAX_VALUE

    var timestamp: Long = 0
}