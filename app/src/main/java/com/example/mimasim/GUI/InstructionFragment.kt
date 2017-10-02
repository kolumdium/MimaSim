package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.mimasim.R
import com.example.mimasim.Simulator.Instruction

/**
 * Created by Martin on 03.09.2017.
 */
class InstructionFragment : Fragment(), InstructionAdapter.saveInstructionAdapterCallback {

    /*TODO The SWipeListner does not work when we swipe in the List View!!! Find out why not and fix it*/

    var mCallback : instructionSaveButtonClickedCallback? = null
    var instructionManager = InstructionManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        instructionManager.loadDefaultInstructions()
        return inflater.inflate(R.layout.instruction_big, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setBackgroundColor( resources.getColor(R.color.lighterGrey))

        /* TODO When the save Button is Clicked save Instructions undependent from the Fragment
        * also TODO when the same button is clicked iterate all instructions and save. or make a editText onfocuschangedlistener saveInstructions*/
        view?.findViewById(R.id.instructionSaveButton)?.setOnClickListener{
            //  pass to Memory now
            mCallback?.saveInstructions(instructionManager.instructions)
        }

        view?.findViewById(R.id.instructionAbortButton)?.setOnClickListener{
            mCallback?.abortInstructions()
        }


        val listView = activity.findViewById(R.id.instructionListView) as ListView
        listView.adapter = InstructionAdapter(activity,  instructionManager.instructions, this)
        val mInstructionAdapter = listView.adapter as InstructionAdapter

        //Just a debug thing

        view?.findViewById(R.id.instructionAddButton)?.setOnClickListener{
            mInstructionAdapter.add(Instruction())
            val textView = view.findViewById(R.id.instructionTextView) as TextView
            textView.text = instructionManager.getAsCharSequence()
            Log.d("Test", "${instructionManager.getAsCharSequence()}" )
        }
    }

    fun makeBigLayout(){
        view?.findViewById(R.id.instructionSaveToFile)?.visibility = View.VISIBLE
        view?.findViewById(R.id.instructionLoadFromFile)?.visibility = View.VISIBLE
        view?.findViewById(R.id.instructionTextView)?.visibility = View.GONE
        view?.findViewById(R.id.instructionListView)?.visibility = View.VISIBLE
        view?.findViewById(R.id.scrollViewInstructions)?.visibility = View.GONE
    }

    fun makeSmallLayout(){
        view?.findViewById(R.id.instructionSaveToFile)?.visibility = View.GONE
        view?.findViewById(R.id.instructionLoadFromFile)?.visibility = View.GONE
        val textView = view?.findViewById(R.id.instructionTextView) as TextView
        view?.findViewById(R.id.instructionListView)?.visibility = View.GONE
        view?.findViewById(R.id.scrollViewInstructions)?.visibility = View.VISIBLE

        textView.visibility = View.VISIBLE
        textView.text = instructionManager.getAsCharSequence()
    }

    override fun saveInstruction(position: Int, instruction: Instruction) {
        instructionManager.instructions.set(position, instruction)
    }

    interface instructionSaveButtonClickedCallback{
        fun saveInstructions(currentInstructions : ArrayList<Instruction>)
        fun abortInstructions()
        fun addedInstruction()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as instructionSaveButtonClickedCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement instructionSaveButtonClickedCallback")
        }
    }
}