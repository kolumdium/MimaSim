package com.example.mimasim.GUI

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.mimasim.R
import com.example.mimasim.Simulator.Instruction

/**
 * Created by Martin on 03.09.2017.
 */
class InstructionFragment : Fragment() {

    /*TODO The SWipeListner does not work when we swipe in the List View!!! Find out why not and fix it*/

    var instructions = ArrayList<Instruction>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       return inflater.inflate(R.layout.instruction_big, container , false)

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setBackgroundColor(Color.BLUE)
       (view?.findViewById(R.id.instructionListView) as ListView).adapter = InstructionAdapter(activity, instructions)
    }
}