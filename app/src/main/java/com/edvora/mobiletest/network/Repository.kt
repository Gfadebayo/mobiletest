package com.edvora.mobiletest.network

import android.os.Handler
import com.edvora.mobiletest.BuildConfig
import com.edvora.mobiletest.model.Ride
import com.edvora.mobiletest.model.User
import com.edvora.mobiletest.model.getUser
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.emptyList
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object Repository {

    private val executors = Executors.newFixedThreadPool(4)

    private var client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()

    private val mobileTestService: MobileTestService = retrofit.create(MobileTestService::class.java)

    fun getRides(onSuccess: (List<Ride>) -> Unit, onError: () -> Unit){
        mobileTestService.getRides().enqueue(object: Callback<List<Ride>>{
            override fun onResponse(call: Call<List<Ride>>, response: Response<List<Ride>>) {

                val rides = if(response.isSuccessful) {
                    response.body()!!.onEach { it.timestamp = changeDateToTimestamp(it.date) }
                }
                else emptyList()
                onSuccess(rides)
            }

            override fun onFailure(call: Call<List<Ride>>, t: Throwable) {
                onError()
            }
        })
    }

    fun getUser(onSuccess: (User) -> Unit, onError: () -> Unit) {

        mobileTestService.getUser().enqueue(object: Callback<User.UserCollector>{
            override fun onResponse(call: Call<User.UserCollector>, response: Response<User.UserCollector>) {
                onSuccess(response.body()!!.user)
            }

            override fun onFailure(call: Call<User.UserCollector>, t: Throwable) {
                onError()
            }
        })
    }

    private fun changeDateToTimestamp(date: String): Long{

        return try {
            val format = SimpleDateFormat("MM/dd/yyyy H:m")
            val d: Date = format.parse(date)
            d.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }
}