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
import kotlinx.android.synthetic.main.activity_enter_otp.*
import org.json.JSONObject

class EnterOtp : AppCompatActivity() {
    var email:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_otp)
        email=intent.getStringExtra("email").toString()
        againOTP.setOnClickListener {
            resendotp()
        }
        btnotp.setOnClickListener{
            val Otp =otp.text.toString().trim()
            val pass=newPass.text.toString().trim()
            if(Otp.length!=0 && pass.length!=0){
                sendreq()
            }
            else{
                Toast.makeText(this,"All fiels are req", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun resendotp()
    {
        loadings.visibility= View.VISIBLE
        val request = VolleyRequest(this, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                val res=response.getString("message")
                Log.d("msgsa",res.toString())
                if(res.toString().equals("OTP sent successfully")) {
                    Toast.makeText(this@EnterOtp,res.toString(), Toast.LENGTH_LONG).show()
                    loadings.visibility=View.GONE
                }
            }
            override fun errorCallback(error_message: JSONObject?) {
                Toast.makeText(this@EnterOtp,
                    "An error occurred!Please try again later.",
                    Toast.LENGTH_SHORT).show()
                loadings.visibility= View.GONE
            }
            override fun responseStatus(response_code: NetworkResponse?) {
            }
        })
        val bodyData = JSONObject()
        bodyData.put("email", email)
        request.postWithBody(Constants.reqotp, bodyData, "")
    }
    private fun sendreq() {
        loadings.visibility = View.VISIBLE
        val request = VolleyRequest(this,object : CallBack {
            override fun responseCallback(response: JSONObject?) {
                val res= JSONObject(response.toString())
                val msg=res.getString("message")
                Log.d("asds",msg.toString())
                if(msg.toString().equals("Password changed successfully")){
                    Toast.makeText(
                        this@EnterOtp,
                        "Password changed successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    loadings.visibility = View.GONE
                    startActivity(Intent(this@EnterOtp,LoginActivity::class.java))
                }
            }
            override fun errorCallback(error_message: JSONObject?) {
                Toast.makeText(this@EnterOtp,"Error", Toast.LENGTH_LONG).show()
                loadings.visibility = View.GONE
                Log.d("asds","error")
            }
            override fun responseStatus(response_code: NetworkResponse?) {
            }
        })
        val bodyData = JSONObject()
        bodyData.put("email",intent.getStringExtra("email").toString().trim())
        bodyData.put("password",newPass.text.toString().trim())
        bodyData.put("otp",otp.text.toString().trim())
        request.putWithBody(Constants.changepOTP,bodyData,"")
    }
}
