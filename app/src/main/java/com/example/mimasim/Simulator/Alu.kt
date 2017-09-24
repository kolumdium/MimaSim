package com.example.mimasim.Simulator

/**
 * Created bY Martin on 23.09.2017.
 */
class Alu(name: String, description : String, val X : Register, val Y : Register, val Z : Register) : Element(name, description) {
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
        Z.Content =  X.Content.shr(Y.Content)
    }
}