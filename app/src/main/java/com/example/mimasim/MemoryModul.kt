package com.example.mimasim

import android.content.Context

/**
 * Created by Martin on 08.09.2017.
 */
class MemoryModul(name: String, description : String, context: Context) : Element(name, description){
    val SIR = Register(  context.resources.getString(R.string.registerSIR), context.resources.getString(R.string.registerSIRDescription))
    val SAR = Register(  context.resources.getString(R.string.registerSAR), context.resources.getString(R.string.registerSARDescription))
    val IOControl = Element(  context.resources.getString(R.string.IOControl), context.resources.getString(R.string.IOControlDescription))
}