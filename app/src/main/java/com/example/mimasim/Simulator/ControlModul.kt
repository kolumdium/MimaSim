package com.example.mimasim.Simulator

import android.content.Context
import android.util.Log
import com.example.mimasim.Instruction
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class ControlModul(name: String, short: String, description: String, var context: Context) : Element(name, short, description) {

    val IR = Register(context.resources.getString(R.string.registerIrName),context.resources.getString(R.string.registerIrShort), context.resources.getString(R.string.registerIrDescription))
    val IAR = Register(context.resources.getString(R.string.registerIarName),context.resources.getString(R.string.registerIarShort), context.resources.getString(R.string.registerIarDescription))
    val Counter = Register(context.resources.getString(R.string.registerCounterName),context.resources.getString(R.string.registerCounterShort), context.resources.getString(R.string.registerCounterDescription))
    val OpCodes = context.resources.getStringArray(R.array.OPCodeArray)

    fun decodeInstruction() : Instruction {
        val instr = Instruction()
        val opCode = getOpCode()
        instr.opCode = opCode
        instr.opCodeString = OpCodes[opCode]
        val tmpCode = instr.opCode.shl(28)
        instr.address = IR.Content xor tmpCode

        return instr
    }

    fun getOpCode() : Int{
        val content = IR.Content
        var shiftedContent = content.shr(28)

        for (index in OpCodes.indices)
            if ((index == shiftedContent))
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