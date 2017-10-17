package com.example.mimasim.GUI

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Switch
import com.example.mimasim.R

/**
 * Created by root on 13.10.17.
 */
class OptionsFragment : Fragment(){

    var optionsCallback : OptionsCallback? = null
    val optionsState = OptionsState()


    interface OptionsCallback{
        fun saveOptionsCallback(optionState: OptionsState)
        fun abortOptions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val bundle = this.arguments

        optionsState.fillZeroes = bundle.getBoolean("fillZeroes", true)
        optionsState.invertViews = bundle.getBoolean("invertViews", true)
        optionsState.invertSpeed = bundle.getBoolean("invertSpeed", true)
        optionsState.maxDelay = bundle.getInt("maxDelay", 1000)

        return inflater.inflate(R.layout.options, container , false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.findViewById<Switch>(R.id.FillZerosSwitch)?.isChecked = optionsState.fillZeroes
        view?.findViewById<Switch>(R.id.invertViewsSwitch)?.isChecked = optionsState.invertViews
        view?.findViewById<Switch>(R.id.invertSpeedSwitch)?.isChecked = optionsState.invertSpeed
        view?.findViewById<EditText>(R.id.optionsMaxDelay)?.setText(optionsState.maxDelay.toString())

        view?.findViewById<Button>(R.id.optionsAbortButton)?.setOnClickListener{
            optionsCallback?.abortOptions()
        }

        view?.findViewById<Button>(R.id.optionsSaveButton)?.setOnClickListener{
            optionsState.fillZeroes = view.findViewById<Switch>(R.id.FillZerosSwitch).isChecked
            optionsState.invertViews = view.findViewById<Switch>(R.id.invertViewsSwitch).isChecked
            optionsState.invertSpeed = view.findViewById<Switch>(R.id.invertSpeedSwitch).isChecked
            optionsState.maxDelay = view.findViewById<EditText>(R.id.optionsMaxDelay).text.toString().toInt()

            optionsCallback?.saveOptionsCallback(optionsState)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            optionsCallback = context as OptionsCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement MimaFragmentCallback")
        }
    }
}