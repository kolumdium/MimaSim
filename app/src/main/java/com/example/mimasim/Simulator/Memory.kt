package com.example.mimasim.Simulator

/**
 * Created by Martin on 23.09.2017.
 */
class Memory(name: String, description : String) : Element(name, description) {
    //size is 2^28 / sizeof Int
    var Content = ArrayList<Int>( Math.pow(2.0,28.0).toInt() / 32)

    fun getInstruction(adress : Int) : Int{
        return Content[adress]
    }
}