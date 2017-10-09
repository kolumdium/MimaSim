package com.example.mimasim.GUI

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.example.mimasim.R
import com.example.mimasim.Simulator.Instruction

/**
 * Created by Martin on 09.09.2017.
 */
class InstructionAdapter(context: Context, instructions : ArrayList<Instruction> , var saveInstructionCallback : saveInstructionAdapterCallback) : ArrayAdapter<Instruction> (context, R.layout.instruction_list_item, instructions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val OPCodeArray = context.resources.getTextArray(R.array.OPCodeArray)

        val holder : ListItemHolder
        var row = convertView

        var lastItemClickedPosition = 0

        val tmpInstruction = this.getItem(position)
        /*
        * View Holder Pattern for smoother Scrolling
        * */
        if (convertView == null) {
            /*
            * If View wasn't yet inflated, inflate it and save it's contents
            * */
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(R.layout.instruction_list_item, parent, false)

            holder = ListItemHolder()
            holder.spinner = row?.findViewById(R.id.instructionItemSpinner)
            holder.editText = row.findViewById(R.id.instructionItemText)
            holder.elementStatus = row.findViewById(R.id.elementStatus)
            row.setTag(holder)
        }
        else {
            /*
            * If View was inflated before just load it's contents (no new inflation)
            * */
            holder = row?.tag as ListItemHolder
        }

        /*
        * Set Spinner Adapter
        * */
        val adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(context, R.array.OPCodeArray , android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinner?.adapter = adapter
        holder.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                /* When an item is selected get OPCode and OPCodeString and write it to the instruction
                * */
                tmpInstruction.opCode = p2
                tmpInstruction.opCodeString = OPCodeArray[p2].toString()
                holder.spinner?.setSelection(p2)
                saveInstructionCallback.saveInstruction(position, tmpInstruction)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //
            }
        }

        holder.elementStatus?.setOnClickListener{
            saveInstructionCallback.lastSelectedItem(position)
        }


        //This is terribly slow TODO check if can make faster
        if (getItem(position).isActive){
            holder.elementStatus?.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.elementStatus?.setBackgroundColor(ContextCompat.getColor(context, R.color.primary50))
        }

        holder.spinner?.setSelection(tmpInstruction.opCode)

        holder.editText?.setOnFocusChangeListener{ view: View, b: Boolean ->
            tmpInstruction.address = Integer.decode( "0x" + holder.editText?.text)
            saveInstructionCallback.saveInstruction(position, tmpInstruction)
            //saveInstructionCallback.lastSelectedItem(position)
        }

        holder.editText?.setText( Integer.toHexString(tmpInstruction.address) )
        holder.spinner?.setSelection( tmpInstruction.opCode)

        return row as View
    }

    interface saveInstructionAdapterCallback{
        fun saveInstruction(position: Int, instruction: Instruction)
        fun lastSelectedItem(position : Int)
    }

    class ListItemHolder{
        var spinner : Spinner? = null
        var editText : EditText? = null
        var elementStatus : View? = null
    }
}