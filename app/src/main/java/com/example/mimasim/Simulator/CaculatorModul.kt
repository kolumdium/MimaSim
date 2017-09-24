package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class CaculatorModul(name: String, description : String, context: Context) : Element(name, description) {
    val ONE = Register(context.resources.getString(R.string.registerONE), context.resources.getString(R.string.registerONEDescription),1 ,true,false)
    val ACC = Register(context.resources.getString(R.string.registerACC), context.resources.getString(R.string.registerACCDescription))
    val X = Register(context.resources.getString(R.string.registerX), context.resources.getString(R.string.registerXDescription), 0, false , true)
    val Y = Register(context.resources.getString(R.string.registerY), context.resources.getString(R.string.registerYDescription), 0, false, true)
    val Z = Register(context.resources.getString(R.string.registerZ), context.resources.getString(R.string.registerZDescription), 0, true, false)

    val Alu = Alu(context.resources.getString(R.string.ALU), context.resources.getString(R.string.ALUDescription), X, Y, Z)

    /*fun decode(opcode : String){
        when(opcode){
           "ADD" ->  Z.Content = Alu.ADD(X, Y)
           "AND" ->  Z.Content = Alu.AND(X, Y)
           "OR" ->  Z.Content = Alu.OR(X, Y)
           "XOR" ->  Z.Content = Alu.XOR(X, Y)
           "EQL" ->  Z.Content = Alu.eql(X, Y)
           "NEG" ->  Z.Content = Alu.negate(X)
           "RRN" ->  Z.Content = Alu.shift(X,Y)
        }
    }*/
}