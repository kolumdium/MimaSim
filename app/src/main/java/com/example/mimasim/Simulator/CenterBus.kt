package com.example.mimasim.Simulator

/**
 * Created by Martin on 08.09.2017.
 */
class CenterBus(name: String, description : String, var allRegsiters : ArrayList<Register>) : Element(name, description) {

    /*fun step(inputregister : Register){
        allRegsiters
                .filter { it.write }
                .forEach { it.Content = inputregister.Content }
    }*/

    /*For convenience*/
    fun transfer(inputregister: Register, outpuRegister: Register){
        outpuRegister.Content = inputregister.Content
    }

    fun transfer(inputregister: Register, outpuRegister1: Register, outpuRegister2: Register){
        outpuRegister1.Content = inputregister.Content
        outpuRegister2.Content = inputregister.Content
    }
}