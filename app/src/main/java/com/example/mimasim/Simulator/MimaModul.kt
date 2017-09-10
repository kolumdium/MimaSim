package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class MimaModul(name: String, description : String, context: Context) : Element(name, description) {

    val calculatorModul = CaculatorModul(context.resources.getString(R.string.calculatorModul), context.resources.getString(R.string.calculatorModulDescription), context)
    val controlModul = ControlModul(context.resources.getString(R.string.controlModul), context.resources.getString(R.string.controlModulDescription), context)
    val memoryModul = MemoryModul(context.resources.getString(R.string.memoryModul), context.resources.getString(R.string.memoryModulDescription), context)
    val centerBus = CenterBus(context.resources.getString(R.string.centerBus), context.resources.getString(R.string.centerBusDescription))
    val IOBus = IOBus(context.resources.getString(R.string.IOBus), context.resources.getString(R.string.IOBusDescription))
}