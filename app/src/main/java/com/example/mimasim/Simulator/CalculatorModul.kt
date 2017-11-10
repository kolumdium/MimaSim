package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class CalculatorModul(name: String, short: String, description : String, context: Context) : Element(name, short, description) {
    val ONE = Register(context.resources.getString(R.string.registerOneName),context.resources.getString(R.string.registerOneShort), context.resources.getString(R.string.registerOneDescription),1)
    val ACC = Register(context.resources.getString(R.string.registerAccName), context.resources.getString(R.string.registerAccShort), context.resources.getString(R.string.registerAccDescription))
    val X = Register(context.resources.getString(R.string.registerXName), context.resources.getString(R.string.registerXShort), context.resources.getString(R.string.registerXDescription), 0)
    val Y = Register(context.resources.getString(R.string.registerYName), context.resources.getString(R.string.registerYShort), context.resources.getString(R.string.registerYDescription), 0)
    val Z = Register(context.resources.getString(R.string.registerZName), context.resources.getString(R.string.registerZShort), context.resources.getString(R.string.registerZDescription), 0)

    val Alu = Alu(context.resources.getString(R.string.aluName), context.resources.getString(R.string.aluShort), context.resources.getString(R.string.aluDescription), X, Y, Z)
}