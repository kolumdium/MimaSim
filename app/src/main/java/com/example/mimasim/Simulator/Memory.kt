package com.example.mimasim.Simulator

import android.util.Log

/**
 * Created by Martin on 23.09.2017.
 */
class Memory(name: String, description : String) : Element(name, description) {
    var Content = mutableMapOf<Int, Int>()

    fun getInstruction(address : Int) : Int{
        var returnValue = 0

        for ((key,value) in Content){
           if (key == address)
               returnValue = value
       }
        Content.put(address, returnValue)
        return returnValue
    }

    fun saveToMemory(address: Int, value:Int){
        if (address in Content.keys)
            Content.set(address, value)
        else
            Content.put(address, value)
    }
}