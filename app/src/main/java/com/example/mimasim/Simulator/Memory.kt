package com.example.mimasim.Simulator

/**
 * Created by Martin on 23.09.2017.
 */
class Memory(name: String, short: String, description : String) : Element(name, short, description) {
    var Content = mutableMapOf<Int, Int>()

    fun getInstruction(address : Int) : Int{
        return Content.getOrPut(address, {0})
    }

    fun saveToMemory(address: Int, value:Int){
        if (Content.containsKey(address))
            Content.set(address, value)
        else
            Content.put(address, value)
    }
}