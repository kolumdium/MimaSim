package com.example.mimasim.Simulator

import android.content.Context
import android.util.Log
import com.example.mimasim.GUI.InstructionFragment
import com.example.mimasim.GUI.MimaFragment
import com.example.mimasim.R
import kotlin.collections.ArrayList

/**
 * Created by Martin on 08.09.2017.
 */
class MimaModul(name: String, description : String, var context: Context, val mimaFragment: MimaFragment, val instructionFragment: InstructionFragment) : Element(name, description){

    var running = false
    var currentInstruction = Instruction()

    val calculatorModul = CaculatorModul(context.resources.getString(R.string.calculatorModul), context.resources.getString(R.string.calculatorModulDescription), context)
    val controlModul = ControlModul(context.resources.getString(R.string.controlModul), context.resources.getString(R.string.controlModulDescription), context)
    val memoryModul = MemoryModul(context.resources.getString(R.string.memoryModul), context.resources.getString(R.string.memoryModulDescription), context, mimaFragment)
    val allRegisters = ArrayList<Register>()
    val centerBus = CenterBus(context.resources.getString(R.string.centerBus), context.resources.getString(R.string.centerBusDescription), allRegisters)
    val IOBus = IOBus(context.resources.getString(R.string.IOBus), context.resources.getString(R.string.IOBusDescription))

    var uiTrigger: UITrigger? = null
    var instructionTrigger: InstructionTrigger? = null


    interface UITrigger{
        fun centerBus()
        fun highlightRegister(activate: Boolean, register: String)
        fun arrowIr(ingoing: Boolean)
        fun arrowIar(ingoing: Boolean)
        fun arrowAcc(ingoing: Boolean)
        fun arrowOne()
        fun arrowX()
        fun arrowY()
        fun arrowZ()
        fun arrowSirToMemory()
        fun arrowSirToBus( ingoing: Boolean)
        fun arrowsSarIO()
        fun arrowsSarMem()
        fun alu(instruction: String)
        fun mem(instruction: String)
        fun ioBus()
        fun ioControl(state : String)
        fun ioRead()
        fun ioWrite()
        fun ioClear()
        fun normal()
    }

    interface InstructionTrigger{
        fun instructionDone()
        fun mimaReset()
    }

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
        try {
            instructionTrigger = instructionFragment
        } catch (e : ClassCastException){
           Log.d("ClassCastException","Didn't implement instructionTrigger")
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
        for (register in allRegisters)
            register.Content = 0
        instructionTrigger?.mimaReset()
    }

    fun step(){
        /*Basic
        * IAR -> (SAR, X); I/O -Read
        * ONE -> Y ; ALU->ADD
        * empty
        * Z->IAR
        * SIR -> IR*/

        uiTrigger?.normal()

        when (controlModul.Counter.Content){
            0 -> {
                centerBus.transfer(controlModul.IAR, memoryModul.SAR, calculatorModul.X)

                if (memoryModul.IOControl.isExternal(memoryModul.SAR.Content)){
                    uiTrigger?.ioRead()
                    uiTrigger?.arrowsSarIO()
                    controlModul.Counter.Content++

                }
                else {
                    controlModul.Counter.Content++
                    uiTrigger?.centerBus()
                    uiTrigger?.arrowIar(false)
                    uiTrigger?.arrowX()
                    uiTrigger?.arrowsSarMem()
                    uiTrigger?.mem("READ")
                }

            }
            1 -> {
                centerBus.transfer(calculatorModul.ONE, calculatorModul.Y)
                calculatorModul.Alu.ADD()
                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowOne()
                uiTrigger?.arrowY()
                uiTrigger?.alu("ADD")
                if (!memoryModul.IOControl.isExternal(memoryModul.SAR.Content))
                    uiTrigger?.mem("READING...")

            }
            2 ->{
                controlModul.Counter.Content++

                uiTrigger?.highlightRegister(true,"Z")
            }
            3-> {
                uiTrigger?.highlightRegister(false, "Z")

                centerBus.transfer(calculatorModul.Z, controlModul.IAR)
                controlModul.Counter.Content++

                memoryModul.read()

                uiTrigger?.centerBus()
                uiTrigger?.arrowZ()
                uiTrigger?.arrowIar(true)
                if (!memoryModul.IOControl.isExternal(memoryModul.SAR.Content))
                    uiTrigger?.mem("READ DONE")

            }
            4 -> {
                centerBus.transfer(memoryModul.SIR, controlModul.IR)
                currentInstruction = controlModul.decodeInstruction()
                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowSirToMemory()
                uiTrigger?.arrowIr(true)
                uiTrigger?.mem("")
                uiTrigger?.arrowSirToBus( false)
            }
            11 -> {
                stepInstruction(currentInstruction)
                instructionTrigger?.instructionDone()
            }
            else -> {
                stepInstruction(currentInstruction)}
        }
    }

    fun stop(){
        running = false
    }

    fun stepInstruction(cInstr : Instruction){
        //val address = cInstr.opCode.shl(28) xor controlModul.IR.Content
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
                memoryModul.read()

                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowsSarMem()
                uiTrigger?.arrowIr(false)
                uiTrigger?.mem("READ")

            }
            6 -> {
                centerBus.transfer(calculatorModul.ACC, calculatorModul.X)
                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowAcc(false)
                uiTrigger?.arrowX()
                uiTrigger?.mem("READING...")

            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++

                uiTrigger?.arrowSirToMemory()
                uiTrigger?.mem("READ DONE")
            }

            9 -> {
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

                uiTrigger?.centerBus()
                uiTrigger?.arrowSirToBus(false)
                uiTrigger?.arrowY()
                uiTrigger?.mem("")
            }
            10 -> {
                controlModul.Counter.Content++
                uiTrigger?.highlightRegister(true, "Z")
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)

                uiTrigger?.centerBus()
                uiTrigger?.arrowZ()
                uiTrigger?.arrowAcc(true)

                controlModul.Counter.Content = 0
            }
        }
    }

    fun LDV() {
        /* LDV
        * IR -> SAR; IO-Read
        * empty
        * empty
        * empty
        * SIR-> ACC
        * empty
        * empty*/

        when (controlModul.Counter.Content){
            5 -> {

                centerBus.transfer(controlModul.IR, memoryModul.SAR)

                if (memoryModul.IOControl.isExternal(memoryModul.SAR.Content)){
                    uiTrigger?.ioRead()
                    uiTrigger?.arrowsSarIO()
                }
                else {
                    controlModul.Counter.Content++
                    uiTrigger?.centerBus()
                    uiTrigger?.arrowsSarMem()
                    uiTrigger?.arrowIr(false)
                    uiTrigger?.mem("READ")
                }
                controlModul.Counter.Content++

            }
            6 -> {
                controlModul.Counter.Content++

                if (!memoryModul.IOControl.isExternal(memoryModul.SAR.Content))
                    uiTrigger?.mem("READING...")

            }
            7 -> {
                controlModul.Counter.Content++
            }
            8 -> {
                controlModul.Counter.Content++

                memoryModul.read()

                uiTrigger?.arrowSirToMemory()
                if (!memoryModul.IOControl.isExternal(memoryModul.SAR.Content))
                    uiTrigger?.mem("READ DONE")
            }

            9 -> {
                centerBus.transfer(memoryModul.SIR, calculatorModul.ACC)
                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowSirToBus( false)
                uiTrigger?.arrowAcc( true)
                uiTrigger?.mem("")
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

                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowSirToBus(true)
                uiTrigger?.arrowAcc( false)
            }
            6 -> {
                centerBus.transfer(controlModul.IR, memoryModul.SAR)

                memoryModul.write()

                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowIr( false)
                uiTrigger?.arrowsSarMem()
                if (!memoryModul.IOControl.isExternal(memoryModul.SAR.Content))
                    uiTrigger?.mem("WRITE")
                else
                    uiTrigger?.ioWrite()

            }
            7-> {
                controlModul.Counter.Content++
                if (!memoryModul.IOControl.isExternal(memoryModul.SAR.Content))
                    uiTrigger?.mem("WRITING...")
            }
            9->{
                controlModul.Counter.Content++

                uiTrigger?.mem("")
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus()
                uiTrigger?.arrowZ()
                uiTrigger?.arrowAcc(true)
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

                uiTrigger?.centerBus()
                uiTrigger?.arrowIr(false)
                uiTrigger?.arrowAcc(true)
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
                uiTrigger?.centerBus()
                uiTrigger?.arrowIr(false)
                uiTrigger?.arrowIar(true)
                controlModul.Counter.Content++
            }
            11 -> {
                controlModul.Counter.Content = 0
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
                    uiTrigger?.centerBus()
                    uiTrigger?.arrowIr(false)
                    uiTrigger?.arrowIar(true)
                    controlModul.Counter.Content++
                }
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

                uiTrigger?.centerBus()
                uiTrigger?.arrowAcc(false)
                uiTrigger?.arrowX()
            }
            9 -> {
                centerBus.transfer(controlModul.IR, calculatorModul.Y)
                calculatorModul.Alu.shift()
                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowIr(false)
                uiTrigger?.arrowY()
                uiTrigger?.alu("SHIFT")
            }
            10 -> {
                controlModul.Counter.Content++

                uiTrigger?.highlightRegister( true,"Z")
            }
            11 -> {
                uiTrigger?.highlightRegister( false,"Z")

                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus()
                uiTrigger?.arrowZ()
                uiTrigger?.arrowAcc(true)
            }
            else -> controlModul.Counter.Content++
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

                uiTrigger?.centerBus()
                uiTrigger?.arrowAcc(false)
                uiTrigger?.arrowX()
            }
            9 -> {
                calculatorModul.Alu.negate()
                controlModul.Counter.Content++

                uiTrigger?.alu("NOT")
            }
            10 -> {
                controlModul.Counter.Content++
                uiTrigger?.highlightRegister(true, "Z")
            }
            11 -> {
                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus()
                uiTrigger?.arrowZ()
                uiTrigger?.arrowAcc(true)
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

                uiTrigger?.centerBus()
                uiTrigger?.arrowAcc(false)
                uiTrigger?.arrowX()
            }

            9 -> {
                centerBus.transfer(calculatorModul.ONE, calculatorModul.Y)
                calculatorModul.Alu.shift()
                controlModul.Counter.Content++

                uiTrigger?.centerBus()
                uiTrigger?.arrowOne()
                uiTrigger?.arrowY()
                uiTrigger?.alu("SHIFT")
            }
            10 -> {
                controlModul.Counter.Content++
                uiTrigger?.highlightRegister(true, "Z")
            }
            11 -> {
                uiTrigger?.highlightRegister(false, "Z")

                centerBus.transfer(calculatorModul.Z, calculatorModul.ACC)
                controlModul.Counter.Content = 0

                uiTrigger?.centerBus()
                uiTrigger?.arrowZ()
                uiTrigger?.arrowAcc(true)
            }
            else -> {
                controlModul.Counter.Content++
            }
        }

    }

}