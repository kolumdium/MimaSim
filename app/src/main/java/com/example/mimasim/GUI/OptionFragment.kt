package com.example.mimasim.GUI

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.options, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setBackgroundColor(Color.GREEN)

        view?.findViewById(R.id.optionsAbort)?.setOnClickListener{
            /*  When Abort Button was Clicked Restore the View to it's original State*/
            val contentView = view?.findViewById(R.id.optionsElementContent) as EditText
            if (_hasContent)
                contentView.setText((_currentlyLoadedElement as Register).getContentAsHex(), TextView.BufferType.EDITABLE)
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
            contentView.setText((currentlyLoadedElement as Register).getContentAsHex(), TextView.BufferType.EDITABLE)

            view?.findViewById(R.id.optionsSave)?.setOnClickListener{
                currentlyLoadedElement.Content = (view.findViewById(R.id.optionsElementContent) as EditText).text.toString().toByteArray()
            }
        }
    }

}