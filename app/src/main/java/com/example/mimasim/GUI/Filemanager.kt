package com.example.mimasim.GUI

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mimasim.Instruction
import com.example.mimasim.R
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


/**
 * Created by Martin on 07.11.2017.
 */
class Filemanager(var context: Context) {
    //On the tested Devide it is internalstorage/Documents/Mima-Programs
    val dirName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/Mima-Programs"
    val mydir = File(dirName)
    val filenames = ArrayList<String>()
    val instructions = ArrayList<Instruction>()
    val extension = ".txt"

    var loadedInstr = 0
    var filemanagerCallback: FilemanagerCallback? = null

    interface FilemanagerCallback{
        fun doneLoading(){}
    }

    data class OpCodeResult(val isOpcodeString : Boolean, val opCode: Int)
    data class HexStringResult(val isValidHexString : Boolean, val hexString: String)

    init {
        if (!mydir.exists()) {
            mydir.mkdirs()
        }
        filemanagerCallback = context as FilemanagerCallback
    }

    fun getFilenames(){
        if (mydir.listFiles() != null){
            filenames.clear()
            mydir.listFiles().mapTo(filenames) { it.name }
        }
    }

    private fun save(filename : String, content: String){
        val fullname = mydir.path + "/" + filename + extension
        val file = File(fullname)
        if (file.exists())
            file.delete()
        try {

            file.writeText(content)
        } catch (e : IOException){
            Log.e("IOException", "File write failed $e")
        }
    }

    private fun load(filename : String){
        this.instructions.clear()
        val fullname = mydir.path + "/" + filename
        try {
            val inputStream : InputStream = File(fullname).inputStream()
            val lineList = mutableListOf<String>()
            inputStream.bufferedReader().useLines{ lines ->lines.forEach { lineList.add(it) } }

            for (line in lineList){
                val tmpInstruction = Instruction()
                val tmpline = line.split(" ")

                if (tmpline.isNotEmpty()) {
                    val tmpOpcode = translateOpCodeString(tmpline[0])
                    if (tmpOpcode.isOpcodeString) {
                        //If the OpCodeString could be translated we know it is valid and can be added as well as the translated opcode
                        tmpInstruction.opCodeString = tmpline[0]
                        tmpInstruction.opCode = tmpOpcode.opCode
                    } else {
                        //Do something if the first thingy is not Opcode
                    }
                }

                if (tmpline.size > 1){
                    val tmpHexString = translateHexString(tmpline[1])
                    if (tmpHexString.isValidHexString) {
                        tmpInstruction.address = Integer.decode(tmpHexString.hexString)
                    }
                    //If read was succesfull we add the read instruction. If not we add nothing
                    instructions.add(tmpInstruction)
                }

            }
        }  catch (e : FileNotFoundException) {
            Log.e("Loading From File", "File not found: " + e.toString());
        } catch (e : IOException) {
            Log.e("Loading From File", "Can not read file: " + e.toString());
        }

        filemanagerCallback?.doneLoading()
    }

    private fun translateOpCodeString(opCodeString : String) : OpCodeResult{
        val opCodeArray = context.resources.getStringArray(R.array.OPCodeArray)
        for (entry in opCodeArray)
            if (opCodeString == entry)
                return OpCodeResult(true, opCodeArray.indexOf(entry))
        Log.d("Loading From File","Error translating opcodeString to opcode. May result in faulty program")
        return OpCodeResult(false, 0)
    }

    private fun translateHexString(hexstring: String) : HexStringResult{
        //Check Adresslength Should be "0x" + 7 Digits
        val hexStringresult = HexStringResult(false, "0x0")
        var editHexString = hexstring

        //Check if the first two symbols are the hexprefix if so remove them
        if (editHexString.length > 3){
            if (editHexString[0] == '0' && editHexString[1] == 'x'){
                editHexString = editHexString.subSequence(2, editHexString.length).toString()
            }
        }

        //Check if it is to long if so read the first digits
        if (editHexString.length > 7){
            Log.d("Loading From File", "Error reading an Address. It is to Long. Address will be Cut on the right.")
            editHexString = editHexString.subSequence(0, 7).toString()
        }

        //Check if Hexstring only contains hexsymbols
        val allowedHexsymbols = context.resources.getString(R.string.allowedHexSymbols)
        for (char in editHexString)
            if(!allowedHexsymbols.contains(char)){
                //Does contain non Hexsymbols
                return HexStringResult(false, "0x0")
            }

        //all checks done should be a valid string
        return HexStringResult(true, "0x" + editHexString)
    }

    fun saveFileDialog(content : String){
        val dialogBuilder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    AlertDialog.Builder(context)
                }

        val inflater = (context as Activity).layoutInflater

        val view = inflater.inflate(R.layout.save_file_dialog, null)
        val filenameBox = view.findViewById<TextView>(R.id.filenameBox)

        dialogBuilder.setTitle(context.resources.getString(R.string.dialogSaveFileTitle))
        dialogBuilder.setMessage(context.resources.getString(R.string.dialgSaveFileMessage))
        dialogBuilder.setPositiveButton(android.R.string.yes, object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which:Int) {
                save(filenameBox.text.toString(), content)
            }
        })
        dialogBuilder.setNegativeButton(android.R.string.no) { dialog, _ -> dialog.cancel() }
        dialogBuilder.setView(view)
        dialogBuilder.create()
        dialogBuilder.show()
    }

    fun loadFileDialog(){
        var filename = ""
        val dialogBuilder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    AlertDialog.Builder(context)
                }

        dialogBuilder.setTitle(context.resources.getString(R.string.dialogLoadFileTitle))
        getFilenames()
        val arrayAdapter = ArrayAdapter<String>(context, R.layout.open_file_dialog_item, filenames)
        dialogBuilder.setAdapter(arrayAdapter, object : DialogInterface.OnClickListener{
           override fun onClick(dialog : DialogInterface , which: Int){
                filename = arrayAdapter.getItem(which)
                load(filename)
                dialog.dismiss()
           }
        })
        dialogBuilder.setNegativeButton(R.string.abortButton) { dialog, _ -> dialog.cancel() }
        dialogBuilder.create()
        dialogBuilder.show()
    }
}