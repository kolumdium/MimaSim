package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class CenterBus(name: String, short: String, description : String, var allRegisters: ArrayList<Register>, var context: Context) : Element(name, short, description) {

    /*For convenience*/
    fun transfer(inputregister: Register, outpuRegister: Register){
        if (inputregister.name == context.resources.getString(R.string.registerIrName)){
            val maskedInput =  maskInput(inputregister.Content)
            outpuRegister.Content = maskedInput
            return
        }

        outpuRegister.Content = inputregister.Content
    }

    private fun maskInput(content : Int): Int{
        val x = content.shl(4)
        return x.ushr(4)
    }
}