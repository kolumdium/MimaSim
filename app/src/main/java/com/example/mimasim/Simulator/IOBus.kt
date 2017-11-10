package com.example.mimasim.Simulator

/**
 * Created by Martin on 08.09.2017.
 */
class IOBus(name: String, short: String, description : String, var SIR : Register) : Element(name, short, description){

    var externallyUsedAddresses = mutableMapOf<Int, Boolean>()

    init {
        // Adress, read
        externallyUsedAddresses.put(0xC000001, true)
        externallyUsedAddresses.put(0xC000002, false)
    }

    fun fromExternal(externalInput: Int){
        SIR.Content = externalInput
    }

    fun toExternal() {

    }
}