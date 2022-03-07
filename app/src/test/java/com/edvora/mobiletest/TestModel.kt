package com.edvora.mobiletest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.edvora.mobiletest.model.City
import com.edvora.mobiletest.model.Ride
import com.edvora.mobiletest.model.State
import com.edvora.mobiletest.model.User
import com.edvora.mobiletest.network.Repository
import kotlin.random.Random

class TestModel {
    companion object{
        const val TAG_NEAREST = "tag nearest"
        const val TAG_UPCOMING = "tag upcoming"
        const val TAG_PAST = "tag past"
    }

    private val repo: Repository = Repository

    //This is needed to store all the values gotten from the response from the Repository
    // as the rideObserver only holds the latest value and not the entire list
    private val allRides = mutableListOf<Ride>()

    private val allUsers = mutableListOf<User>()

    private val states = mutableListOf<State>()

    private val cities = mutableListOf<City>()

    private val _rideObserver = MutableLiveData<List<Ride>>()

    val rideObserver: LiveData<List<Ride>>
        get() = _rideObserver

    private val _userObserver = MutableLiveData<User>()

    val userObserver: LiveData<User>
        get() = _userObserver

    fun getUsers(){
        val users = repo.getUsers()

        allUsers.clear()
        allUsers.addAll(users)
        val randomUser = Random(1).nextInt(0, users.size)

        _userObserver.postValue(users[randomUser])
    }

    private fun storeAllStates(){
        var id = 0
        states.clear()

        states.addAll(allRides.map {
            State(id++, it.state)
        }.distinct())
    }

    private fun storeAllCities(){
        var id = 0
        cities.clear()

        cities.addAll(allRides.map {
            City(id++, it.city)
        }.distinct())
    }

    fun getRides(userStationCode: Int, tag: String = TAG_NEAREST, state: String = "", city: String = ""): MutableList<Ride>{
        var filteredList : MutableList<Ride> = (if(allRides.isEmpty()) {
            val rides = repo.getRides()
            allRides.addAll(rides)
            setDistance(userStationCode)
            storeAllStates()
            storeAllCities()

            when(tag) {
                TAG_NEAREST -> getNearestRides()
                TAG_PAST -> getPastRides()
                else -> getUpcomingRides()
            }

        } else {
            when(tag) {
                TAG_NEAREST -> getNearestRides()
                TAG_PAST -> getPastRides()
                else -> getUpcomingRides()
            }
        }).toMutableList()

        if(state.isNotEmpty()) filteredList = filteredList.filter { it.state == state }.toMutableList()

        if(city.isNotEmpty()) filteredList = filteredList.filter { it.city == city }.toMutableList()

//        filteredList.sortWith({
//            it.distance
//        })
        return filteredList.sortedBy { it.distance }.toMutableList()
    }

    private fun setDistance(userStationCode: Int){
        allRides.forEach {
            it.distance = it.stationPath
                    .map { path -> Math.abs(path - userStationCode)  }
                    .minOrNull()!!
        }
    }

    private fun getNearestRides(): List<Ride>{
        val maxNearest = 5
        println("Getting the nearest")

        return allRides.filter {
        println("${it.id} distance is ${it.distance}")
            it.distance <= maxNearest
        }
    }

    private fun getUpcomingRides(): List<Ride>{
        val currentTime = System.currentTimeMillis()

        return allRides.filter { (it.timestamp*1000) > currentTime }
    }

    private fun getPastRides(): List<Ride>{
        val currentTime = System.currentTimeMillis()

        return allRides.filter { (it.timestamp*1000) < currentTime }
    }

    fun getAllStates(): List<State>{
        return states.toList()
    }

    fun getAllCities(): List<City>{
        return cities.toList()
    }
}