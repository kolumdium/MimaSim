package com.example.mimasim.Simulator

import android.content.Context
import android.util.Log
import com.example.mimasim.GUI.MimaFragment
import com.example.mimasim.Instruction
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class MemoryModul(name: String, short: String, description : String, context: Context, val mimaFragment: MimaFragment) : Element(name, short, description){
    val SIR = Register(context.resources.getString(R.string.registerSirName),context.resources.getString(R.string.registerSirShort), context.resources.getString(R.string.registerSirDescription))
    val SAR = Register(context.resources.getString(R.string.registerSarName),context.resources.getString(R.string.registerSarShort), context.resources.getString(R.string.registerSarDescription))
    val IOControl = IOControl(context.resources.getString(R.string.ioControlName),context.resources.getString(R.string.ioControlName), context.resources.getString(R.string.ioControlDescription))
    val ioBus = IOBus(context.resources.getString(R.string.ioBusName),context.resources.getString(R.string.ioBusShort), context.resources.getString(R.string.ioBusDescription), SIR)

    val memory = Memory(context.resources.getString(R.string.memoryName), context.resources.getString(R.string.memoryShort), context.resources.getString(R.string.memoryDescription))

    var externalIOTrigger : ExternalIOTrigger? = null


    interface ExternalIOTrigger{
        fun readExternal()
        fun writeExternal()
        fun noDeviceFound()
    }

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
        SIR.Content = memory.getInstruction(SAR.Content)
    }

    private fun saveToMemory(){
        if (SIR.Content >= 0)
        memory.saveToMemory(SAR.Content, SIR.Content)
    }

    private fun external(){
        if (SAR.Content in ioBus.externallyUsedAddresses) {
            val read = ioBus.externallyUsedAddresses.get(SAR.Content)
            if (read!!) {
                externalIOTrigger?.readExternal()
            } else {
                externalIOTrigger?.writeExternal()
            }
        } else {
         externalIOTrigger?.noDeviceFound()
        }
    }
}