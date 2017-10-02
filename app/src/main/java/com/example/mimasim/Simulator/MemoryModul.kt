package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class MemoryModul(name: String, description : String, context: Context) : Element(name, description){
    val SIR = Register(context.resources.getString(R.string.registerSIR), context.resources.getString(R.string.registerSIRDescription))
    val SAR = Register(context.resources.getString(R.string.registerSAR), context.resources.getString(R.string.registerSARDescription))
    val IOControl = Element(context.resources.getString(R.string.IOControl), context.resources.getString(R.string.IOControlDescription))
    //TODO I/O Control

    val memory = Memory(context.resources.getString(R.string.Memory), context.resources.getString(R.string.MemoryDescription))

    fun loadMapToMemory(content : Map<Int,Int>){
        for ((key, value) in content) {
            memory.Content[key] = value
        }
    }

    fun saveToMemory(instructions : ArrayList<Instruction>){
        for (instruction in instructions){
            memory.Content[instructions.indexOf(instruction)] =  instruction.adress xor instruction.opCode
        }
    }

    fun loadFromMemory(){
        SIR.Content = memory.getInstruction(SAR.Content)
    }

    fun saveToMemory(){
        memory.Content[SAR.Content] = SIR.Content
    }

}