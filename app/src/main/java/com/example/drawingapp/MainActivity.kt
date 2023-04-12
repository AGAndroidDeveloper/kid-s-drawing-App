package com.example.drawingapp

import android.Manifest
import android.annotation.SuppressLint

import android.app.AlertDialog
import android.app.Dialog

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    //  val permissionStr :ArrayList<String> = arrayListOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { result ->
            result.entries.forEach {
                val PermissionName = it.key
                val value = it.value
                if (value) {
                    Toast.makeText(this, "permission allowed", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }

    var obj: DrawingView? = null
    var mCurrentColorBtn: ImageButton? = null
    var defaultColor = Color.GRAY
    var gallaryBtn: ImageButton? = null
    var permissionBtn: ImageButton? = null
    var filesaveObj: ImageButton? = null
    var objProgress :Dialog? = null
    var shareObj :ImageButton? = null
    private var frameLayoutObj :FrameLayout? = null

    private val galeryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { Result ->

            if (Result.resultCode == RESULT_OK && Result.data != null) {
                val btn: ImageView = findViewById(R.id.imageBackGround)
                btn.setImageURI(Result.data!!.data)
            }


        }


    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionBtn = findViewById(R.id.permissionDialog)

        val btn: ImageButton = findViewById(R.id.open_Gallery)

        btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galeryLauncher.launch(intent)
        }
        val undoObj: ImageButton = findViewById(R.id.undo_BTn)
        undoObj.setOnClickListener {
            val obj: DrawingView = findViewById(R.id.drawingView)
            obj.undoPath()
        }

        permissionBtn!!.setOnClickListener {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
        val brushObj: ImageButton = findViewById(R.id.brushImage)
        brushObj.setOnClickListener {
            setDialog()
        }
        val btnObj: Button = findViewById(R.id.button1)
        btnObj.setOnClickListener { // to make code look cleaner the color
            // picker dialog functionality are
            // handled in openColorPickerDialogue()
            // function
            openDialogForSelectColor()

        }
        obj = findViewById(R.id.drawingView)

        filesaveObj = findViewById(R.id.file_save)
        filesaveObj?.setOnClickListener {
               showProgressBarDialog()
           lifecycleScope.launch(){
               val frameLayoutObj :FrameLayout    = findViewById(R.id.frame_layout)
               saveImage(returnBitmap(frameLayoutObj))
           }
        }

         shareObj = findViewById(R.id.shareImage)
        shareObj?.setOnClickListener {

lifecycleScope.launch {
    val frameLayoutObj :FrameLayout    = findViewById(R.id.frame_layout)
    shareImage(saveImage(returnBitmap(frameLayoutObj)))

}


        }




    }

    private fun requestStoragePermission() {
        // Check if the permission was denied and show rationale
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            //call the rationale dialog to tell the user why they need to allow permission request
            showRationaleDialog(
                "Kids Drawing App", "Kids Drawing App " +
                        "needs to Access Your External Storage"
            )
        } else {
            // You can directly ask for the permission.
            //if it has not been denied then request for permission
            //  The registered ActivityResultCallback gets the result of this request.
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }


    private fun showRationaleDialog(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }


    @SuppressLint("SuspiciousIndentation")
    fun setDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.brushsize)
        dialog.setTitle("select brush size?")
        val smallBtn: ImageButton = dialog.findViewById(R.id.image1)
        smallBtn.setOnClickListener {
            obj?.setBrushSize(10)
            dialog.dismiss()
        }
        val mediumBtn: ImageButton = dialog.findViewById(R.id.image2)
        mediumBtn.setOnClickListener {
            obj?.setBrushSize(20)
            dialog.dismiss()
        }
        val largeBtn: ImageButton = dialog.findViewById(R.id.image3)
        largeBtn.setOnClickListener {
            obj?.setBrushSize(30)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openDialogForSelectColor() {
        val colorPickerDialogue = AmbilWarnaDialog(this, defaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    // leave this function body as
                    // blank, as the dialog
                    // automatically closes when
                    // clicked on cancel button
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // change the mDefaultColor to
                    // change the GFG text color as
                    // it is returned when the OK
                    // button is clicked from the
                    // color picker dialog
                    defaultColor = color
                    // now change the picked color
                    // preview box to mDefaultColor
                    // obj?.setColor(defaultColor)
                    // val textObj
                    val textObj: TextView? = null
                    textObj?.setBackgroundColor(defaultColor)
                    obj?.color = defaultColor
                    //  obj?.setColor(defaultColor)
                }
            }
        )
        colorPickerDialogue.show()
    }

    private fun returnBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    private suspend fun saveImage(mBitmap: Bitmap): String {
        var result = ""
        withContext(Dispatchers.IO) {     // run on behind
            if (mBitmap != null) {
                try {
                    val byte = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, byte)

                    val path = File(
                        externalCacheDir?.absoluteFile.toString()
                                + File.separator + "DrawingApp_" + System.currentTimeMillis() / 1000 + ".png"
                    )

                    val fileOutput = FileOutputStream(path)

                    fileOutput.write(byte.toByteArray())
                    fileOutput.close()
                    result = path.absolutePath
                    runOnUiThread {                // run on main ui
                        cancelProgressDialog()

                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "your saved drawing is here :$result",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "your files can't be saved ",
                                Toast.LENGTH_LONG
                            ).show()

                        }


                    }


                }catch (e :Exception){
                    result = ""
                    e.printStackTrace()
            }
        }
    }
        return result

}
    private fun showProgressBarDialog(){
       objProgress = Dialog(this)
        objProgress?.setContentView(R.layout.progress_dialog)

        objProgress?.show()



    }
    private fun cancelProgressDialog(){
        if (objProgress!=null){
            objProgress?.dismiss()
            objProgress = null
        }
    }


          fun shareImage(result :String){
MediaScannerConnection.scanFile(this, arrayOf(result),null){
    path , Uri ->

    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.putExtra(Intent.EXTRA_STREAM,Uri)
    shareIntent.type = "image/png"

    startActivity(Intent.createChooser(shareIntent,"share"))



}






    }


}



