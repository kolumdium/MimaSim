package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R

/**
 * Created by Martin on 08.09.2017.
 */
class CaculatorModul(name: String, description : String, context: Context) : Element(name, description) {
    val ONE = Register(context.resources.getString(R.string.registerONE), context.resources.getString(R.string.registerONEDescription))
    val ACC = Register(context.resources.getString(R.string.registerACC), context.resources.getString(R.string.registerACCDescription))
    val X = Register(context.resources.getString(R.string.registerX), context.resources.getString(R.string.registerXDescription))
    val Y = Register(context.resources.getString(R.string.registerY), context.resources.getString(R.string.registerYDescription))
    val Z = Register(context.resources.getString(R.string.registerZ), context.resources.getString(R.string.registerZDescription))

    init {
        ONE.Content.set(ONE.Content.lastIndex, 1)
    }
}