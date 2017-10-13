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
class InformationPreviewFragment : Fragment(){

    var informationPreviewCallback : InformationPreviewCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.information_preview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setOnClickListener{
            informationPreviewCallback?.informationPreviewClicked()
        }
    }

    interface InformationPreviewCallback {
        fun informationPreviewClicked()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            informationPreviewCallback = context as InformationPreviewCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement InformationPreviewCallback")
        }
    }
}