package com.example.mimasim.GUI

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mimasim.Instruction
import com.example.mimasim.R

/**
 * Created by Martin on 09.09.2017.
 */
class InstructionAdapter(context: Context, instructions : ArrayList<Instruction>, var saveInstructionCallback : InstructionAdapterCallback) : ArrayAdapter<Instruction> (context, R.layout.instruction_list_item, instructions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder : ListItemHolder
        var row = convertView

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
            holder.elementHint = row.findViewById(R.id.elementHint)
            /*
            * Set Spinner Adapter
            * */
            val adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(context, R.array.OPCodeArray , android.R.layout.simple_spinner_dropdown_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinner?.adapter = adapter

            row.setTag(holder)
        }
        else {
            /*
            * If View was inflated before just load it's contents (no new inflation)
            * */
            holder = row?.tag as ListItemHolder
        }

        holder.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                /*
                * When an item is selected get OPCode and OPCodeString and write it to the instruction
                * */
                val OPCodeArray = context.resources.getTextArray(R.array.OPCodeArray)

                tmpInstruction.opCode = p2
                tmpInstruction.opCodeString = OPCodeArray[p2].toString()
                holder.spinner?.setSelection(p2)
                //TODO This makes scrolling slow but if not there the element doesn get updated on selection...
                this@InstructionAdapter.notifyDataSetChanged()
                //saveInstructionCallback.saveInstruction(position, tmpInstruction)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        holder.elementStatus?.setOnClickListener{
            saveInstructionCallback.lastSelectedItem(position)
        }
        holder.elementHint?.setOnClickListener{
            saveInstructionCallback.lastSelectedItem(position)
        }
        if (tmpInstruction.isActive){
            holder.elementStatus?.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.elementStatus?.setBackgroundColor(ContextCompat.getColor(context, R.color.primary50))
        }

        holder.editText?.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 -> tmpInstruction.address = convert(holder.editText?.text.toString()) }

        holder.editText?.setText( Integer.toHexString(tmpInstruction.address) )
        holder.spinner?.setSelection( tmpInstruction.opCode)

        var hintString = ""
        when (tmpInstruction.opCodeString){
            "LDC" -> {
                hintString = "const:"
                holder.editText?.visibility = View.VISIBLE
            }
            "RRN" -> {
                hintString = "var:"
                holder.editText?.visibility = View.VISIBLE
            }
            "HLT", "NOT", "RAR" -> {
                hintString = ""
                holder.editText?.visibility = View.INVISIBLE
            }
            else -> {
                hintString = "addr:"
                holder.editText?.visibility = View.VISIBLE}
        }
        holder.elementHint?.text = hintString

        return row as View
    }

    fun convert(hexString : String) : Int{
        var returnvalue = 0
        var contentString = hexString
        if (hexString.isEmpty()){
            contentString = "0"
        }
        //if Bigger then max length set to max value
        if (hexString.length > 8) {
            contentString = "0xffffffff"
        }

        if(hexString.length == 8) {
            //if first hexcode bigger then 7
            if (hexString[0].toInt() > 7){
                contentString = "0x" + hexString.subSequence(1, 8).toString()
                returnvalue = Integer.decode(contentString)
                val firstHex = hexString[0].toString()
                val firstHexAsInt = Integer.decode("0x" + firstHex)
                returnvalue = returnvalue xor firstHexAsInt.shl(28)
            }
            else
                returnvalue = Integer.decode("0x" + contentString)
        }
        else
            returnvalue = Integer.decode("0x" + contentString)

        return returnvalue
    }

    interface InstructionAdapterCallback {
//        fun saveInstruction(position: Int, instruction: Instruction)
        fun lastSelectedItem(position : Int)
    }

    class ListItemHolder{
        var spinner : Spinner? = null
        var editText : EditText? = null
        var elementStatus : View? = null
        var elementHint : TextView? = null
    }
}