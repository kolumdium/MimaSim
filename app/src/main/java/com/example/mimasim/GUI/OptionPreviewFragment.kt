package com.example.mimasim.GUI

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mimasim.R

/**
 * Created by Martin on 07.09.2017.
 */
class OptionPreviewFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.option_preview, container, false)
    }
}