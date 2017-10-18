package com.example.mimasim.Simulator

/**
 * Created by Martin on 08.09.2017.
 */
class IOBus(name: String, description : String, var SIR : Register) : Element(name, description){
    fun fromExternal(externalInput: Int){
        SIR.Content = externalInput
    }

    fun toExternal() {

    }
}