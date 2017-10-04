package com.example.mimasim.Simulator

import android.content.Context
import android.util.Log
import com.example.mimasim.GUI.MimaFragment
import com.example.mimasim.GUI.OptionFragment
import com.example.mimasim.MainActivity
import com.example.mimasim.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Martin on 08.09.2017.
 */
class MimaModul(name: String, description : String, var context: Context, val mimaFragment: MimaFragment) : Element(name, description){

    var running = false
    var currentInstruction = Instruction()

    val calculatorModul = CaculatorModul(context.resources.getString(R.string.calculatorModul), context.resources.getString(R.string.calculatorModulDescription), context)
    val controlModul = ControlModul(context.resources.getString(R.string.controlModul), context.resources.getString(R.string.controlModulDescription), context)
    val memoryModul = MemoryModul(context.resources.getString(R.string.memoryModul), context.resources.getString(R.string.memoryModulDescription), context)
    val allRegisters = ArrayList<Register>()
    val centerBus = CenterBus(context.resources.getString(R.string.centerBus), context.resources.getString(R.string.centerBusDescription), allRegisters)
    val IOBus = IOBus(context.resources.getString(R.string.IOBus), context.resources.getString(R.string.IOBusDescription))

    var uiTrigger: UITrigger? = null

    init {
        allRegisters.add(calculatorModul.ACC)
        allRegisters.add(calculatorModul.X)
        allRegisters.add(calculatorModul.Y)
        allRegisters.add(calculatorModul.Z)
        allRegisters.add(memoryModul.SIR)
        allRegisters.add(memoryModul.SAR)
        allRegisters.add(controlModul.IAR)
        allRegisters.add(controlModul.IR)
        allRegisters.add(controlModul.Counter)
        centerBus.allRegsiters.addAll(allRegisters)

        try {
            uiTrigger = mimaFragment
        } catch (e : ClassCastException){
           Log.d("ClassCastException","Didn't implement uiTrigger")
        }
    }

    fun speedChanged(speed : Long ){
        if (speed < 100){
            uiTrigger?.normal()
            uiTrigger = null
        }
        else{
            try {
                uiTrigger = mimaFragment
            } catch (e : ClassCastException){
                Log.d("ClassCastException","Didn't implement uiTrigger")
            }
        }
    }

    fun reset(){
        /*TODO all Registers to 0
        **/
        for (register in allRegisters)
            register.Content = 0
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
                uiTrigger?.normal()
                centerBus.transfer(controlModul.IAR, memoryModul.SAR, calculatorModul.X)
                memoryModul.loadFromMemory()
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowIar(true, false)
                uiTrigger?.arrowX(true)
                uiTrigger?.arrowsSarMem(true)
                uiTrigger?.mem("READ")
            }
            1 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowIar(false, false)
                uiTrigger?.arrowX(false)
                uiTrigger?.arrowsSarMem(false)

                centerBus.transfer(calculatorModul.ONE, calculatorModul.Y)
                calculatorModul.Alu.ADD()
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowOne(true)
                uiTrigger?.arrowY(true)
                uiTrigger?.alu("ADD")
                uiTrigger?.mem("READING...")

            }
            2 ->{
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowOne(false)
                uiTrigger?.arrowY(false)

                controlModul.Counter.Content++

                uiTrigger?.highlightRegister(true,"Z")
            }
            3-> {
                uiTrigger?.highlightRegister(false, "Z")

                centerBus.transfer(calculatorModul.Z, controlModul.IAR)
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowZ(true)
                uiTrigger?.arrowIar(true, true)
                uiTrigger?.mem("READ DONE")

            }
            4 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowZ(false)
                uiTrigger?.arrowIar(false, true)

                centerBus.transfer(memoryModul.SIR, controlModul.IR)
                currentInstruction = controlModul.decodeInstruction()
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowSirToMemory(true)
                uiTrigger?.arrowIr(true, true)
                uiTrigger?.mem("")
                uiTrigger?.arrowSirToBus(true, false)
            }
            5 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowSirToMemory(false)
                uiTrigger?.arrowIr(false, true)
                uiTrigger?.arrowSirToBus(false, false)

                stepInstruction(currentInstruction)
            }
            else -> {
                stepInstruction(currentInstruction)}
        }
    }

    fun stop(){
        running = false
    }

    fun stepInstruction(cInstr : Instruction){
        //val adress = cInstr.opCode.shl(28) xor controlModul.IR.Content
        when (cInstr.opCodeString){
            "ADD" -> common("ADD")
            "AND" -> common("AND")
            "OR" -> common("OR")
            "XOR" -> common("XOR")
            "LDV" -> LDV()
            "STV" -> STV()
            "LDC" -> LDC()
            "JMP" -> JMP()
            "JMN" -> JMN()
            "EQL" -> common("EQL")
            "RRN" -> RRN()
            "HLT" -> HLT()
            "NOT" -> NOT()
            "RAR" -> RAR()
        }
    }

    fun common(aluInstrs: String) {
        /* common
       * IR -> SAR; IO-Read
       * ACC->X
       * empty
       * empty
       * SIR-> Y; ALU->instr
       * empty
       * Z->ACC*/
        when (controlModul.Counter.Content) {
            5 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.loadFromMemory()

                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowsSarMem(true)
                uiTrigger?.arrowIr(true, false)
                uiTrigger?.mem("READ")

            }
            6 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowsSarMem(false)
                uiTrigger?.arrowIr(false, false)

                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowAcc(true, false)
                uiTrigger?.arrowX(true)
                uiTrigger?.mem("READING...")

            }
            7 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowAcc(false, false)
                uiTrigger?.arrowX(false)
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++

                uiTrigger?.arrowSirToMemory(true)
                uiTrigger?.mem("READ DONE")
            }

            9 -> {
                uiTrigger?.arrowSirToMemory(false)

                centerBus.transfer(memoryModul.SIR, calculatorModul.Y)

                when (aluInstrs) {
                    "ADD" -> {
                        calculatorModul.Alu.ADD()
                        uiTrigger?.alu("ADD")
                    }
                    "AND" -> {
                        calculatorModul.Alu.AND()
                        uiTrigger?.alu("AND")
                    }
                    "OR" -> {
                        calculatorModul.Alu.OR()
                        uiTrigger?.alu("OR")
                    }
                    "XOR" -> {
                        calculatorModul.Alu.XOR()
                        uiTrigger?.alu("XOR")
                    }
                    "EQL" -> {
                        calculatorModul.Alu.eql()
                        uiTrigger?.alu("EQL")
                    }
                }

                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowSirToBus(true, false)
                uiTrigger?.arrowY(true)
                uiTrigger?.mem("")
            }
            10 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowSirToBus(false, false)
                uiTrigger?.arrowY(false)

                controlModul.Counter.Content++

                uiTrigger?.highlightRegister(true, "Z")
            }
            11 -> {
                uiTrigger?.highlightRegister(false, "Z")

                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowZ(true)
                uiTrigger?.arrowAcc(true, true)

                controlModul.Counter.Content = 0
            }
        }
    }

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

                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowsSarMem(true)
                uiTrigger?.arrowIr(true, false)
                uiTrigger?.mem("READ")

            }
            6 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowsSarMem(false)
                uiTrigger?.arrowIr(false,false)

                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowAcc(true, false)
                uiTrigger?.arrowX(true)
                uiTrigger?.mem("READING...")

            }
            7 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowAcc(false, false)
                uiTrigger?.arrowX(false)
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++

                uiTrigger?.arrowSirToMemory(true)
                uiTrigger?.mem("READ DONE")
            }

            9 -> {
                uiTrigger?.arrowSirToMemory(false)

                centerBus.transfer(memoryModul.SIR, calculatorModul.ACC)
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowSirToBus(true, false)
                uiTrigger?.arrowAcc(true, true)
                uiTrigger?.mem("")
            }
            10 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowSirToBus(false, false)
                uiTrigger?.arrowAcc(false, true)
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
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowSirToBus(true, true)
                uiTrigger?.arrowAcc(true, false)
            }
            6 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowSirToBus(false, true)
                uiTrigger?.arrowAcc(false, false)

                centerBus.transfer(controlModul.IR, memoryModul.SAR)
                memoryModul.saveToMemory()
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowIr(true, false)
                uiTrigger?.arrowsSarMem(true)
                uiTrigger?.mem("WRITE")

            }
            7-> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowIr(false, false)
                uiTrigger?.arrowsSarMem(false)
                controlModul.Counter.Content++
                uiTrigger?.mem("WRITING...")
            }
            9->{
                controlModul.Counter.Content++

                uiTrigger?.mem("")
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowZ(true)
                uiTrigger?.arrowAcc(true, true)
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

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowIr(true, false)
                uiTrigger?.arrowAcc(true, true)
            }

            6->{
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowIr(false, false)
                uiTrigger?.arrowAcc(false, true)
            }
            11 -> {
                controlModul.Counter.Content = 0
            }
            else -> {
                controlModul.Counter.Content++
            }
        }
    }

    fun JMP() {
        /* JMP
        * IR -> IAR
        * empty
        * empty
        * empty
        * empty
        * empty
        * empty*/

        when (controlModul.Counter.Content){
            5 -> {
                centerBus.transfer(controlModul.IR, controlModul.IAR)
                uiTrigger?.centerBus(true)
                uiTrigger?.arrowIr(true, false)
                uiTrigger?.arrowIar(true, true)
                controlModul.Counter.Content++
            }
            6 -> {
                uiTrigger?.centerBus(true)
                uiTrigger?.arrowIr(false, false)
                uiTrigger?.arrowIar(false, true)
                controlModul.Counter.Content++
            }
            else -> controlModul.Counter.Content++
        }
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

        when (controlModul.Counter.Content){
            5 -> {
                if (calculatorModul.ACC.Content == -1){
                    centerBus.transfer(controlModul.IR, controlModul.IAR)
                    uiTrigger?.centerBus(true)
                    uiTrigger?.arrowIr(true, false)
                    uiTrigger?.arrowIar(true, true)
                    controlModul.Counter.Content++
                }
            }
            6 -> {
                    uiTrigger?.centerBus(true)
                    uiTrigger?.arrowIr(false, false)
                    uiTrigger?.arrowIar(false, true)
                    controlModul.Counter.Content++
                }
            else -> controlModul.Counter.Content++
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
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowAcc(true, false)
                uiTrigger?.arrowX(true)
            }
            7 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowAcc(false, false)
                uiTrigger?.arrowX(false)
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
            }

            9 -> {
                centerBus.transfer(controlModul.IR, calculatorModul.Y)
                calculatorModul.Alu.shift()
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowIr(true, false)
                uiTrigger?.arrowY(true)
                uiTrigger?.alu("SHIFT")
            }
            10 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowIr(false, false)
                uiTrigger?.arrowY(false)

                controlModul.Counter.Content++

                uiTrigger?.highlightRegister( true,"Z")
            }
            11 -> {
                uiTrigger?.highlightRegister( false,"Z")

                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowZ(true)
                uiTrigger?.arrowAcc(true, true)
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
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowAcc(true, false)
                uiTrigger?.arrowX(true)
            }
            7 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowAcc(false, false)
                uiTrigger?.arrowX(false)
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
                uiTrigger?.arrowSirToMemory(true)
            }

            9 -> {
                uiTrigger?.arrowSirToMemory(false)

                calculatorModul.Alu.negate()
                controlModul.Counter.Content++

                uiTrigger?.alu("NOT")
            }
            10 -> {
                controlModul.Counter.Content++
                //trigger UI Z
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowZ(true)
                uiTrigger?.arrowAcc(true, true)
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
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowAcc(true, false)
                uiTrigger?.arrowX(true)
            }
            7 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowAcc(false, false)
                uiTrigger?.arrowX(false)
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++
            }

            9 -> {
                centerBus.transfer(calculatorModul.ONE, calculatorModul.Y)
                calculatorModul.Alu.shift()
                controlModul.Counter.Content++

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowOne(true)
                uiTrigger?.arrowY(true)
                uiTrigger?.alu("SHIFT")
            }
            10 -> {
                uiTrigger?.centerBus(false)
                uiTrigger?.arrowOne(false)
                uiTrigger?.arrowY(false)

                controlModul.Counter.Content++

                uiTrigger?.highlightRegister(true, "Z")
            }
            11 -> {
                uiTrigger?.highlightRegister(false, "Z")

                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus(true)
                uiTrigger?.arrowZ(true)
                uiTrigger?.arrowAcc(true, true)
            }
        }

    }

    interface UITrigger{
        fun centerBus(activate: Boolean)
        fun highlightRegister(activate: Boolean, register: String)
        fun arrowIr(activate : Boolean, ingoing: Boolean)
        fun arrowIar(activate : Boolean, ingoing: Boolean)
        fun arrowAcc(activate : Boolean, ingoing: Boolean)
        fun arrowOne(activate : Boolean)
        fun arrowX(activate : Boolean)
        fun arrowY(activate : Boolean)
        fun arrowZ(activate : Boolean)
        fun arrowSirToMemory(activate: Boolean)
        fun arrowSirToBus(activate: Boolean, ingoing: Boolean)
        fun arrowsSarIO(activate: Boolean)
        fun arrowsSarMem(activate: Boolean)
        fun alu(instruction: String)
        fun mem(instruction: String)
        fun ioBus(activate: Boolean)
        fun ioControl(state : String)
        fun normal()
    }

}