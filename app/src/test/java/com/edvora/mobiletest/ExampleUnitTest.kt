package com.edvora.mobiletest

import com.edvora.mobiletest.network.Repository
import org.junit.Test

import org.junit.Assert.*
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRides() {
        val rides = Repository.getRides()

        rides.forEach {
            println(it)
        }
    }

    @Test
    fun testNearest(){
        val model = TestModel()

        val rides = Repository.getUsers()


        val city = "Goa"

        val state = "Indore"

        rides.forEach { user ->
            model.getRides(user.stationCode, city = city).forEach {
            println(it)

            }
        }
    }

    @Test
    fun testUpcoming(){
        val model = TestModel()

        val rides = Repository.getUsers()

        val city = "Panvel"

        val state = "Indore"

        rides.forEach { user ->
            model.getRides(user.stationCode, TestModel.TAG_UPCOMING, state, city).forEach {
                println(it)
            }
        }
    }

    @Test
    fun testPast(){
        val model = TestModel()

        val rides = Repository.getUsers()

        rides.forEach { user ->
            model.getRides(user.stationCode, TestModel.TAG_PAST).forEach {
                println(it)
            }
        }
    }

    @Test
    fun testStateRandomly(){
        val model = TestModel()

        val rides = Repository.getUsers()

        val tag = Random(1).nextInt(0, 2)
        val stringTag = when(tag){
            0 -> TestModel.TAG_NEAREST
            1 -> TestModel.TAG_UPCOMING
            else -> TestModel.TAG_PAST
        }

        val states = model.getAllStates()
        val state = states[Random(3).nextInt(0, states.size)]

        rides.forEach { user ->
            model.getRides(user.stationCode, stringTag, state.name).forEach {
                println(it)
            }
        }
    }
}