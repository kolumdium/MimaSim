package com.example.mimasim.Simulator

import kotlin.experimental.and

/**
 * Created by Martin on 08.09.2017.
 */
class Register(name: String, description : String, var Content : Int = 0x0) : Element(name, description)