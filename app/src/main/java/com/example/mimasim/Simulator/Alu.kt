package com.example.mimasim.Simulator

/**
 * Created bY Martin on 23.09.2017.
 */
class Alu(name: String, short: String, description : String, private val X : Register, private val Y : Register, private val Z : Register) : Element(name, short, description) {
    fun ADD(){
       Z.Content = X.Content + Y.Content
    }
    fun AND(){
        Z.Content =  X.Content and Y.Content
    }
    fun OR(){
        Z.Content =  X.Content or Y.Content
    }
    fun XOR() {
        Z.Content =  X.Content xor Y.Content
    }
    fun negate() {
        Z.Content =   X.Content.inv()
    }
    fun eql(){
        if (X.Content == Y.Content) Z.Content =  -1
        else Z.Content =  0
    }
    fun shift(){
        Z.Content =  X.Content.ushr(Y.Content)
    }
}