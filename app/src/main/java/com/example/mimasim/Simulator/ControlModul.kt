package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class ControlModul(name: String, description : String, context: Context) : Element(name, description) {

    val IR = Register(context.resources.getString(R.string.registerIR), context.resources.getString(R.string.registerIRDescription))
    val IAR = Register(context.resources.getString(R.string.registerIAR), context.resources.getString(R.string.registerIARDescription))
    val Control = Element(context.resources.getString(R.string.IOControl), context.resources.getString(R.string.IOControlDescription))

}