package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mimasim.MainActivity
import com.example.mimasim.R
import com.example.mimasim.Simulator.Element

/**
 * Created by Martin on 04.09.2017.
 */
class MimaFragment : Fragment() {

    var currentlyLoadedElement = Element(" ", "Long Click an Element to see the Options for it")
    var mCallback : elementSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.mima, container , false)
    }

    interface elementSelectedListener{
        fun sendElement(currentlyLoadedElement : Element, hasContent : Boolean)
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
        val map = mutableMapOf<Int, Element>()
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

        for ((key,value) in map) {
            val someView = view?.findViewById(key)
            currentlyLoadedElement = value
            someView?.isLongClickable = true
            someView?.setOnLongClickListener {
                if (key == R.id.ViewIOBus || key == R.id.centerBus || key == R.id.IOControler){
                    mCallback?.sendElement(value, false)
                    main.openOptions()
                } else {
                    mCallback?.sendElement(value, true)
                    main.openOptions()
                }
            }
        }

        drawArrows()
        drawAlu()
    }

    fun drawAlu(){
        //TODO Draw ALU
        this.view.findViewById(R.id.viewALU).setBackgroundColor(Color.YELLOW)
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
        this.view.findViewById(R.id.arrowFromSARToIOBus).setBackgroundResource(R.drawable.icon_arrow_down_a)
        this.view.findViewById(R.id.arrowFromXToAlu).setBackgroundResource(R.drawable.icon_arrow_down_a)
        this.view.findViewById(R.id.arrowFromYToAlu).setBackgroundResource(R.drawable.icon_arrow_down_a)
        this.view.findViewById(R.id.arrowFromAluToZ).setBackgroundResource(R.drawable.icon_arrow_down_a)
        this.view.findViewById(R.id.arrowFromSARToIOControler).setBackgroundResource(R.drawable.icon_arrow_down_a)

    }

    private fun drawBottomUpArrows() {
        /*SAR --> Memory*/
        this.view.findViewById(R.id.arrowFromSARToMemory).setBackgroundResource(R.drawable.icon_arrow_up_a)
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
        this.view.findViewById(R.id.arrowFromXToBus).setBackgroundResource(R.drawable.icon_arrow_left_a)
        this.view.findViewById(R.id.arrowFromYToBus).setBackgroundResource(R.drawable.icon_arrow_left_a)
    }

    private fun drawLeftToRightArrows() {
        /*ONE -> cBus
        * cBus ->SAR
        * SAR -> IO/Control*/

        this.view.findViewById(R.id.arrowFromONEToBus).setBackgroundResource(R.drawable.icon_arrow_right_a)
        this.view.findViewById(R.id.arrowFromSARToCenterBus).setBackgroundResource(R.drawable.icon_arrow_right_a)
        this.view.findViewById(R.id.arrowFromZToBus).setBackgroundResource(R.drawable.icon_arrow_right_a)
    }

    private fun drawUpAndDownArrows() {
        /* SIR->MEMORY
        * SIR -> IOBUS
        * cBus
        * */
        this.view.findViewById(R.id.arrowFromSIRToMemory).setBackgroundResource(R.drawable.icon_arrow_updown)
        this.view.findViewById(R.id.arrowFromSIRToIOBus).setBackgroundResource(R.drawable.icon_arrow_updown)
        this.view.findViewById(R.id.centerBus).setBackgroundResource(R.drawable.icon_arrow_updown)
        //view?.invalidate()
    }

}