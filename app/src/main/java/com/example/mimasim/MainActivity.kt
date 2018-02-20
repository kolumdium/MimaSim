package com.example.mimasim

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.mimasim.GUI.*
import com.example.mimasim.Simulator.Element
import com.example.mimasim.Simulator.MimaModul
import java.util.*


/*TODO: Credits for the Images:
* left-and-right-arrow -> <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>*/

class MainActivity :
        AppCompatActivity(),
        MimaFragment.MimaFragmentCallback,
        InstructionFragment.InstructionCallback,
        InformationFragment.InformationCallback,
        InformationPreviewFragment.InformationPreviewCallback,
        InstructionPreviewFragment.InstructionPreviewCallback,
        Filemanager.FilemanagerCallback,
        MimaModul.HaltMimaTrigger {
    var mimaFragment = MimaFragment()
    var informationFragment = InformationFragment()
    var instructionFragment = InstructionFragment()
    var informationPreviewFragment = InformationPreviewFragment()
    var instructionPreviewFragment = InstructionPreviewFragment()
    var optionFragment = OptionsFragment()
    var mimaModul : MimaModul? = null

    var leftView : View? = null
    var rightView : View? = null
    var centerView : View? = null

    var leftFragment : Fragment? = null
    var rightFragment : Fragment? = null
    var leftPreview : Fragment? = null
    var rightPreview : Fragment? = null

    var speed : Long = 0
    var isRunning : Boolean = false
    //this is for running after user input
    var wasRunning : Boolean = false

    var filemanger : Filemanager? = null

    //Can be any Integer. Is used for Requesting read and write permission at runtime
    private val PERMISSIONS_MULTIPLE_REQUEST = 1

    private val PREF_KEY_ZEROS = "pref_zeroes"
    private val PREF_KEY_VIEWS = "pref_switchViews"
    private val PREF_KEY_SPEED = "pref_invertSpeed"
    private val PREF_KEY_DELAY = "pref_maxDelay"

    var prefFillZeros = true
    var prefInvertSpeed = true
    var prefSwitchViews = false
    var prefMaxDelay = 1000

    var timerHandler : Handler? = null
    var timerRunnable = object : Runnable{
        override fun run() {
            if (!isRunning)
                return
            mimaModul?.step()
            updateMima()
            timerHandler?.postDelayed(this, speed)
        }
    }

    enum class Extended{
        NORMAL, RIGHT, LEFT , RIGHTFULL, LEFTFULL, OPTIONS
    }

    var extended = Extended.NORMAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true)
        permissions()
        readPref()
        init()
    }

    override fun onResume() {
        super.onResume()
        readPref()
        mimaFragment.prefMaxDelay = prefMaxDelay
        mimaFragment.prefFillZeros = prefFillZeros
    }

    override fun onStart() {
        super.onStart()
        setListener()
        //load the last programm
        filemanger?.load("latestProgram.txt")
    }

    override fun onPause() {
        filemanger?.save("latestProgram", instructionFragment.instructionManager.getStringForSaving())
        super.onPause()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        when (extended){
            MainActivity.Extended.LEFTFULL ->{extendLeft()}
            MainActivity.Extended.NORMAL -> {}
            MainActivity.Extended.RIGHT, MainActivity.Extended.LEFT -> extendNormal()
            MainActivity.Extended.RIGHTFULL -> {extendRight()}
            MainActivity.Extended.OPTIONS -> {
                closeOptions()
                extendNormal()
            }
        }
    }

    private fun init(){
        filemanger = Filemanager(this)
        setViews()
        timerHandler = Handler()
        /*Get an instance of the simulator*/
        mimaModul = MimaModul(resources.getString(R.string.mimaModulName), resources.getString(R.string.mimaModulShort) , resources.getString(R.string.mimaModulDescription), this, mimaFragment, instructionFragment)
        setFragmnents()
    }

    private fun setViews(){
        if (prefSwitchViews){
            rightFragment = instructionFragment
            leftFragment = informationFragment
            leftPreview = informationPreviewFragment
            rightPreview = instructionPreviewFragment
        }
        else {
            leftFragment = instructionFragment
            rightFragment = informationFragment
            rightPreview = informationPreviewFragment
            leftPreview = instructionPreviewFragment
        }
    }

    fun setFragmnents(){
        centerView = findViewById(R.id.centerView)
        rightView = findViewById(R.id.rightView)
        leftView = findViewById(R.id.leftView)

        /* Add all Fragments show/hide to default*/
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.leftView, leftPreview , "FragmentTagInstructionPreview")
        transaction.add(R.id.leftView, leftFragment, "FragmentTagInstruction")
        transaction.add(R.id.centerView, mimaFragment, "FragmentTagMima")
        transaction.add(R.id.rightView, rightPreview, "FragmentTagInformationPreview")
        transaction.add(R.id.rightView, rightFragment, "FragmentTagInformation")
        transaction.add(R.id.centerView, optionFragment, "OptionsFragmentTag")

        transaction.hide(rightFragment)
        transaction.hide(leftFragment)
        transaction.hide(optionFragment)
        transaction.commit()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener(){
        /*
        * Swipe Listener to extend/close the right and left View
        * */
        val baseView = findViewById<LinearLayout>(R.id.baseLayout)
        baseView.setOnTouchListener(object : OnSwipeTouchListener(applicationContext){
            override fun onSwipeRight() {
                swipeRight()
            }
            override fun onSwipeLeft() {
                swipeLeft()
            }
        })
    }

    private fun permissions(){
        val hasPermission =
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                        ContextCompat.checkSelfPermission(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)      == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_MULTIPLE_REQUEST);
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_MULTIPLE_REQUEST ->{
                if (grantResults.size > 0   && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)  {
                    //We can now save and load Programs
                } else {
                    Toast.makeText(parent, "The app was not allowed to read or wirte to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun readPref(){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        prefFillZeros = sharedPref.getBoolean( PREF_KEY_ZEROS, true)
        prefInvertSpeed = sharedPref.getBoolean( PREF_KEY_SPEED, true)
        prefSwitchViews = sharedPref.getBoolean( PREF_KEY_VIEWS, false)
        prefMaxDelay = sharedPref.getInt(PREF_KEY_DELAY, 1000);
    }

    /*
    * Visual Managment
    * */

    fun swipeRight(){
        when(this.extended) {
            MainActivity.Extended.NORMAL -> extendLeft()
            MainActivity.Extended.RIGHT -> extendNormal()
            MainActivity.Extended.LEFT -> extendFullscreen(leftFragment!!)
            MainActivity.Extended.RIGHTFULL -> extendRight()
            MainActivity.Extended.LEFTFULL -> Log.d("SWIPETAG", "I am Full extended")
            else -> {
            }
        }
    }

    fun swipeLeft(){
        when(extended) {
            MainActivity.Extended.NORMAL -> extendRight()
            MainActivity.Extended.RIGHT -> extendFullscreen(rightFragment!!)
            MainActivity.Extended.LEFT -> extendNormal()
            MainActivity.Extended.RIGHTFULL -> Log.d("SWIPETAG", "I am Full extended")
            MainActivity.Extended.LEFTFULL -> extendLeft()
            else -> {
            }
        }
    }

    private fun extendNormal(){

        val transaction = fragmentManager.beginTransaction()
        transaction.hide(informationFragment)
        transaction.hide(instructionFragment)
        transaction.show(instructionPreviewFragment)
        transaction.show(informationPreviewFragment)
        transaction.show(mimaFragment)
        transaction.commit()

        //resize after Transaction makes smoother transition
        resize(1f, 10f ,1f)

        //For some Reason this prevents those UI Bugs
        if (extended != Extended.LEFT && extended != Extended.RIGHT){
            val transactionSmooth = fragmentManager.beginTransaction()
            transactionSmooth.detach(mimaFragment)
            transactionSmooth.attach(mimaFragment)
            transactionSmooth.commit()
        }

        extended = Extended.NORMAL
    }

    private fun extendRight() {

        val transaction = fragmentManager.beginTransaction()
        transaction.show(rightFragment)
        transaction.show(mimaFragment)
        transaction.show(leftPreview)
        transaction.hide(rightPreview)
        transaction.commit()
        //resize after Transaction makes smoother transition
        resize(0f, 10f ,5f)
        extended = Extended.RIGHT

        instructionFragment.makeSmallLayout()
    }

    private fun extendLeft(){

        val transaction = fragmentManager.beginTransaction()
        transaction.show(leftFragment)
        transaction.show(mimaFragment)
        transaction.show(rightPreview)
        transaction.hide(leftPreview)
        transaction.commit()
        //resize after Transaction makes smoother transition
        resize(5f, 10f ,0f)
        extended = Extended.LEFT

        instructionFragment.makeSmallLayout()
    }

    fun extendFullscreen(fragment: Fragment){
        val transaction = fragmentManager.beginTransaction()
        transaction.hide(mimaFragment)
        transaction.commit()

        when (fragment) {
            rightFragment -> {
                resize(0f, 0f, 1f)
                extended = Extended.RIGHTFULL
            }
            leftFragment -> {
                resize(1f, 0f, 0f)
                extended = Extended.LEFTFULL
            }
            else -> {
                resize(0f,1f,0f)
                extended = Extended.OPTIONS
            }
        }

        instructionFragment.makeBigLayout()

        stopMima()
    }

    private fun resize(leftSize : Float, centerSize : Float, rightSize : Float){
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

    private fun openInformation(){
        /* Opens the Information Menu when triggered*/
        if (informationFragment == rightFragment) {
            if (extended == Extended.NORMAL)
                extendRight()
        } else {
            if (extended == Extended.NORMAL)
                extendLeft()
        }
    }

    private fun openInstruction(){
        if (instructionFragment == leftFragment) {
            if (extended == Extended.NORMAL)
                extendLeft()
        } else {
            if (extended == Extended.NORMAL)
                extendRight()
        }
    }

    private fun openOptions(){
        stopMima()

        val transaction = fragmentManager.beginTransaction()
        transaction.hide(mimaFragment)
        transaction.show(optionFragment)
        transaction.commit()

        extendFullscreen(optionFragment)
    }

    private fun closeOptions(){
        readPref()
        mimaFragment.prefMaxDelay = prefMaxDelay
        mimaFragment.prefFillZeros = prefFillZeros

        val transaction = fragmentManager.beginTransaction()
        transaction.show(mimaFragment)
        transaction.hide(optionFragment)
        transaction.commit()

        replaceFragments()
    }

    private fun replaceFragments(){
        setViews()
        reAttach(rightFragment, rightView as ViewGroup)
        reAttach(rightPreview, rightView as ViewGroup)
        reAttach(leftFragment, leftView as ViewGroup)
        reAttach(leftPreview, leftView as ViewGroup)
    }

    private fun reAttach(fragment: Fragment?, newparent : ViewGroup){
        val vv = fragment?.view
        val parent : ViewGroup = vv?.parent as ViewGroup
        parent.removeView(vv)
        newparent.addView(vv)
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = findViewById<EditText>(R.id.importView)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(activity: Activity){
        val view = findViewById<EditText>(R.id.importView)
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0);
    }

    /*
    * MimaControl
    * */

    private fun stopMima(){
        isRunning = false
        mimaModul?.speedChanged(1000)
        timerHandler?.removeCallbacks(timerRunnable)
        mimaFragment.mimaStoped()
    }

    private fun startMima(){
        isRunning = true
        timerHandler?.postDelayed(timerRunnable, 0)
    }

    /*
    * Save and Load
    * */

    override fun saveToFile() {
        permissions()
        filemanger?.saveFileDialog(instructionFragment.instructionManager.getStringForSaving())
    }

    override fun loadFromFile(){
        permissions()
        filemanger?.loadFileDialog()
    }

    override fun doneLoading(){
        instructionFragment?.instructionManager.load(filemanger!!.instructions)
        instructionFragment?.mInstructionAdapter?.notifyDataSetChanged()
        mimaModul?.memoryModul?.saveToMemory(filemanger!!.instructions)
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
        mimaFragment.updateRegisters()
        mimaFragment.reDraw()
    }

    override fun closeInstructions() {
        extendNormal()
    }

    override fun extendInstructions() {
        extendFullscreen(instructionFragment)
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

    override fun extendInformations() {
        extendFullscreen(informationFragment)
    }

    /*
    * MimaFragmentCallbacks
    * */

    override fun writeExternal(identifier : String) {
        if (identifier == "Char") {
            //Outputs one Char
            val exportView = findViewById<TextView>(R.id.ExportView)
            var exportString = exportView.text.toString()
            exportString += mimaModul?.memoryModul?.SIR?.Content?.toChar()
            exportView.setText(exportString)
        } else if (identifier == "Integer") {
            //outputs one Integer
            val exportView = findViewById<TextView>(R.id.ExportView)
            exportView.text = mimaModul?.memoryModul?.SIR?.Content.toString()
        }
    }

    override fun readExternal() {
        stopMima()
        val importView = findViewById<EditText>(R.id.importView)
        importView.visibility = View.VISIBLE
        importView.isClickable
        showKeyboard(this)
    }

    override fun readExternalDone(text: Char) {
        val asciiConvert = Integer.decode("0x" + Integer.toHexString(text.toInt()))
        mimaModul?.readExternalDone(asciiConvert)
        mimaFragment.updateRegisters()
        hideKeyboard(this)
        if (wasRunning)
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

        /* Let OPTIONS know which Element there is to Edit*/
        informationFragment.updateView(currentlyLoadedElement)
    }

    /* StepControl */
    override fun startButtonPressed() {
        wasRunning = true
        mimaModul?.speedChanged(speed)
        startMima()
    }

    override fun stopButtonPressed() {
        wasRunning = false
        stopMima()
    }

    override fun stepButtonPressed() {
        mimaModul?.step()
        updateMima()
    }

    override fun speedChanged(speed: Long) {
        /*speed from 0 to maxdelay*/
        if (prefInvertSpeed){
            if (speed >= 0.toLong())
                this.speed = prefMaxDelay - speed
            else
                this.speed = 0.toLong()
        } else {
          this.speed = speed
        }
        mimaModul?.speedChanged(this.speed)
    }

    /*
    * Preview Callbacks
    * */

    override fun informationPreviewClicked() {
        openInformation()
    }

    override fun instructionPreviewClicked() {
        openInstruction()
    }

    /*
    * HaltMima from MimaModul (HLT Instruktion)
    * */
    override fun stop() {
        stopButtonPressed()
    }
}
