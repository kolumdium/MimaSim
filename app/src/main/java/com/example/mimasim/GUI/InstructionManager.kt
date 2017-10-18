package com.example.mimasim.GUI

import com.example.mimasim.Simulator.Instruction

/**
 * Created by Martin on 01.10.2017.
 */
class InstructionManager {
    var instructions = ArrayList<Instruction>()
    var currentlyLoadedInstruction = 0;
    val opCodes = ArrayList<String>()

    fun getAsString() : ArrayList<String>{
        val string = ArrayList<String>()

        instructions.mapTo(string) { "${instructions.indexOf(it)}: ${it.opCodeString} ${it.address} \n" }

        return string
    }

    init {
        opCodes.add("ADD")
        opCodes.add("AND")
        opCodes.add("OR")
        opCodes.add("XOR")
        opCodes.add("LDV")
        opCodes.add("STV")
        opCodes.add("LDC")
        opCodes.add("JMP")
        opCodes.add("JMN")
        opCodes.add("EQL")
        opCodes.add("RRN")
        opCodes.add("HLT")
        opCodes.add("NOT")
        opCodes.add("RAR")
    }

    fun loadDefaultInstructions(){
        instructions.clear()
       // instructions.add(Instruction(0, opCodes[0]))
        //instructions.add(Instruction(1, opCodes[1], address = 0x0000c002))
        instructions.add(Instruction(2, opCodes[2], address = 0xC000001))
        //instructions.add(Instruction(3, opCodes[3]))
       // instructions.add(Instruction(4, opCodes[6], address = 0x0000500))
        instructions.add(Instruction(5, opCodes[5], address = 0xC000002))
        instructions.add(Instruction(7, opCodes[7], address = 0x0000000))
        instructions.add(Instruction(7, opCodes[7]))
        instructions.add(Instruction(8, opCodes[8]))
        instructions.add(Instruction(9, opCodes[9]))
        instructions.add(Instruction(10, opCodes[10]))
        instructions.add(Instruction(11, opCodes[11]))
        instructions.add(Instruction(12, opCodes[12]))
        instructions.add(Instruction(13, opCodes[13]))
    }

    fun getAsCharSequence() : CharSequence{
        var string = ""

        for (instruction in instructions) {
            string += if (instructions.indexOf(instruction) == currentlyLoadedInstruction)
                ">> ${instructions.indexOf(instruction)}: ${instruction.opCodeString} 0x${Integer.toHexString(instruction.address)} ${System.getProperty("line.separator")}"
            else
                " ${instructions.indexOf(instruction)}: ${instruction.opCodeString} 0x${Integer.toHexString(instruction.address)} ${System.getProperty("line.separator")}"
        }
        return string
    }

    fun up(position : Int) : Int{
        return if (position - 1 in 0 .. instructions.lastIndex && instructions.size > 1) {
            instructions[position - 1].isActive = false
            instructions[position].isActive = true
            val tmpInstruction = instructions[position - 1]
            instructions.set(position-1, instructions[position])
            instructions.set(position, tmpInstruction)
            position - 1
        }
        else{
            position
        }

    }

    fun down(position : Int) : Int{
        return if (position + 1 in 0 .. instructions.lastIndex && instructions.size > 1) {
            instructions[position + 1].isActive = false
            instructions[position].isActive = true
            val tmpInstruction = instructions[position + 1]
            instructions.set(position + 1, instructions[position])
            instructions.set(position, tmpInstruction)
            position + 1
        }
        else{
            position
        }
    }

    fun first(position : Int) : Int{
        if (position in 0 .. instructions.lastIndex && instructions.size > 1){
            val tmpInstruction = instructions[position]
            instructions.set(position, instructions[0])
            instructions.set(0, tmpInstruction)
        }
        setActive(0)
        return 0
    }

    fun last(position : Int) : Int{
        if (position in 0 .. instructions.lastIndex && instructions.size > 1) {
            val tmpInstruction = instructions[position]
            instructions.removeAt(position)
            instructions.add(tmpInstruction)
        }
        return instructions.lastIndex
    }

    fun remove(position: Int) : Int{
        if (position in 0 .. instructions.lastIndex && instructions.size > 1) {
            instructions[position].isActive = false
            when (position) {
                0 -> instructions[1].isActive = true
                1 -> instructions[0].isActive = true
                else -> instructions[position - 1].isActive = true
            }

            instructions.removeAt(position)

            return if (position - 1 < 0)
                0
            else
                position - 1
        }
        else return 0
    }

    fun setActive(lastItem : Int){
        for (instr in instructions.indices)
            instructions[instr].isActive = instr == lastItem
    }

    fun jumpTo(item : Int){
        currentlyLoadedInstruction = item
    }

    fun clearInstructions(){
        instructions.clear()
        instructions.add(Instruction())
    }

    fun add(instr : Instruction){
        instr.opCodeString = opCodes[instr.opCode]
        this.instructions.add(instr)
    }

    fun saveToFile(){

    }

    fun loadFromFile(){

    }
}