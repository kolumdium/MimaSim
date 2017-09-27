package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mimasim.MainActivity
import com.example.mimasim.R
import com.example.mimasim.Simulator.Element
import com.example.mimasim.Simulator.Register

/**
 * Created by Martin on 04.09.2017.
 */
class MimaFragment : Fragment() {

    var currentlyLoadedElement = Element(" ", "Long Click an Element to see the Options for it")
    var mCallback : elementSelectedListener? = null
    val map = mutableMapOf<Int, Element>()

    interface elementSelectedListener{
        fun sendElement(currentlyLoadedElement : Element)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.mima, container , false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as elementSelectedListener
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement elementSelectedListener")
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view?.setBackgroundColor(resources.getColor(R.color.lighterGrey) )

        val main = activity as MainActivity

        /*A map of all Clickable Items*/
        map.put(R.id.registerONE, main.MimaModul!!.calculatorModul.ONE)
        map.put(R.id.registerACC, main.MimaModul!!.calculatorModul.ACC)
        map.put(R.id.registerX, main.MimaModul!!.calculatorModul.X)
        map.put(R.id.registerY, main.MimaModul!!.calculatorModul.Y)
        map.put(R.id.registerZ, main.MimaModul!!.calculatorModul.Z)
        map.put(R.id.registerIAR, main.MimaModul!!.controlModul.IAR)
        map.put(R.id.registerIR, main.MimaModul!!.controlModul.IR)
        map.put(R.id.registerSIR, main.MimaModul!!.memoryModul.SIR)
        map.put(R.id.registerSAR, main.MimaModul!!.memoryModul.SAR)
        map.put(R.id.IOControler, main.MimaModul!!.memoryModul.IOControl)
        map.put(R.id.centerBus, main.MimaModul!!.centerBus)
        map.put(R.id.ViewIOBus, main.MimaModul!!.IOBus)
        map.put(R.id.viewALU, main.MimaModul!!.calculatorModul.Alu)
        map.put(R.id.viewMemory, main.MimaModul!!.memoryModul.Memory)
        map.put(R.id.memoryModul, main.MimaModul!!.memoryModul)
        map.put(R.id.calculatorModul, main.MimaModul!!.calculatorModul)
        map.put(R.id.controlModul, main.MimaModul!!.controlModul)

        for ((key,value) in map) {
            val someView = view?.findViewById(key)
            someView?.setBackgroundResource(R.drawable.kasten)
            currentlyLoadedElement = value
            someView?.isLongClickable = true
            someView?.setOnLongClickListener {
                mCallback?.sendElement(value)
                true
            }
        }

        drawArrows()
        updateView()
        setBackgrounds()
    }

    fun updateView(){
        for ((key,value) in map) {
            when (value.name){
                "ALU" , "I/O-Bus", "I/O-Control" , "Prozessorbus", "Mima", "Speicherwerk" , "Steuerwerk" , "Rechenwerk" -> {}
                "Memory" -> {//TODO some fancy stuff
                    }
                else -> {
                    //should be a Register when you get here.
                    val someTextView = (view?.findViewById(key) as TextView)
                    var fillZeros = ""
                    for ( i in 0.. (7 - (value as Register).Content.toString().length)){
                        fillZeros += "0"
                    }
                    someTextView.text = String.format("0x" + fillZeros + Integer.toHexString(value.Content))
               }
            }
        }
    }

    fun setBackgrounds(){
        this.view.findViewById(R.id.viewALU).setBackgroundResource(R.drawable.alu)

    }

    fun drawArrows(){
        drawLeftToRightArrows()
        drawRightToLeftArrows()
        drawRightAndLeftArrows()
        drawBottomUpArrows()
        drawTopDownArrows()
        drawUpAndDownArrows()
    }

    private fun drawTopDownArrows() {
        /*SAR -> IOBus
        * X -> Alu
        * Y - > Alu
        * Alu -> Z*/
        this.view.findViewById(R.id.arrowFromSARToIOBus).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById(R.id.arrowFromXToAlu).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById(R.id.arrowFromYToAlu).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById(R.id.arrowFromAluToZ).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById(R.id.arrowFromSARToIOControler).setBackgroundResource(R.drawable.arrow_down)

    }

    private fun drawBottomUpArrows() {
        /*SAR --> Memory*/
        this.view.findViewById(R.id.arrowFromSARToMemory).setBackgroundResource(R.drawable.arrow_up)
    }

    private fun drawRightAndLeftArrows() {
        /* ACC,IR,IAR,SIR -> cBus
        * IOBus*/
        this.view.findViewById(R.id.arrowFromACCToBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById(R.id.arrowFromIRToBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById(R.id.arrowFromIARToBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById(R.id.arrowFromSIRToCenterBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById(R.id.ViewIOBus).setBackgroundResource(R.drawable.left_and_right_arrow)
    }

    private fun drawRightToLeftArrows() {
        /*cBus-> x,y*/
        this.view.findViewById(R.id.arrowFromXToBus).setBackgroundResource(R.drawable.arrow_left)
        this.view.findViewById(R.id.arrowFromYToBus).setBackgroundResource(R.drawable.arrow_left)
    }

    private fun drawLeftToRightArrows() {
        /*ONE -> cBus
        * cBus ->SAR
        * SAR -> IO/Control*/

        this.view.findViewById(R.id.arrowFromONEToBus).setBackgroundResource(R.drawable.arrow_right)
        this.view.findViewById(R.id.arrowFromSARToCenterBus).setBackgroundResource(R.drawable.arrow_right)
        this.view.findViewById(R.id.arrowFromZToBus).setBackgroundResource(R.drawable.arrow_right)
    }

    private fun drawUpAndDownArrows() {
        /* SIR->MEMORY
        * SIR -> IOBUS
        * cBus
        * */
        this.view.findViewById(R.id.arrowFromSIRToMemory).setBackgroundResource(R.drawable.arrow_up_down)
        this.view.findViewById(R.id.arrowFromSIRToIOBus).setBackgroundResource(R.drawable.arrow_up_down)
        this.view.findViewById(R.id.centerBus).setBackgroundResource(R.drawable.arrow_up_down)
        //view?.invalidate()
    }

}