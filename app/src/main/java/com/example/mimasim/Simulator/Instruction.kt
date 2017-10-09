package com.example.mimasim.Simulator

/**
 * Created by Martin on 09.09.2017.
 */
class Instruction (opCode : Int = 0x0, var opCodeString: String = "ADD", address : Int = 0x0000000) {
    var isActive = false

    var address : Int = address
    set(value) {
        while (value > 0xFFFFFFF) {
            value.shr(1)
        }
        field = value
    }

    var opCode : Int = opCode
    set(value) {
        if (value > 13)
            field = 13
        else
            field = value
    }

    fun getBoth() : Int{
        return (opCode.shl(28)  xor address)
    }

}