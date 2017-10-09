package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mimasim.R

/**
 * Created by Martin on 07.09.2017.
 */
class OptionPreviewFragment : Fragment(){

    var optionPreviewCallback : OptionPreviewCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.option_preview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setOnClickListener{
            optionPreviewCallback?.optionPreviewClicked()
        }
    }

    interface OptionPreviewCallback {
        fun optionPreviewClicked()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            optionPreviewCallback = context as OptionPreviewCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement OptionPreviewCallback")
        }
    }
}