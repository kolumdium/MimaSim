package com.example.mimasim.GUI

import android.content.Context
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
class InstructionAdapter(context: Context, instructions : ArrayList<Instruction>) : ArrayAdapter<Instruction> (context, 0, instructions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        //super.getView(position, convertView, parent)

        val holder : ListItemHolder
        var row = convertView

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
            holder.spinner = row?.findViewById(R.id.instructionItemSpinner) as Spinner
            holder.editText = row.findViewById(R.id.instructionItemText) as EditText

            row.setTag(holder)
        }
        else {
            /*
            * If View was inflated before just load it's contents (no new inflation)
            * */
            holder = row?.getTag() as ListItemHolder
        }

        /*TODO ADD functionallity for the holder (set Text)
        * get text from the instruction array!!*/

        /*
        * Set Spinner Adapter
        * */
        /*TODO set the Selected Item properly*/
        val adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(context, R.array.OPCodeArray , android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner?.adapter = adapter
        holder.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                /*
                * When an item is selected get OPCode and OPCodeString and write it to the instruction
                * */
                // TODO add this when your OPCODEArray is done
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        /*TODO see if POSITION is right or starts 1 instead of 0*/
        //holder.editText?.setText(instructions[position].adress.toString())
        //holder.spinner?.setSelection()

        return row!!
    }


    class ListItemHolder{
        var spinner : Spinner? = null
        var editText : EditText? = null
    }
}