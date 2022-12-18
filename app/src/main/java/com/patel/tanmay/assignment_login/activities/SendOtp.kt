package com.patel.tanmay.assignment_login.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.interfaces.CallBack
import kotlinx.android.synthetic.main.activity_send_otp.*
import org.json.JSONObject

class SendOtp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_otp)

    if(forgetEmail.text.toString().trim()!=null){
        val email=forgetEmail.text.toString().trim()
        btnsendotp.setOnClickListener {
            sendotp()
        }

    }
    else{
        Toast.makeText(this,"Email is required to send OTP", Toast.LENGTH_LONG).show()
    }
}

fun sendotp(){
    loading.visibility= View.VISIBLE
    val request = VolleyRequest(this, object :
        CallBack {
        override fun responseCallback(response: JSONObject) {
            val res = response.getString("message")
            Log.d("msgsa", res.toString())
            Toast.makeText(this@SendOtp, res.toString(), Toast.LENGTH_LONG).show()
            if (res.toString().equals("OTP sent successfully")) {
                val i = Intent(this@SendOtp, EnterOtp::class.java)
                i.putExtra("email", forgetEmail.text.toString())
                loading.visibility = View.GONE
                startActivity(i)
            }
        }
        override fun errorCallback(error_message: JSONObject?) {
            Toast.makeText(this@SendOtp,
                "An error occurred!Please try again later.",
            Toast.LENGTH_SHORT).show()
            loading.visibility= View.GONE
        }
        override fun responseStatus(response_code: NetworkResponse?) {

        }
    })
    val bodyData = JSONObject()
    bodyData.put("email", forgetEmail.text.toString().trim().toString())
    request.postWithBody(Constants.reqotp, bodyData, "")
    }

}