package com.example.mimasim.Simulator

import android.content.Context
import com.example.mimasim.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Martin on 08.09.2017.
 */
class MimaModul(name: String, description : String, var context: Context) : Element(name, description){

    var running = false
    var speed : Int = 1
    var currentInstruction = Instruction()


    val calculatorModul = CaculatorModul(context.resources.getString(R.string.calculatorModul), context.resources.getString(R.string.calculatorModulDescription), context)
    val controlModul = ControlModul(context.resources.getString(R.string.controlModul), context.resources.getString(R.string.controlModulDescription), context)
    val memoryModul = MemoryModul(context.resources.getString(R.string.memoryModul), context.resources.getString(R.string.memoryModulDescription), context)
    val allRegisters = ArrayList<Register>()
    val centerBus = CenterBus(context.resources.getString(R.string.centerBus), context.resources.getString(R.string.centerBusDescription), allRegisters)
    val IOBus = IOBus(context.resources.getString(R.string.IOBus), context.resources.getString(R.string.IOBusDescription))

    init {
        allRegisters.add(calculatorModul.ACC)
        allRegisters.add(calculatorModul.ONE)
        allRegisters.add(calculatorModul.X)
        allRegisters.add(calculatorModul.Y)
        allRegisters.add(calculatorModul.Z)
        allRegisters.add(memoryModul.SIR)
        allRegisters.add(memoryModul.SAR)
        allRegisters.add(controlModul.IAR)
        allRegisters.add(controlModul.IR)
        centerBus.allRegsiters.addAll(allRegisters)
    }

    fun step(){
        /*Basic
        * IAR -> (SAR, X); I/O -Read
        * ONE -> Y ; ALU->ADD
        * empty
        * Z->IAR
        * SIR -> IR*/
        when (controlModul.Counter.Content){
            0 -> {
                centerBus.transfer(controlModul.IAR, memoryModul.SAR, calculatorModul.X)
                memoryModul.loadFromMemory()
                controlModul.Counter.Content++
                //trigger UI Bus, IAR,SAR,X, memory,counter
            }
            1 -> {
                centerBus.transfer(calculatorModul.ONE, calculatorModul.Y)
                calculatorModul.Alu.ADD()
                controlModul.Counter.Content++
            }
            2 ->{
                //trigger UI Z
                controlModul.Counter.Content++
            }
            3-> {
                centerBus.transfer(calculatorModul.Z, controlModul.IAR)
                controlModul.Counter.Content++
            }
            4 -> {
                centerBus.transfer(memoryModul.SIR, controlModul.IR)
                currentInstruction = controlModul.decodeInstruction()
            }
            else -> {
                stepInstruction(currentInstruction)}
        }
    }

    fun stop(){
        running = false
    }

    fun run(){
        running = true
        while (running)
            step()
    }

    fun stepInstruction(cInstr : Instruction){
        val adress = cInstr.opCode.shl(28) xor controlModul.IR.Content
        when (cInstr.opCodeString){
            "ADD" -> ADD()
            "AND" -> AND()
            "OR" -> OR()
            "XOR" -> XOR()
            "LDV" -> LDV()
            "STV" -> STV()
            "LDC" -> LDC()
            "JMP" -> JMP()
            "JMN" -> JMN()
            "EQL" -> EQL()
            "RRN" -> RRN()
            "HLT" -> HLT()
            "NOT" -> NOT()
            "RAR" -> RAR()
        }
    }


    /*TODO Now for each operation we load the right things into the right Registers, trigger the ALU and then Registers again
    * Here is also where we should manage the triggers for the UI updates!! Highlighting stuff and so on also here is where the speed should matter!!!*/

    fun ADD() {
        /* ADD
        * IR -> SAR; IO-Read
        * ACC->X
        * empty
        * empty
        * SIR-> Y; ALU->ADD
        * empty
        * Z->ACC*/

        when (controlModul.Counter.Content){
            5 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.loadFromMemory()
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
                //trigger UI SIR
            }

            9 -> {
                centerBus.transfer(memoryModul.SIR, calculatorModul.Y)
                calculatorModul.Alu.ADD()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 ->{
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }
    }
    fun AND() {
        /* AND
        * IR -> SAR; IO-Read
        * ACC->X
        * empty
        * empty
        * SIR-> Y; ALU->AND
        * empty
        * Z->ACC*/

        when (controlModul.Counter.Content){
            5 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.loadFromMemory()
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            8 -> {
                controlModul.Counter.Content++
                //trigger UI SIR
            }

            9 -> {
                centerBus.transfer(memoryModul.SIR, calculatorModul.Y)
                calculatorModul.Alu.AND()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 ->{
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }    }

    fun OR() {
        /* OR
        * IR -> SAR; IO-Read
        * ACC->X
        * empty
        * empty
        * SIR-> Y; ALU->OR
        * empty
        * Z->ACC*/

        when (controlModul.Counter.Content){
            5 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.loadFromMemory()
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            8 -> {
                controlModul.Counter.Content++
                //trigger UI SIR
            }

            9 -> {
                centerBus.transfer(memoryModul.SIR, calculatorModul.Y)
                calculatorModul.Alu.OR()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 ->{
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }   }

    fun XOR() {
        /* XOR
         * IR -> SAR; IO-Read
         * ACC->X
         * empty
         * empty
         * SIR-> Y; ALU->XOR
         * empty
         * Z->ACC*/

        when (controlModul.Counter.Content){
            5 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.loadFromMemory()
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            8 -> {
                controlModul.Counter.Content++
                //trigger UI SIR
            }

            9 -> {
                centerBus.transfer(memoryModul.SIR, calculatorModul.Y)
                calculatorModul.Alu.XOR()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 ->{
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }   }

    fun LDV() {
        /* LDV
        * IR -> SAR; IO-Read
        * ACC->X
        * empty
        * empty
        * SIR-> ACC
        * empty
        * empty*/

        when (controlModul.Counter.Content){
            5 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.loadFromMemory()
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
                //trigger UI SIR
            }

            9 -> {
                centerBus.transfer(memoryModul.SIR, calculatorModul.ACC)
            }
            10 -> {
                controlModul.Counter.Content++
            }
            11 ->{
                controlModul.Counter.Content = 0
            }
        }
    }

    fun STV() {
        /* STV
        * ACC -> SIR
        * IR -> SAR; I/O->Write
        * empty
        * empty
        * empty
        * empty
        * empty*/
        when (controlModul.Counter.Content) {
            5 -> {
                centerBus.transfer(calculatorModul.ACC, memoryModul.SIR)
            }
            6 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.saveToMemory()
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
            else -> {
                controlModul.Counter.Content++
            }
        }
    }

    fun LDC() {
        /* LDC
        * IR -> ACC
        * empty
        * empty
        * empty
        * empty
        * empty
        * empty*/

        when (controlModul.Counter.Content) {
            5->{
                centerBus.transfer(controlModul.IR, calculatorModul.ACC)
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
            else -> {
                controlModul.Counter.Content++
            }
        }
    }

    fun JMP() {
        /*TODO maybe check if acc is positiv?!?!*/
        /* JMP
        * IR -> IAR
        * empty
        * empty
        * empty
        * empty
        * empty
        * empty*/
    }

    fun JMN() {
        //if acc is neg
        /* JMN
        * IR -> IAR
        * empty
        * empty
        * empty
        * empty
        * empty
        * empty*/
    }

    fun EQL() {
        /* EQL
        * IR -> SAR; IO-Read
        * ACC->X
        * empty
        * empty
        * SIR-> Y; ALU->EQL
        * empty
        * Z->ACC*/
        when (controlModul.Counter.Content) {
            5 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.loadFromMemory()
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
                //trigger UI SIR
            }

            9 -> {
                centerBus.transfer(memoryModul.SIR, calculatorModul.Y)
                calculatorModul.Alu.eql()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }
    }

    fun RRN() {
        /* RRN
        * empty
        * ACC->X
        * empty
        * empty
        * IR-> Y; ALU->RAR
        * empty
        * Z->ACC*/

        when (controlModul.Counter.Content) {
            5 -> {
                controlModul.Counter.Content++
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
            }

            9 -> {
                centerBus.transfer(controlModul.IR, calculatorModul.Y)
                calculatorModul.Alu.shift()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }
    }

    fun HLT() {
        stop()
    }

    fun NOT() {
        /* NOT
        * empty
        * ACC->X
        * empty
        * empty
        * ALU -> NOT
        * empty
        * Z->ACC*/

        when (controlModul.Counter.Content) {
            5 -> {
                controlModul.Counter.Content++
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
                //trigger UI SIR
            }

            9 -> {
                calculatorModul.Alu.negate()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }
    }

    fun RAR() {
        /* RAR
        * empty
        * ACC->X
        * empty
        * empty
        * ONE-> Y; ALU->RAR
        * empty
        * Z->ACC*/

        when (controlModul.Counter.Content) {
            5 -> {
                controlModul.Counter.Content++
            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
            }

            9 -> {
                centerBus.transfer(calculatorModul.ONE, calculatorModul.Y)
                calculatorModul.Alu.shift()
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0
            }
        }

    }

}