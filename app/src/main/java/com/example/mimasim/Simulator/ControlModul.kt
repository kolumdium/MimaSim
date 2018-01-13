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
        instr.opCodeString = OpCodes.elementAtOrElse(opCode, {"ADD"})
        val tmpCode = instr.opCode.shl(28)
        instr.address = IR.Content xor tmpCode

        return instr
    }

    fun getOpCode() : Int{
        val content = IR.Content
        val shiftedContent = content.ushr(28)

        if (shiftedContent in 0..OpCodes.size){
            return OpCodes.indexOf(OpCodes[shiftedContent])
        }

        Log.d("OPCODE Interpreting: ","Didn't find matching OpCode please check ControlModul or input Instruction")
        return 0
    }
}