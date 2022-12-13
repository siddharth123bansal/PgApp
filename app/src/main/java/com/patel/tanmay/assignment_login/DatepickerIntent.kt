package com.patel.tanmay.assignment_login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_datepicker_intent.*

class DatepickerIntent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datepicker_intent)
        var str =intent.getStringExtra("data")
        textValue.setText(str)
        //Toast.makeText(this@DatepickerIntent,"present count is "+str,Toast.LENGTH_LONG).show()
    }
}