package com.edvora.mobiletest.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edvora.mobiletest.model.City
import com.edvora.mobiletest.model.Ride
import com.edvora.mobiletest.model.State
import com.edvora.mobiletest.model.User
import com.edvora.mobiletest.network.Repository
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class MainViewModel: ViewModel() {
    companion object{
        const val TAG_NEAREST = "tag nearest"
        const val TAG_UPCOMING = "tag upcoming"
        const val TAG_PAST = "tag past"

        const val ERROR_USER = "user error"
        const val ERROR_RIDE = "ride error"
    }

    private val repo: Repository = Repository

    //This is needed to store all the values gotten from the response from the Repository
    // as the rideObserver only holds the latest value and not the entire list
    private val allRides = mutableListOf<Ride>()

    private val states = mutableListOf<State>()

    private val cities = mutableListOf<City>()

    private val _rideObserver = MutableLiveData<List<Ride>>()

    val rideObserver: LiveData<List<Ride>>
    get() = _rideObserver

    private val _userObserver = MutableLiveData<User>()

    val userObserver: LiveData<User>
    get() = _userObserver

    private val _errorObserver = MutableLiveData<String>()

    val errorObserver: LiveData<String>
        get() = _errorObserver

    var currentTag: String = TAG_NEAREST

    var userStationCode: Int = 0

    var currentState: String = ""

    var currentCity: String = ""

    fun getUsers(){
        repo.getUser({
                 _userObserver.postValue(it)
        }, {
            _errorObserver.postValue(ERROR_USER)
        })
    }

    fun getRides(){
        if(allRides.isEmpty()) {
            repo.getRides({
                allRides.clear()
                allRides.addAll(it)
                setDistance(userStationCode)
                storeAllStates()
                storeAllCities()

                postRides()
            }, { _errorObserver.postValue(ERROR_RIDE) })

        } else {
            postRides()
        }
    }

    private fun postRides(){
        var filteredList = when(currentTag) {
            TAG_NEAREST -> getNearestRides()
            TAG_PAST -> getPastRides()
            else -> getUpcomingRides()
        }

        if(currentState.isNotEmpty()) filteredList = filteredList.filter { it.state == currentState }

        if(currentCity.isNotEmpty()) filteredList = filteredList.filter { it.city == currentCity }

        _rideObserver.postValue(filteredList.sortedBy { it.distance })
    }

    private fun setDistance(userStationCode: Int){
        allRides.forEach {
            it.distance = it.stationPath
                    .map { path -> abs(path - userStationCode)  }
                    .minOrNull()!!
        }
    }

    private fun getNearestRides(): List<Ride>{
        val maxNearest = 5

        return allRides
    }

    private fun getUpcomingRides(): List<Ride>{
        val currentTime = System.currentTimeMillis()

        return allRides.filter { (it.timestamp) > currentTime }
    }

    private fun getPastRides(): List<Ride>{
        val currentTime = System.currentTimeMillis()

        return allRides.filter { (it.timestamp) < currentTime }
    }

    private fun storeAllStates(){
        var id = 0
        states.clear()

        states.addAll(allRides.map {
            State(id++, it.state)
        }.distinctBy { it.name })
    }

    private fun storeAllCities(){
        var id = 0
        cities.clear()

        cities.addAll(allRides.map {
            City(id++, it.city)
        }.distinctBy { it.name })
    }

    fun getAllStates(): List<State>{
        return states.toList()
    }

    fun getAllCities(): List<City>{
        return cities.toList()
    }
}