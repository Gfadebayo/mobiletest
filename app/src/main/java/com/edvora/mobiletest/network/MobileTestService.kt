package com.edvora.mobiletest.network

import com.edvora.mobiletest.model.Ride
import com.edvora.mobiletest.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface MobileTestService {

    @GET("/rides")
    fun getRides(): Call<List<Ride>>

    @GET("/user")
    fun getUser(): Call<User.UserCollector>
}