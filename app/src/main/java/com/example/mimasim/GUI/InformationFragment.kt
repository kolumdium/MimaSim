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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.mimasim.MainActivity
import com.example.mimasim.R
import com.example.mimasim.Simulator.Element
import com.example.mimasim.Simulator.Register


/**
 * Created by Martin on 03.09.2017.
 */

class InformationFragment : Fragment() {

    var _currentlyLoadedElement = Element("", "")
    var _hasContent = false

    var informationCallback : informationSaveButtonClickedCallback? = null

    interface informationSaveButtonClickedCallback{
        fun updateMima()
        fun abortInformations()
        fun openOptionsClicked()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.information, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        val abortButton = view?.findViewById<Button>(R.id.informationAbort)
        abortButton?.setOnClickListener{
            /*  When Abort Button was Clicked Restore the View to it's original State*/
            val contentView = view.findViewById<EditText>(R.id.informationElementContent)
            contentView.visibility = View.VISIBLE
            if (_hasContent)
                contentView.setText((_currentlyLoadedElement as Register).Content.toString(), TextView.BufferType.EDITABLE)
            else
                contentView.text.clear()
            informationCallback?.abortInformations()
        }

        view?.findViewById<Button>(R.id.openOptionsButton)?.setOnClickListener{
            informationCallback?.openOptionsClicked()
        }

    }

    fun updateView(currentlyLoadedElement: Element){
        _currentlyLoadedElement = currentlyLoadedElement

        val nameView = view?.findViewById<TextView>(R.id.informationElementName)
        nameView?.text = currentlyLoadedElement.name

        val descriptionView = view?.findViewById<TextView>(R.id.informationDescription)
        descriptionView?.text = currentlyLoadedElement.description

        val contentView = view?.findViewById<EditText>(R.id.informationElementContent)

        when (currentlyLoadedElement.name){
            "ALU" , "I/O-Bus", "I/O-Control" , "Prozessorbus", "Mima", "Speicherwerk" , "Steuerwerk" , "Rechenwerk", "Counter", "Speicher" -> {
                contentView?.visibility = View.GONE
                view?.findViewById<Button>(R.id.informationSave)?.setOnClickListener{
                    informationCallback?.abortInformations()
                }
            }
            else -> {
                //should be a Register when you get here.
                contentView?.visibility = View.VISIBLE
                contentView?.setText( String.format(Integer.toHexString((currentlyLoadedElement as Register).Content)) , TextView.BufferType.EDITABLE)
                _hasContent = true

                view?.findViewById<Button>(R.id.informationSave)?.setOnClickListener{
                    val inputString = contentView?.text.toString()

                    //TODO this should go into an callback and then get passed to the modul. though this works too it is not as modular
                    (currentlyLoadedElement as Register).Content = Integer.decode( "0x" + inputString )
                    informationCallback?.updateMima()
                    contentView?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            informationCallback = context as informationSaveButtonClickedCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implementinformationSaveButtonClickedCallback")
        }
    }

}