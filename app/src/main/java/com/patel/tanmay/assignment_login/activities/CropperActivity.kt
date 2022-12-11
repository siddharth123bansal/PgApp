package com.patel.tanmay.assignment_login.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.patel.tanmay.assignment_login.R
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class CropperActivity : AppCompatActivity() {
    private lateinit var result : String
    private lateinit var fileUri: Uri
    private lateinit var imageType : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cropper_layout)

        readIntent();
        val dest_uri = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()

        val options = UCrop.Options()
        if(!imageType.equals("BANNER"))
         options.setCircleDimmedLayer(true)


        UCrop.of(fileUri,Uri.fromFile(File(cacheDir,dest_uri)))
            .withOptions(options)
            .withAspectRatio(0F, 0F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(1080,1080)
            .start(this@CropperActivity)

    }

    fun readIntent(){
        val i = intent
        if(i.extras != null){
            result = i.getStringExtra("IMGURI").toString()
            imageType =  i.getStringExtra("IMGTYPE").toString()
            fileUri = Uri.parse(result)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP){
            val resultUri = UCrop.getOutput(data!!)
            val returnIntent = Intent()
            returnIntent.putExtra("RESULT",resultUri.toString()+"")
            setResult(-1,returnIntent)
            finish()
        }else Toast.makeText(this, "Upload ERROR!", Toast.LENGTH_SHORT).show()
    }
}