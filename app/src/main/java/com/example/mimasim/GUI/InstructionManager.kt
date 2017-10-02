package com.example.mimasim.GUI

import com.example.mimasim.Simulator.Instruction

/**
 * Created by Martin on 01.10.2017.
 */
class InstructionManager {
    var instructions = ArrayList<Instruction>()

    fun getAsString() : ArrayList<String>{
        val string = ArrayList<String>()

        instructions.mapTo(string) { "${instructions.indexOf(it)}: ${it.opCodeString} ${it.adress} \n" }

        return string
    }

    fun loadDefaultInstructions(){
        instructions.clear()
        instructions.add(Instruction())
    }

    fun getAsCharSequence() : CharSequence{
        var string = ""

        for (instruction in instructions) {
            string += "${instructions.indexOf(instruction)}: ${instruction.opCodeString} 0x${Integer.toHexString(instruction.adress)} ${System.getProperty("line.separator")}"
        }


        return string
    }

    fun saveToFile(){

    }

    fun loadFromFile(){

    }
}