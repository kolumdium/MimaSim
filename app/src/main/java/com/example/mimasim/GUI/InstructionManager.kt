package com.example.mimasim.GUI

import com.example.mimasim.Simulator.Instruction

/**
 * Created by Martin on 01.10.2017.
 */
class InstructionManager {
    var instructions = ArrayList<Instruction>()

    fun getAsString() : ArrayList<String>{
        val string = ArrayList<String>()

        instructions.mapTo(string) { "${instructions.indexOf(it)}: ${it.opCodeString} ${it.address} \n" }

        return string
    }

    fun loadDefaultInstructions(){
        instructions.clear()
        instructions.add(Instruction())
    }

    fun getAsCharSequence() : CharSequence{
        var string = ""

        for (instruction in instructions) {
            string += if (instruction.isInMima)
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

    fun clearInstructions(){
        instructions.clear()
        instructions.add(Instruction())
    }

    fun saveToFile(){

    }

    fun loadFromFile(){

    }
}