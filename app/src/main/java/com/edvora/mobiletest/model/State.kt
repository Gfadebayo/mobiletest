package com.edvora.mobiletest.model

data class State(val id: Int, val name: String){
    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        return (other is State) && other.name == name
    }
}
