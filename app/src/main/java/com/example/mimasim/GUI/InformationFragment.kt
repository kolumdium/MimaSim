package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.mimasim.R
import com.example.mimasim.Simulator.Element
import com.example.mimasim.Simulator.Register


/**
 * Created by Martin on 03.09.2017.
 */

class InformationFragment : Fragment() {

    var currentlyLoadedElement = Element("", "", "")
    var hasContent = false

    var informationCallback : InformationCallback? = null

    interface InformationCallback{
        fun updateMima()
        fun abortInformations()
        fun openOptionsClicked()
        fun extendInformations()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.information, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        view?.setOnLongClickListener{
            informationCallback?.extendInformations()
            true
        }

        val contentView = view?.findViewById<EditText>(R.id.informationElementContent)

        //Start Element is the Thing in Information on first Load
        currentlyLoadedElement = Element(resources.getString(R.string.startElement), "",resources.getString(R.string.startDescription))
        if (hasContent)
            contentView?.visibility = View.VISIBLE
        else
            contentView?.visibility = View.GONE

        val abortButton = view?.findViewById<Button>(R.id.informationAbort)
        abortButton?.setOnClickListener{
            /*  When Abort Button was Clicked Restore the View to it's original State*/
            if (hasContent)
                contentView?.setText((currentlyLoadedElement as Register).Content.toString(), TextView.BufferType.EDITABLE)
            else
                contentView?.text?.clear()

            informationCallback?.abortInformations()
        }
        view?.findViewById<Button>(R.id.openOptionsButton)?.setOnClickListener{
            informationCallback?.openOptionsClicked()
        }

    }

    fun updateView(currentlyLoadedElement: Element){
        this.currentlyLoadedElement = currentlyLoadedElement

        val nameView = view?.findViewById<TextView>(R.id.informationElementName)
        nameView?.text = currentlyLoadedElement.name

        val descriptionView = view?.findViewById<TextView>(R.id.informationDescription)
        descriptionView?.text = currentlyLoadedElement.description

        val contentView = view?.findViewById<EditText>(R.id.informationElementContent)

        when (currentlyLoadedElement.name){
            resources.getString(R.string.registerIrName),
            resources.getString(R.string.registerSarName),
            resources.getString(R.string.registerSirName),
            resources.getString(R.string.registerIarName),
            resources.getString(R.string.registerOneName),
            resources.getString(R.string.registerXName),
            resources.getString(R.string.registerYName),
            resources.getString(R.string.registerZName),
            resources.getString(R.string.registerAccName),
            resources.getString(R.string.registerCounterName)
            -> {
                //should be a Register when you get here.
                contentView?.visibility = View.VISIBLE
                contentView?.setText( String.format(Integer.toHexString((currentlyLoadedElement as Register).Content)) , TextView.BufferType.EDITABLE)
                hasContent = true

                view?.findViewById<Button>(R.id.informationSave)?.setOnClickListener{
                    val inputString = contentView?.text.toString()
                    //TODO this should go into an callback and then get passed to the modul. though this works too it is not as modular
                    (currentlyLoadedElement as Register).setContent( inputString )
                    informationCallback?.updateMima()
                }
            }
            else -> {
                hasContent = false
                contentView?.visibility = View.GONE
                view?.findViewById<Button>(R.id.informationSave)?.setOnClickListener{
                    informationCallback?.abortInformations()
                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            informationCallback = context as InformationCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement InformationCallback")
        }
    }

}