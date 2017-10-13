package com.example.mimasim

import android.app.Fragment
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

class MainActivity : AppCompatActivity(), MimaFragment.MimaFragmentCallback, InstructionFragment.InstructionButtonClickedCallback , InformationFragment.informationSaveButtonClickedCallback, InformationPreviewFragment.InformationPreviewCallback, InstructionPreviewFragment.InstructionPreviewCallback, OptionsFragment.OptionsCallback{

    var mimaFragment = MimaFragment()
    var informationFragment = InformationFragment()
    var instructionFragment = InstructionFragment()
    var informationPreviewFragment = InformationPreviewFragment()
    var instructionPreviewFragment = InstructionPreviewFragment()
    var optionsFragment = OptionsFragment()
    var mimaModul : MimaModul? = null

    var leftView : View? = null
    var rightView : View? = null
    var centerView : View? = null

    var leftFragment : Fragment? = null
    var rightFragment : Fragment? = null
    var leftPreview : Fragment? = null
    var rightPreview : Fragment? = null

    var speed : Long = 0
    var minSpeed : Long = 1000
    var timerHandler : Handler? = null
    var timerRunnable = object : Runnable{
        override fun run() {
            mimaModul?.step()
            updateMima()
            timerHandler?.postDelayed(this, speed)
        }
    }

    enum class Extended{
        NORMAL, RIGHT, LEFT , RIGHTFULL, LEFTFULL, Options
    }

    var extended = Extended.NORMAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        leftFragment = instructionFragment
        rightFragment = informationFragment
        rightPreview = informationPreviewFragment
        leftPreview = instructionPreviewFragment

        timerHandler = Handler()

        /*Get an instance of the simulator*/
        mimaModul = MimaModul(resources.getString(R.string.MimaModul), resources.getString(R.string.MimaModulDescription), this, mimaFragment, instructionFragment)
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
        transaction.add(R.id.rightView, informationPreviewFragment, "FragmentTagInformationPreview")
        transaction.add(R.id.rightView, informationFragment, "FragmentTagInformation")
        transaction.add(R.id.centerView, optionsFragment, "FragmentTagOptions")
        transaction.hide(informationFragment)
        transaction.hide(instructionFragment)
        transaction.hide(optionsFragment)
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
                    MainActivity.Extended.LEFT -> extendFullscreen(leftFragment!!)
                    MainActivity.Extended.RIGHTFULL -> extendRight()
                    MainActivity.Extended.LEFTFULL -> Log.d("SWIPETAG", "I am Full extended")
                }
            }
            override fun onSwipeLeft() {
                when(extended) {
                    MainActivity.Extended.NORMAL -> extendRight()
                    MainActivity.Extended.RIGHT -> extendFullscreen(rightFragment!!)
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

        transaction.hide(informationFragment)
        transaction.hide(instructionFragment)
        transaction.hide(optionsFragment)
        transaction.show(instructionPreviewFragment)
        transaction.show(informationPreviewFragment)
        transaction.show(mimaFragment)
        transaction.commit()

        extended = Extended.NORMAL
    }

    fun extendRight() {
        resize(0f, 10f ,5f)

        val transaction = fragmentManager.beginTransaction()
        transaction.show(rightFragment)
        transaction.show(mimaFragment)
        transaction.show(leftPreview)

        transaction.hide(rightPreview)
        transaction.commit()
        extended = Extended.RIGHT

        instructionFragment.makeSmallLayout()
    }

    fun extendLeft(){
        resize(5f, 10f ,0f)

        val transaction = fragmentManager.beginTransaction()
        transaction.show(leftFragment)
        transaction.show(mimaFragment)
        transaction.show(rightPreview)

        transaction.hide(leftPreview)
        transaction.commit()
        extended = Extended.LEFT

        instructionFragment.makeSmallLayout()
    }

    fun extendFullscreen(fragment: Fragment){
        val transaction = fragmentManager.beginTransaction()
        transaction.hide(mimaFragment)

        when (fragment) {
            rightFragment -> {
                resize(0f, 0f, 1f)
                extended = Extended.RIGHTFULL
            }
            leftFragment -> {
                resize(1f, 0f, 0f)
                extended = Extended.LEFTFULL
            }
            optionsFragment-> {
                resize(0f,1f,0f)
                extended = Extended.Options
                transaction.show(optionsFragment)
            }
        }

        instructionFragment.makeBigLayout()

        transaction.commit()

        stopMima()
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

    fun openInformation(){
        /* Opens the Information Menu when triggered*/
        if (informationFragment == rightFragment) {
            if (extended == Extended.NORMAL)
                extendRight()
        } else {
            if (extended == Extended.NORMAL)
                extendLeft()
        }
    }

    fun openOptions(){
        extendFullscreen(optionsFragment)
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
    * InformationCallbacks
    * */

    override fun abortInformations() {
        extendNormal()
    }

    override fun updateMima() {
        mimaFragment.updateRegisters()
    }

    override fun openOptionsClicked(){
        openOptions()
    }

    /*
    * OptionsCallback
    * */

    override fun saveOptions() {
        //TODO Save Options
        extendNormal()
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
        openInformation()

        /* Let Options know which Element there is to Edit*/
        informationFragment.updateView(currentlyLoadedElement)
    }

    /* StepControl */
    override fun startButtonPressed() {
        mimaModul?.speedChanged(speed)
        startMima()
    }

    override fun stopButtonPressed() {
        stopMima()
        mimaModul?.speedChanged(1000)
    }

    override fun stepButtonPressed() {
        mimaModul?.step()
        updateMima()
    }

    override fun speedChanged(speed: Long) {
        /*speed from 0 to 1000*/
        if (speed >= 0.toLong())
            this.speed = minSpeed - speed
        else
            this.speed = 0.toLong()
        mimaModul?.speedChanged(this.speed)
    }

    /*
    * Preview Callbacks
    * */

    override fun informationPreviewClicked() {
        openInformation()
    }

    override fun instructionPreviewClicked() {
        extendLeft()
    }
}
