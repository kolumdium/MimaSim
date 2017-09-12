package com.example.mimasim.Simulator

import kotlin.experimental.and

/**
 * Created by Martin on 08.09.2017.
 */
class Register(name: String, description : String, var Content : Int = 0x00000000) : Element(name, description) {

    // private val hexArray = "0123456789ABCDEF".toCharArray()

    /*public fun getContentAsHexString() : String{
        return getContentAsHexString(this.Content)
    }*/

    // Idea From https://www.programiz.com/kotlin-programming/examples/convert-byte-array-hexadecimal
    /*fun getContentAsHexString(bytes : ByteArray) : String{
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices){
            val v = (bytes[i] and 0xFF.toByte()).toInt()

            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }*/
}