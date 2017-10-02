package com.example.mimasim.Simulator

import android.content.Context
import android.util.Log
import com.example.mimasim.R
import kotlin.experimental.xor

/**
 * Created by Martin on 08.09.2017.
 */
class ControlModul(name: String, description : String, var context: Context) : Element(name, description) {

    val IR = Register(context.resources.getString(R.string.registerIR), context.resources.getString(R.string.registerIRDescription))
    val IAR = Register(context.resources.getString(R.string.registerIAR), context.resources.getString(R.string.registerIARDescription))
    val Counter = Register(context.resources.getString(R.string.registerCounter), context.resources.getString(R.string.registerCounterDescription))
    val OpCodes = context.resources.getStringArray(R.array.OPCodeArray)

    fun decodeInstruction() : Instruction{
        val instr = Instruction()
        val opCode = getOpcode()
        instr.opCode = opCode
        instr.opCodeString = OpCodes[opCode]
        return instr
    }

    fun getOpcode() : Int{
        val content = IR.Content

        content.shr(28)

        for (index in OpCodes.indices)
            if ((index xor content) == 0)
                return index

               /* The Mima does only use 14 Instruktions so we dont actually need to implement the extended version right?
              for (index in OpCodes.indices)
            when(content){
                in 0..10 -> {
                    if ((index xor content) == 0)
                        return index
                }
                in 11..14 -> {
                    Log.d("OPCODE Interpreting: ","Didn't find matching OpCode please check ControlModul or input Instruction")
                    return 0
                }
                15->{
                    content = IR.Content
                    content.shr(24)
                }
            }
               }*/

        Log.d("OPCODE Interpreting: ","Didn't find matching OpCode please check ControlModul or input Instruction")
        return 0
    }
}