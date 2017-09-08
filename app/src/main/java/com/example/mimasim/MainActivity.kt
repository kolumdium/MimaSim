package com.example.mimasim

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity(), MimaFragment.elementSelectedListener  {

    var mimaFragment = MimaFragment()
    var optionsFragment = OptionFragment()
    var instructionFragment = InstructionFragment()
    var optionPreviewFragment = OptionPreviewFragment()
    var instructionPreviewFragment = InstructionPreviewFragment()
    var MimaModul : MimaModul? = null

    var leftView : View? = null
    var rightView : View? = null
    var centerView : View? = null
    /* a variable to keep track of what is extended
    * 0 = normal
    * 1 = right extended
    * 2 = left extended*/
    var extended = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MimaModul = MimaModul(resources.getString(R.string.MimaModul), resources.getString(R.string.MimaModulDescription), applicationContext)

        init()

        //drawArrows()
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
        transaction.add(R.id.centerView, mimaFragment, "FragmentTagMima")
        transaction.add(R.id.rightView, optionPreviewFragment, "FragmentTagOptionsPreview")
        transaction.add(R.id.rightView, optionsFragment , "FragmentTagOptions")
        transaction.add(R.id.leftView, instructionFragment, "FragmentTagInstruction")
        transaction.hide(optionsFragment)
        transaction.hide(instructionFragment)
        transaction.commit()

    }

    fun setListener(){
        /*Swipe Listener to extend/close the right and left View*/
        findViewById(R.id.baseLayout)?.setOnTouchListener(object : OnSwipeTouchListener(applicationContext){
            override fun onSwipeRight() {
                if (extended == 0) {
                    extendLeft()
                } else if (extended == 1) {
                    extendNormal()
                }
            }
            override fun onSwipeLeft() {
                if (extended == 0) {
                    extendRight()
                } else if (extended == 2) {
                    extendNormal()
                }
            }
        })
    }

    fun extendNormal(){
        resize(1f, 1f)
        val transaction = fragmentManager.beginTransaction()
        transaction.hide(optionsFragment)
        transaction.hide(instructionFragment)
        transaction.show(instructionPreviewFragment)
        transaction.show(optionPreviewFragment)
        transaction.commit()
        extended = 0;
    }

    fun extendRight() {
        resize(0f, 5f)
        val transaction = fragmentManager.beginTransaction()
        transaction.show(optionsFragment)
        transaction.hide(optionPreviewFragment)
        transaction.show(instructionPreviewFragment)
        transaction.commit()
        extended = 1;

    }

    fun extendLeft(){
        resize(5f,  0f)
        val transaction = fragmentManager.beginTransaction()
        transaction.show(instructionFragment)
        transaction.hide(instructionPreviewFragment)
        transaction.commit()
        extended = 2;
    }

    fun resize(leftSize : Float, rightSize : Float){
        val lparamsE= rightView?.layoutParams as LinearLayout.LayoutParams
        lparamsE.weight = rightSize
        rightView?.layoutParams = lparamsE;

        val lparamsC= leftView?.layoutParams as LinearLayout.LayoutParams
        lparamsC.weight = leftSize
        leftView?.layoutParams = lparamsC;
    }

    fun openOptions() :Boolean {
        /* Opens the Option Menu when triggered*/
        if (extended == 0) {
            extendRight()
        } else if (extended == 2) {
            extendNormal()
            extendRight()
        }
        return true
    }

    override fun sendElement(currentlyLoadedElement: Element, hasContent: Boolean) {
        /*When an Element is Long hold (wants to be edited) this gets Called*/
        openOptions()

        /* Let Options know which Element there is to Edit*/
        optionsFragment.updateView(currentlyLoadedElement, hasContent)

    }

    fun drawArrows(){
        /*drawLeftToRightArrows()
        drawRightToLeftArrows()
        drawRightAndLeftArrows()
        drawBottomUpArrows()
        drawTopDownArrows()*/
        drawUpAndDownArrows()
    }

    private fun drawTopDownArrows() {

    }

    private fun drawBottomUpArrows() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawRightAndLeftArrows() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawRightToLeftArrows() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawLeftToRightArrows() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawUpAndDownArrows() {
        val view = findViewById(R.id.arrowFromSIRToIOBus)
        view?.setBackgroundColor(Color.BLACK)
        view?.invalidate()
    }


}
