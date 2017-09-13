package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.mimasim.MainActivity
import com.example.mimasim.R
import com.example.mimasim.Simulator.Element
import com.example.mimasim.Simulator.Register


/**
 * Created by Martin on 03.09.2017.
 */

class OptionFragment : Fragment() {

    var _currentlyLoadedElement = Element("", "")
    var _hasContent = false

    var optionCallback : optionSaveButtonClickedCallback? = null

    interface optionSaveButtonClickedCallback{
        fun updateMima()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.options, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setBackgroundColor(resources.getColor(R.color.grey))

        view?.findViewById(R.id.optionsAbort)?.setOnClickListener{
            /*  When Abort Button was Clicked Restore the View to it's original State*/
            val contentView = view?.findViewById(R.id.optionsElementContent) as EditText

            /*val inputFilter = InputFilter { charSequence, start, end, dest, dstart, dend ->
                val hexArray = "0123456789abcdefABCDEF".toCharArray()
                for ( i in charSequence!!.indices) {
                    for (hex in hexArray) {
                        if (charSequence[i] == hex) continue
                    }
                    //There is an unallowed char!!
                    return@InputFilter null
                }
                charSequence
            }

            contentView.filters = inputFilter*/

            if (_hasContent)
                contentView.setText((_currentlyLoadedElement as Register).Content.toString(), TextView.BufferType.EDITABLE)
            else
                contentView.text.clear()
            val main = activity as MainActivity
            main.extendNormal()
        }
    }

    fun updateView(currentlyLoadedElement: Element, hasContent : Boolean){
        _currentlyLoadedElement = currentlyLoadedElement
        _hasContent = hasContent

        val nameView = view?.findViewById(R.id.optionsElementName) as TextView
        nameView.text = currentlyLoadedElement.name

        val descriptionView = view?.findViewById(R.id.optionsDescription) as TextView
        descriptionView.text = currentlyLoadedElement.description

        val contentView = view?.findViewById(R.id.optionsElementContent) as EditText

        if (hasContent) {
            /*TODO check why this saves or loads trash*/
            contentView.setText( String.format(Integer.toHexString((currentlyLoadedElement as Register).Content)) , TextView.BufferType.EDITABLE)

            view?.findViewById(R.id.optionsSave)?.setOnClickListener{
                val inputString = contentView.text.toString()
                    currentlyLoadedElement.Content = Integer.decode( "0x" + inputString )
                    optionCallback?.updateMima()
                //TODO make save properly
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            optionCallback = context as optionSaveButtonClickedCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implementoptionSaveButtonClickedCallback")
        }
    }

}