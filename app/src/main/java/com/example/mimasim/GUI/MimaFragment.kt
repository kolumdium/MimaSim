package com.example.mimasim.GUI

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.example.mimasim.MainActivity
import com.example.mimasim.R
import com.example.mimasim.Simulator.Element
import com.example.mimasim.Simulator.MemoryModul
import com.example.mimasim.Simulator.MimaModul
import com.example.mimasim.Simulator.Register


/**
 * Created by Martin on 04.09.2017.
 */
class MimaFragment : Fragment(), MimaModul.UITrigger , MemoryModul.ExternalIOTrigger{

    var currentlyLoadedElement = Element(" ", "Long Click an Element to see the Informations for it")
    var mimaFragmentCallback: MimaFragmentCallback? = null
    var optionsState = OptionsState()

    private val clickableElementsMap = mutableMapOf<Int, Element>()
    private val registerMap = mutableMapOf<Int, Register>()

    interface MimaFragmentCallback {
        fun sendElement(currentlyLoadedElement : Element)
        fun startButtonPressed()
        fun stopButtonPressed()
        fun stepButtonPressed()
        fun speedChanged(speed : Long)
        fun readExternal()
        fun readExternalDone(text: Char)
        fun makeToast(text: String)
        fun writeExternal()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.mima, container , false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mimaFragmentCallback = context as MimaFragmentCallback
        } catch (e : ClassCastException){
            throw ClassCastException(activity.toString() + " must implement MimaFragmentCallback")
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val main = activity as MainActivity

        getBundle()
        getRegister(main)
        getClickableElements(main)
        setBackgrounds()
        setOnClickListeners()
        initSeekBar()
        drawArrows()
        updateRegisters()
        drawAlu()
        prepareIO()
    }

    private fun getBundle(){
        val bundle = this.arguments
        optionsState.fillZeroes = bundle.getBoolean("fillZeroes", true)
        optionsState.maxDelay = bundle.getInt("maxDelay", 1000)
    }

    private fun getRegister(mainActivity: MainActivity){
        registerMap.put(R.id.registerONE, mainActivity.mimaModul!!.calculatorModul.ONE)
        registerMap.put(R.id.registerACC, mainActivity.mimaModul!!.calculatorModul.ACC)
        registerMap.put(R.id.registerX, mainActivity.mimaModul!!.calculatorModul.X)
        registerMap.put(R.id.registerY, mainActivity.mimaModul!!.calculatorModul.Y)
        registerMap.put(R.id.registerZ, mainActivity.mimaModul!!.calculatorModul.Z)
        registerMap.put(R.id.registerIAR, mainActivity.mimaModul!!.controlModul.IAR)
        registerMap.put(R.id.registerIR, mainActivity.mimaModul!!.controlModul.IR)
        registerMap.put(R.id.registerSIR, mainActivity.mimaModul!!.memoryModul.SIR)
        registerMap.put(R.id.registerCounter, mainActivity.mimaModul!!.controlModul.Counter)
        registerMap.put(R.id.registerSAR, mainActivity.mimaModul!!.memoryModul.SAR)
    }

    private fun getClickableElements(mainActivity: MainActivity){
        /*A clickableElementsMap of all Clickable Items*/

        clickableElementsMap.putAll(registerMap)
        clickableElementsMap.put(R.id.IOControler, mainActivity.mimaModul!!.memoryModul.IOControl)
        clickableElementsMap.put(R.id.centerBus, mainActivity.mimaModul!!.centerBus)
        clickableElementsMap.put(R.id.ViewIOBus, mainActivity.mimaModul!!.IOBus)
        clickableElementsMap.put(R.id.viewALU, mainActivity.mimaModul!!.calculatorModul.Alu)
        clickableElementsMap.put(R.id.viewMemory, mainActivity.mimaModul!!.memoryModul.memory)
        clickableElementsMap.put(R.id.memoryModul, mainActivity.mimaModul!!.memoryModul)
        clickableElementsMap.put(R.id.calculatorModul, mainActivity.mimaModul!!.calculatorModul)
        clickableElementsMap.put(R.id.controlModul, mainActivity.mimaModul!!.controlModul)
    }

    private fun setBackgrounds(){
        for ((key, _) in clickableElementsMap) {
            val someView = view?.findViewById<View>(key)
            when (key) {
                R.id.memoryModul, R.id.calculatorModul, R.id.controlModul -> someView?.setBackgroundResource(R.drawable.kasten)
                else -> someView?.setBackgroundColor(resources.getColor(R.color.primary50))
            }
        }
    }

    private fun setOnClickListeners(){

        for ((key,value) in clickableElementsMap) {
            val someView = view?.findViewById<View>(key)
            currentlyLoadedElement = value
            someView?.isLongClickable = true
            someView?.setOnLongClickListener {
                mimaFragmentCallback?.sendElement(value)
                true
            }
        }

        val startButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStartButton)
        val stepButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStepButton)
        val stopButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStopButton)

        startButton?.setOnClickListener{
            mimaFragmentCallback?.startButtonPressed()
            stepButton?.isClickable = false
            stepButton?.visibility = View.INVISIBLE
            startButton.isClickable = false
            startButton.visibility = View.INVISIBLE

        }
        stepButton?.setOnClickListener{
            mimaFragmentCallback?.stepButtonPressed()
        }
        stopButton?.setOnClickListener{
            stepButton?.isClickable = true
            startButton?.isClickable = true
            startButton?.visibility = View.VISIBLE
            stepButton?.visibility = View.VISIBLE
            mimaFragmentCallback?.stopButtonPressed()
        }

    }

    private  fun initSeekBar(){
        val seekBar = view?.findViewById<SeekBar>(R.id.viewSpeed)

        seekBar?.max = optionsState.maxDelay
        seekBar?.progress = seekBar?.max!!.div(2)
        mimaFragmentCallback?.speedChanged( seekBar.progress.toLong())
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mimaFragmentCallback?.speedChanged( seekBar.progress.toLong())
            }
        })
    }

    fun updateRegisters(){
        for ((key,value) in registerMap) {
            when (value.name){
                "Counter" ->{
                    val counter = (view?.findViewById<TextView>(key))
                    val content = value.Content
                    /*Some Code to make the Register Content same length when displayed aka filling empty stuff with 0*/
                    var fillZeroes = ""
                    for (i in 0..(3 - Integer.toBinaryString(content).length)) {
                        fillZeroes += "0"
                    }
                    counter?.text = String.format(fillZeroes + Integer.toBinaryString (content))
                }
                else -> {
                    val someTextView = view?.findViewById<TextView>(key)
                    var fillZeros = ""
                    if (optionsState.fillZeroes)
                        for ( i in 0.. (7 - Integer.toHexString(value.Content).length)){
                            fillZeros += "0"
                        }
                    someTextView?.text = String.format(fillZeros + Integer.toHexString(value.Content))
                    someTextView?.bringToFront()
                    view.findViewById<View>(R.id.aluText).bringToFront()
               }
            }
        }
    }

    fun hideButtons(){
        val stepCotrol = view?.findViewById<ConstraintLayout>(R.id.StepControl)
        stepCotrol?.visibility = View.GONE
    }

    fun showButtons(){
        val stepCotrol = view?.findViewById<ConstraintLayout>(R.id.StepControl)
        stepCotrol?.visibility = View.VISIBLE

        val startButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStartButton)
        val stepButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStepButton)
        val stopButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStopButton)

        stepButton?.isClickable = true
        startButton?.isClickable = true
        stopButton?.isClickable = true
        startButton?.visibility = View.VISIBLE
        stepButton?.visibility = View.VISIBLE
        stopButton?.visibility = View.VISIBLE
    }

    private fun drawAlu(){
        this.view.findViewById<View>(R.id.viewALU).setBackgroundResource(R.drawable.alu)
    }

    private fun drawArrows(){
        drawLeftToRightArrows()
        drawRightToLeftArrows()
        drawRightAndLeftArrows()
        drawBottomUpArrows()
        drawTopDownArrows()
        drawUpAndDownArrows()
    }

    private fun prepareIO(){
        hideInput()
        val importView = view.findViewById<EditText>(R.id.ImportView)
        importView.setText("")

        importView.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                hideInput()
                showButtons()
                if (importView.text.toString() != "") {
                    mimaFragmentCallback?.readExternalDone(importView.text.toSet().elementAt(0))
                    importView.setText("")
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        val exportView = view.findViewById<TextView>(R.id.ExportView)
        exportView.setOnLongClickListener{
            exportView.text = " "
            true
        }
        exportView.text = " "
    }

    fun hideInput(){
        val importView = view.findViewById<EditText>(R.id.ImportView)
        importView.isClickable = false
        importView.visibility = View.INVISIBLE

    }

    fun mimaStoped(){
        val startButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStartButton)
        startButton?.isClickable
        startButton?.visibility = View.VISIBLE
        val stepButton = view?.findViewById<FloatingActionButton>(R.id.stepControlStartButton)
        stepButton?.isClickable
        stepButton?.visibility = View.VISIBLE
    }


    override fun readExternal() {
        mimaFragmentCallback?.readExternal()
        hideButtons()
    }

    override fun writeExternal() {
        mimaFragmentCallback?.writeExternal()
    }

    override fun noDeviceFound() {
        mimaFragmentCallback?.makeToast("No Device with that Adress on IOBus found")
    }

    private fun drawTopDownArrows() {
        /*SAR -> IOBus
        * X -> Alu
        * Y - > Alu
        * Alu -> Z*/
        this.view.findViewById<View>(R.id.arrowFromSARToIOBus).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById<View>(R.id.arrowFromXToAlu).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById<View>(R.id.arrowFromYToAlu).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById<View>(R.id.arrowFromAluToZ).setBackgroundResource(R.drawable.arrow_down)
        this.view.findViewById<View>(R.id.arrowFromSARToIOControler).setBackgroundResource(R.drawable.arrow_down)
    }

    private fun drawBottomUpArrows() {
        /*SAR --> Memory*/
        this.view.findViewById<View>(R.id.arrowFromSARToMemory).setBackgroundResource(R.drawable.arrow_up)
    }

    private fun drawRightAndLeftArrows() {
        /* ACC,IR,IAR,SIR -> cBus
        * IOBus*/
        this.view.findViewById<View>(R.id.arrowFromACCToBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById<View>(R.id.arrowFromIRToBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById<View>(R.id.arrowFromIARToBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById<View>(R.id.arrowFromSIRToCenterBus).setBackgroundResource(R.drawable.left_and_right_arrow)
        this.view.findViewById<View>(R.id.ViewIOBus).setBackgroundResource(R.drawable.left_and_right_arrow)
    }

    private fun drawRightToLeftArrows() {
        /*cBus-> x,y*/
        this.view.findViewById<View>(R.id.arrowFromXToBus).setBackgroundResource(R.drawable.arrow_left)
        this.view.findViewById<View>(R.id.arrowFromYToBus).setBackgroundResource(R.drawable.arrow_left)
    }

    private fun drawLeftToRightArrows() {
        /*ONE -> cBus
        * cBus ->SAR
        * SAR -> IO/Control*/

        this.view.findViewById<View>(R.id.arrowFromONEToBus).setBackgroundResource(R.drawable.arrow_right)
        this.view.findViewById<View>(R.id.arrowFromSARToCenterBus).setBackgroundResource(R.drawable.arrow_right)
        this.view.findViewById<View>(R.id.arrowFromZToBus).setBackgroundResource(R.drawable.arrow_right)
    }

    private fun drawUpAndDownArrows() {
        /* SIR->MEMORY
        * SIR -> IOBUS
        * cBus
        * */
        this.view.findViewById<View>(R.id.arrowFromSIRToMemory).setBackgroundResource(R.drawable.arrow_up_down)
        this.view.findViewById<View>(R.id.arrowFromSIRToIOBus).setBackgroundResource(R.drawable.arrow_up_down)
        this.view.findViewById<View>(R.id.centerBus).setBackgroundResource(R.drawable.arrow_up_down)
        this.view.findViewById<View>(R.id.arrowFromIOToImport).setBackgroundResource(R.drawable.arrow_up_down)
        this.view.findViewById<View>(R.id.arrowFromIOToExport).setBackgroundResource(R.drawable.arrow_up_down)
    }

    /* UI Trigger*/

    override fun normal() {
        drawArrows()
        highlightRegister(false, "Z")
    }

    override fun centerBus() {
        view.findViewById<View>(R.id.centerBus).setBackgroundResource(R.drawable.arrow_up_down_active)
    }

    override fun highlightRegister(activate: Boolean, register: String){
        var tmpView : View? = null
        when(register){
            "IAR" -> tmpView = view.findViewById(R.id.registerIAR)
            "IR" -> tmpView = view.findViewById(R.id.registerIR)
            "SAR" -> tmpView = view.findViewById(R.id.registerSAR)
            "SIR" -> tmpView = view.findViewById(R.id.registerSIR)
            "X" -> tmpView = view.findViewById(R.id.registerX)
            "Y" -> tmpView = view.findViewById(R.id.registerY)
            "Z" -> tmpView = view.findViewById(R.id.registerZ)
            "ACC" -> tmpView = view.findViewById(R.id.registerACC)
            "ONE" -> tmpView = view.findViewById(R.id.registerONE)
            "Counter" -> tmpView = view.findViewById(R.id.registerCounter)
            else -> {
                Log.d("highlightRegister", "Didn't find a Register to Highlight")
            }
        }
        if (activate) tmpView?.setBackgroundColor(resources.getColor(R.color.green))
        else tmpView?.setBackgroundColor(resources.getColor(R.color.registerColor))
    }

    override fun alu(instruction : String) {
        view.findViewById<TextView>(R.id.aluText).text = instruction
    }

    override fun mem(instruction: String) {
        view.findViewById<TextView>(R.id.viewMemory).text = instruction
    }

    override fun ioBus() {
        view.findViewById<View>(R.id.ViewIOBus).setBackgroundResource(R.drawable.left_and_right_arrow_active)
    }

    override fun ioControl(state : String) {
        view.findViewById<TextView>(R.id.IOState).text = state
    }

    override fun arrowIr(ingoing : Boolean) {
        if (ingoing) view.findViewById<View>(R.id.arrowFromIRToBus).setBackgroundResource(R.drawable.arrow_right_active)
        else view.findViewById<View>(R.id.arrowFromIRToBus).setBackgroundResource(R.drawable.arrow_left_active)
    }

    override fun arrowIar(ingoing: Boolean) {
        if (ingoing) view.findViewById<View>(R.id.arrowFromIARToBus).setBackgroundResource(R.drawable.arrow_right_active)
        else view.findViewById<View>(R.id.arrowFromIARToBus).setBackgroundResource(R.drawable.arrow_left_active)
    }

    override fun arrowAcc(ingoing: Boolean) {
        if (ingoing) view.findViewById<View>(R.id.arrowFromACCToBus).setBackgroundResource(R.drawable.arrow_left_active)
        else view.findViewById<View>(R.id.arrowFromACCToBus).setBackgroundResource(R.drawable.arrow_right_active)
    }

    override fun arrowOne() {
        view.findViewById<View>(R.id.arrowFromONEToBus).setBackgroundResource(R.drawable.arrow_right_active)
    }

    override fun arrowX() {
        view.findViewById<View>(R.id.arrowFromXToBus).setBackgroundResource(R.drawable.arrow_left_active)
    }

    override fun arrowY() {
        view.findViewById<View>(R.id.arrowFromYToBus).setBackgroundResource(R.drawable.arrow_left_active)
    }

    override fun arrowZ() {
        view.findViewById<View>(R.id.arrowFromZToBus).setBackgroundResource(R.drawable.arrow_right_active)
    }

    override fun arrowsSarMem() {
        view.findViewById<View>(R.id.arrowFromSARToCenterBus).setBackgroundResource(R.drawable.arrow_right_active)
        view.findViewById<View>(R.id.arrowFromSARToMemory).setBackgroundResource(R.drawable.arrow_up_active)
        view.findViewById<View>(R.id.arrowFromSARToIOControler).setBackgroundResource(R.drawable.arrow_down_active)
    }

    override fun arrowsSarIO(){
        view.findViewById<View>(R.id.arrowFromSARToCenterBus).setBackgroundResource(R.drawable.arrow_right_active)
        view.findViewById<View>(R.id.arrowFromSARToIOBus).setBackgroundResource(R.drawable.arrow_down_active)
        view.findViewById<View>(R.id.arrowFromSARToIOControler).setBackgroundResource(R.drawable.arrow_down_active)

    }

    override fun arrowSirToMemory() {
        view.findViewById<View>(R.id.arrowFromSIRToMemory).setBackgroundResource(R.drawable.arrow_down_active)
    }

    override fun arrowSirToBus(ingoing: Boolean) {
        if (ingoing) view.findViewById<View>(R.id.arrowFromSIRToCenterBus).setBackgroundResource(R.drawable.arrow_right_active)
        else view.findViewById<View>(R.id.arrowFromSIRToCenterBus).setBackgroundResource(R.drawable.arrow_left_active)
    }

    override fun ioRead() {
        view.findViewById<TextView>(R.id.IOState).text = getText(R.string.ioRead)
    }

    override fun ioWrite() {
        view.findViewById<TextView>(R.id.IOState).text = getText(R.string.ioWrite)
    }

    override fun ioClear(){
        view.findViewById<TextView>(R.id.IOState).text = ""
    }

    override fun ioReadDone() {
        normal()
        view.findViewById<View>(R.id.arrowFromSIRToIOBus).setBackgroundResource(R.drawable.arrow_up_active)
    }

    override fun ioWriteDone(){
        normal()
        view.findViewById<View>(R.id.arrowFromSIRToIOBus).setBackgroundResource(R.drawable.arrow_down_active)
    }

}