package com.example.mimasim.Simulator

import android.util.Log

/**
 * Created by Martin on 23.09.2017.
 */
class Memory(name: String, description : String) : Element(name, description) {
    //size is 2^28 / sizeof Int
    var Content = ArrayList<Int>()

    init {
        if (Content.isEmpty()){
            for (i in 0 .. 1000)
                Content.add(0)
        }
     }

    fun getInstruction(adress : Int) : Int{
        /*If we get to a non initialized part of Memory initialize*/
       if (Content.lastIndex < adress)
           for (i in Content.lastIndex .. adress)
               Content.add(0)

        return Content[adress]
    }
}