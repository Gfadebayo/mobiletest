package com.edvora.mobiletest.model

import com.google.gson.annotations.SerializedName

data class User(val name: String,
                @SerializedName("station_code") val stationCode: Int,
                @SerializedName("profile_key") val profileKey: String){

    data class UserCollector(val user: User)
}
