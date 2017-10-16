package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import com.example.mimasim.R
import com.example.mimasim.Simulator.Instruction
import com.example.mimasim.Simulator.MimaModul

/**
 * Created by Martin on 03.09.2017.
 */
class InstructionFragment : Fragment(), InstructionAdapter.saveInstructionAdapterCallback , MimaModul.InstructionTrigger{
    var mCallback : InstructionCallback? = null
    var instructionManager = InstructionManager()
    var lastSelectedItem : Int = 0


    var mInstructionAdapter : InstructionAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        instructionManager.loadDefaultInstructions()
        mCallback?.saveInstructions(instructionManager.instructions)
        return inflater.inflate(R.layout.instruction_big, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setBackgroundColor( resources.getColor(R.color.lighterGrey))

        view?.findViewById<Button>(R.id.instructionSaveButton)?.setOnClickListener{
            //  pass to Memory now
            mCallback?.saveInstructions(instructionManager.instructions)
        }

        view?.findViewById<Button>(R.id.mimaClearButton)?.setOnClickListener{
            mCallback?.clearMima()
        }

        view?.findViewById<Button>(R.id.instructionsClearButton)?.setOnClickListener{
            instructionManager.clearInstructions()
            mInstructionAdapter?.notifyDataSetChanged()
        }


        val listView = activity.findViewById<ListView>(R.id.instructionListView)
        listView.adapter = InstructionAdapter(activity,  instructionManager.instructions, this)
        mInstructionAdapter = listView.adapter as InstructionAdapter

        view?.findViewById<android.support.design.widget.FloatingActionButton>(R.id.instructionAddButton)?.setOnClickListener{
            mInstructionAdapter?.add(Instruction())
            val textView = view.findViewById<TextView>(R.id.instructionTextView)
            textView.text = instructionManager.getAsCharSequence()
            Log.d("Test", "${instructionManager.getAsCharSequence()}" )
        }

        view?.findViewById<android.support.design.widget.FloatingActionButton>(R.id.instructionUpButton)?.setOnClickListener{
            lastSelectedItem = instructionManager.up(lastSelectedItem)
            mInstructionAdapter?.notifyDataSetChanged()
        }
        view?.findViewById<android.support.design.widget.FloatingActionButton>(R.id.instructionUpButton)?.setOnLongClickListener{
            lastSelectedItem = instructionManager.first(lastSelectedItem)
            mInstructionAdapter?.notifyDataSetChanged()
            true
        }
        view?.findViewById<android.support.design.widget.FloatingActionButton>(R.id.instructionDownButton)?.setOnClickListener{
            lastSelectedItem = instructionManager.down(lastSelectedItem)
            mInstructionAdapter?.notifyDataSetChanged()
        }
        view?.findViewById<android.support.design.widget.FloatingActionButton>(R.id.instructionDownButton)?.setOnLongClickListener{
            lastSelectedItem = instructionManager.last(lastSelectedItem)
            mInstructionAdapter?.notifyDataSetChanged()
            true
        }
        view?.findViewById<android.support.design.widget.FloatingActionButton>(R.id.instructionDeleteButton)?.setOnClickListener{
            lastSelectedItem = instructionManager.remove(lastSelectedItem)
            mInstructionAdapter?.notifyDataSetChanged()
        }
    }

    fun makeBigLayout(){
        view?.findViewById<Button>(R.id.instructionSaveToFile)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.instructionLoadFromFile)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.instructionTextView)?.visibility = View.GONE
        view?.findViewById<ListView>(R.id.instructionListView)?.visibility = View.VISIBLE
        view?.findViewById<ScrollView>(R.id.scrollViewInstructions)?.visibility = View.GONE
        view?.findViewById<ConstraintLayout>(R.id.instructionManagement)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.instructionsClearButton)?.visibility = View.VISIBLE

    }

    fun makeSmallLayout(){
        view?.findViewById<Button>(R.id.instructionSaveToFile)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.instructionLoadFromFile)?.visibility = View.GONE
        val textView = view?.findViewById<TextView>(R.id.instructionTextView)
        view?.findViewById<ListView>(R.id.instructionListView)?.visibility = View.GONE
        view?.findViewById<ScrollView>(R.id.scrollViewInstructions)?.visibility = View.VISIBLE
        view?.findViewById<ConstraintLayout>(R.id.instructionManagement)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.instructionsClearButton)?.visibility = View.GONE


        textView?.visibility = View.VISIBLE
        textView?.text = instructionManager.getAsCharSequence()
    }

    override fun instructionDone() {
        instructionManager.currentlyLoadedInstruction++
        val textView = view.findViewById<TextView>(R.id.instructionTextView)
        textView.text = instructionManager.getAsCharSequence()
    }

    override fun mimaReset() {
        instructionManager.currentlyLoadedInstruction = 0
    }

    override fun saveInstruction(position: Int, instruction: Instruction) {
        instructionManager.instructions.set(position, instruction)
    }

    override fun lastSelectedItem(position: Int) {
        this.lastSelectedItem = position
        instructionManager.setActive(lastSelectedItem)
        mInstructionAdapter?.notifyDataSetChanged()
    }

    interface InstructionCallback {
        fun saveInstructions(currentInstructions : ArrayList<Instruction>)
        fun closeInstructions()
        fun clearMima()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as InstructionCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement InstructionCallback")
        }
    }
}