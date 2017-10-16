package com.example.mimasim.Simulator

import android.content.Context
import android.util.Log
import com.example.mimasim.GUI.MimaFragment
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class MemoryModul(name: String, description : String, context: Context, val mimaFragment: MimaFragment) : Element(name, description){
    val SIR = Register(context.resources.getString(R.string.registerSIR), context.resources.getString(R.string.registerSIRDescription))
    val SAR = Register(context.resources.getString(R.string.registerSAR), context.resources.getString(R.string.registerSARDescription))
    val IOControl = IOControl(context.resources.getString(R.string.IOControl), context.resources.getString(R.string.IOControlDescription))

    val memory = Memory(context.resources.getString(R.string.Memory), context.resources.getString(R.string.MemoryDescription))

    var externalIOTrigger : ExternalIOTrigger? = null


    init {
        try {
            externalIOTrigger = mimaFragment
        } catch (e : ClassCastException){
            Log.d("ClassCastException","Didn't implement uiTrigger")
        }
    }

    fun loadMapToMemory(content : Map<Int,Int>){
        memory.Content.putAll(content)
    }

    fun saveToMemory(instructions : ArrayList<Instruction>){
        for (instruction in instructions){
            memory.saveToMemory(instructions.indexOf(instruction) , instruction.getBoth())
        }
    }

    fun read(){
        if (IOControl.isExternal(SAR.Content))
            external()
        else
            loadFromMemory()
    }

    fun write(){
        if (IOControl.isExternal(SAR.Content))
            external()
        else
            saveToMemory()
    }

    private fun loadFromMemory(){
        //if (SIR.Content >= 0)
        SIR.Content = memory.getInstruction(SAR.Content)
    }

    private fun saveToMemory(){
        if (SIR.Content >= 0)
        memory.saveToMemory(SAR.Content, SIR.Content)
    }

    private fun external(){
        when (SAR.Content){
            0xC000001 -> {externalIOTrigger?.readExternal()}
            0xC000002 -> {externalIOTrigger?.writeExternal()}
            else -> {//Toast no external device at that position found
                externalIOTrigger?.noDeviceFound()
            }
        }
    }

    interface ExternalIOTrigger{
        fun readExternal()
        fun writeExternal()
        fun noDeviceFound()
    }

}