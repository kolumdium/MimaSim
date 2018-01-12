package com.example.mimasim.GUI

import android.os.Bundle
import android.preference.PreferenceFragment
import com.example.mimasim.R

/**
 * Created by root on 13.10.17.
 */
class OptionsFragment : PreferenceFragment()
{
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

}