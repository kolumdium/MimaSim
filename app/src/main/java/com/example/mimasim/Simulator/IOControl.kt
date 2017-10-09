package com.example.mimasim.Simulator

/**
 * Created by Martin on 09.10.2017.
 */

class IOControl (name: String, description : String) : Element(name, description){
    private val upperLimit = 0xFFFFFFF
    private val lowerLimit = 0xC000000

    fun isExternal(address : Int) : Boolean {
        return address in lowerLimit..upperLimit
    }
}