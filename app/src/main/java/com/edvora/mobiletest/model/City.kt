package com.edvora.mobiletest.model

data class City(val id: Int, val name: String){
    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        return (other is City) && other.name == name
    }
}
