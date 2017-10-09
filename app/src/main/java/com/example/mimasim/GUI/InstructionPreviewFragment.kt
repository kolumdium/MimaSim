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
class InstructionPreviewFragment : Fragment() {

    var instructionPreviewCallback : InstructionPreviewCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       return inflater.inflate(R.layout.instruction_preview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setOnClickListener{
            instructionPreviewCallback?.instructionPreviewClicked()
        }
    }

    interface InstructionPreviewCallback {
        fun instructionPreviewClicked()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            instructionPreviewCallback = context as InstructionPreviewCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement InstructionPreviewCallback")
        }
    }
}