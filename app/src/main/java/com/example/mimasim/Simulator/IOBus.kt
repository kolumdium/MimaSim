package com.example.mimasim.Simulator

/**
 * Created by Martin on 08.09.2017.
 */
class IOBus(name: String, short: String, description : String, var SIR : Register) : Element(name, short, description){

    var externallyUsedAddresses = mutableMapOf<Int, Boolean>()

    init {
        // Adress, read
        //input
        externallyUsedAddresses.put(0xC000001, true)
        //output
        externallyUsedAddresses.put(0xC000002, false)
        externallyUsedAddresses.put(0xC000003, false)
    }

    //get stuff from IO Bus
    fun fromExternal(externalInput: Int){
        SIR.Content = externalInput
    }

    //Function to get acces from IO/Bus
    public fun toExternal() : Int {
        return SIR.Content
    }
}