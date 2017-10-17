package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class CenterBus(name: String, description : String, var allRegsiters : ArrayList<Register>, var context: Context) : Element(name, description) {

    /*fun step(inputregister : Register){
        allRegsiters
                .filter { it.write }
                .forEach { it.Content = inputregister.Content }
    }*/

    /*For convenience*/
    fun transfer(inputregister: Register, outpuRegister: Register){
        if (inputregister.name == context.resources.getString(R.string.registerIR)){
            val maskedInput =  maskInput(inputregister.Content)
            outpuRegister.Content = maskedInput
            return
        }

        outpuRegister.Content = inputregister.Content
    }

    fun transfer(inputregister: Register, outpuRegister1: Register, outpuRegister2: Register){
        outpuRegister1.Content = inputregister.Content
        outpuRegister2.Content = inputregister.Content
    }

    fun maskInput(content : Int): Int{
        val x = content.shl(4)
        return x.shr(4)
    }
}