package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.mimasim.R
import com.example.mimasim.Simulator.Instruction

/**
 * Created by Martin on 03.09.2017.
 */
class InstructionFragment : Fragment(), InstructionAdapter.saveInstructionAdapterCallback {

    /*TODO The SWipeListner does not work when we swipe in the List View!!! Find out why not and fix it*/

    var mCallback : SaveButtonPushedListener? = null
    var mInstructions = ArrayList<Instruction>()
    var mInstructionAdapter : InstructionAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.instruction_big, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setBackgroundColor( resources.getColor(R.color.lighterGrey))

        /* TODO When the save Button is Clicked save Instructions undependent from the Fragment*/
        view?.findViewById(R.id.instructionSaveButton)?.setOnClickListener{
            mCallback?.saveInstructions(mInstructions)
        }


        val listView = activity.findViewById(R.id.instructionListView) as ListView
        listView.adapter = InstructionAdapter(activity,  mInstructions, this)
        mInstructionAdapter = listView.adapter as InstructionAdapter

        //Just a debug thing
        //mInstructionAdapter?.add(Instruction())
        mInstructions.add(Instruction())

        view?.findViewById(R.id.instructionAddButton)?.setOnClickListener{
            //mInstructionAdapter?.add(Instruction())
            mInstructions.add(Instruction())
            mInstructionAdapter?.notifyDataSetChanged()
        }
    }

    fun makeBigLayout(){
        view?.findViewById(R.id.instructionSaveToFile)?.visibility = View.VISIBLE
        view?.findViewById(R.id.instructionLoadFromFile)?.visibility = View.VISIBLE
    }

    fun makeSmallLayout(){
        view?.findViewById(R.id.instructionSaveToFile)?.visibility = View.GONE
        view?.findViewById(R.id.instructionLoadFromFile)?.visibility = View.GONE
    }

    fun setInstructions(instructions : ArrayList<Instruction>){
        mInstructions.clear()
        mInstructions.addAll(instructions)
        mInstructionAdapter?.notifyDataSetChanged()
    }

    interface SaveButtonPushedListener{
        fun saveInstructions(currentInstructions : ArrayList<Instruction>)
    }

    override fun saveInstruction(position: Int, instruction: Instruction) {
        /*When a Instruction was chaned in the editor save it here*/
        mInstructions[position] = instruction
        //mCallback?.saveInstructions(mInstructions)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as SaveButtonPushedListener
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement SaveButtonPushedListener")
        }
    }
}