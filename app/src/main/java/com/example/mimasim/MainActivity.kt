package com.example.mimasim

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText

import android.widget.LinearLayout
import com.example.mimasim.GUI.*
import com.example.mimasim.Simulator.Element
import com.example.mimasim.Simulator.Instruction
import com.example.mimasim.Simulator.MimaModul
import java.util.*
import android.widget.Toast



/*TODO: Credits for the Images:
* left-and-right-arrow -> <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>*/

class MainActivity : AppCompatActivity(), MimaFragment.MimaFragmentCallback, InstructionFragment.InstructionButtonClickedCallback , OptionFragment.optionSaveButtonClickedCallback, OptionPreviewFragment.OptionPreviewCallback, InstructionPreviewFragment.InstructionPreviewCallback{

    var mimaFragment = MimaFragment()
    var optionsFragment = OptionFragment()
    var instructionFragment = InstructionFragment()
    var optionPreviewFragment = OptionPreviewFragment()
    var instructionPreviewFragment = InstructionPreviewFragment()
    var mimaModul : MimaModul? = null

    var leftView : View? = null
    var rightView : View? = null
    var centerView : View? = null

    var speed : Long = 0
    var timerHandler : Handler? = null
    var timerRunnable = object : Runnable{
        override fun run() {
            mimaModul?.step()
            updateMima()
            timerHandler?.postDelayed(this, speed)
        }
    }

    enum class Extended{
        NORMAL, RIGHT, LEFT , RIGHTFULL, LEFTFULL
    }

    var extended = Extended.NORMAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerHandler = Handler()

        /*Get an instance of the simulator*/
        mimaModul = MimaModul(resources.getString(R.string.MimaModul), resources.getString(R.string.MimaModulDescription), this, mimaFragment)
        init()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        when (extended){
            MainActivity.Extended.LEFTFULL ->{extendLeft()}
            MainActivity.Extended.NORMAL -> {}
            MainActivity.Extended.RIGHT, MainActivity.Extended.LEFT -> extendNormal()
            MainActivity.Extended.RIGHTFULL -> {extendRight()}
        }
    }

    override fun onStart() {
        super.onStart()
        setListener()
    }

    fun init(){

        leftView = this.findViewById(R.id.leftView)
        rightView = this.findViewById(R.id.rightView)
        centerView = this.findViewById(R.id.centerView)

        /* Add all Fragments show/hide to default*/
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.leftView, instructionPreviewFragment , "FragmentTagInstructionPreview")
        transaction.add(R.id.leftView, instructionFragment, "FragmentTagInstruction")
        transaction.add(R.id.centerView, mimaFragment, "FragmentTagMima")
        transaction.add(R.id.rightView, optionPreviewFragment, "FragmentTagOptionsPreview")
        transaction.add(R.id.rightView, optionsFragment , "FragmentTagOptions")
        transaction.hide(optionsFragment)
        transaction.hide(instructionFragment)
        transaction.commit()

    }

    fun setListener(){
        /*
        * Swipe Listener to extend/close the right and left View
        * */
        val baseView = findViewById<LinearLayout>(R.id.baseLayout)
        baseView.setOnTouchListener(object : OnSwipeTouchListener(applicationContext){
            override fun onSwipeRight() {
                when(extended) {
                    MainActivity.Extended.NORMAL -> extendLeft()
                    MainActivity.Extended.RIGHT -> extendNormal()
                    MainActivity.Extended.LEFT -> extendLeftFullscreen()
                    MainActivity.Extended.RIGHTFULL -> extendRight()
                    MainActivity.Extended.LEFTFULL -> Log.d("SWIPETAG", "I am Full extended")
                }
            }
            override fun onSwipeLeft() {
                when(extended) {
                    MainActivity.Extended.NORMAL -> extendRight()
                    MainActivity.Extended.RIGHT -> extendRightFullscreen()
                    MainActivity.Extended.LEFT -> extendNormal()
                    MainActivity.Extended.RIGHTFULL -> Log.d("SWIPETAG", "I am Full extended")
                    MainActivity.Extended.LEFTFULL -> extendLeft()
                }
            }
        })

    }

    fun extendNormal(){
        resize(1f, 10f ,1f)
        val transaction = fragmentManager.beginTransaction()

        transaction.hide(optionsFragment)
        transaction.hide(instructionFragment)
        transaction.show(instructionPreviewFragment)
        transaction.show(optionPreviewFragment)
        transaction.show(mimaFragment)
        transaction.commit()

        extended = Extended.NORMAL
    }

    fun extendRight() {
        resize(0f, 10f ,5f)

        val transaction = fragmentManager.beginTransaction()
        transaction.show(optionsFragment)
        transaction.show(mimaFragment)
        transaction.show(instructionPreviewFragment)

        transaction.hide(optionPreviewFragment)
        transaction.commit()
        extended = Extended.RIGHT

    }

    fun extendLeft(){
        resize(5f, 10f ,0f)

        val transaction = fragmentManager.beginTransaction()
        transaction.show(instructionFragment)
        transaction.show(mimaFragment)
        transaction.show(optionPreviewFragment)

        transaction.hide(instructionPreviewFragment)
        transaction.commit()
        extended = Extended.LEFT

        instructionFragment.makeSmallLayout()
    }

    /*TODO HOLD MIMA when either of these gets triggered*/
    fun extendRightFullscreen(){
        resize(0f, 0f ,1f)

        val transaction = fragmentManager.beginTransaction()
        transaction.hide(mimaFragment)
        transaction.commit()

        extended = Extended.RIGHTFULL
    }

    fun extendLeftFullscreen(){
        resize(1f, 0f ,0f)

        val transaction = fragmentManager.beginTransaction()
        transaction.hide(mimaFragment)
        transaction.commit()

        extended = Extended.LEFTFULL
        instructionFragment.makeBigLayout()
    }

    fun resize(leftSize : Float, centerSize : Float, rightSize : Float){
        val lparamsR= rightView?.layoutParams as LinearLayout.LayoutParams
        lparamsR.weight = rightSize
        rightView?.layoutParams = lparamsR

        val lparamsC= centerView?.layoutParams as LinearLayout.LayoutParams
        lparamsC.weight = centerSize
        centerView?.layoutParams = lparamsC

        val lparamsL= leftView?.layoutParams as LinearLayout.LayoutParams
        lparamsL.weight = leftSize
        leftView?.layoutParams = lparamsL
    }

    fun openOptions(){
        /* Opens the Option Menu when triggered*/
        if (extended == Extended.NORMAL) {
            extendRight()
        } else if (extended == Extended.RIGHT) {
            extendNormal()
            extendRight()
        }
    }

    fun stopMima(){
        timerHandler?.removeCallbacks(timerRunnable)
    }

    fun startMima(){
        timerHandler?.postDelayed(timerRunnable, 0)
    }

    /*
    * InstructionCallbacks
    * */

    override fun saveInstructions(currentInstructions : ArrayList<Instruction>){
        this.mimaModul?.memoryModul?.saveToMemory(currentInstructions)
        extendNormal()
    }

    override fun clearMima() {
        mimaModul?.reset()
    }

    override fun closeInstructions() {
        extendNormal()
    }

    /*
    * OptionsCallbacks
    * */

    override fun abortOptions() {
        extendNormal()
    }

    override fun updateMima() {
        mimaFragment.updateRegisters()
    }

    /*
    * MimaFragmentCallbacks
    * */

    override fun readExternal() {
        stopMima()
        val importView = findViewById<EditText>(R.id.ImportView)
        importView.visibility = View.VISIBLE
        importView.isClickable

    }

    override fun readExternalDone() {
        startMima()
    }

    override fun makeToast(text: String) {
        val context = applicationContext
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }

    override fun sendElement(currentlyLoadedElement: Element) {
        /*When an Element is Long hold (wants to be edited) this gets Called*/
        openOptions()

        /* Let Options know which Element there is to Edit*/
        optionsFragment.updateView(currentlyLoadedElement)
    }

    /* StepControl */
    override fun startButtonPressed() {
        mimaModul?.speedChanged(speed)
        startMima()
    }

    override fun stopButtonPressed() {
        stopMima()
    }

    override fun stepButtonPressed() {
        mimaModul?.speedChanged(1000)
        mimaModul?.step()
        updateMima()
    }

    override fun speedChanged(speed: Long) {
        this.speed = speed
        mimaModul?.speedChanged(speed)
    }

    /*
    * Preview Callbacks
    * */

    override fun optionPreviewClicked() {
        openOptions()
    }

    override fun instructionPreviewClicked() {
        extendLeft()
    }
}
