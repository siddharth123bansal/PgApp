package com.patel.tanmay.assignment_login.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.Utils
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.interfaces.CallBack
import kotlinx.android.synthetic.main.activity_apply_now.*
import org.json.JSONObject

class ApplyNowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_now)

        val textPP =
            "<font color=#000F08>By signing up you accept the </font> <font color=#1B5299>Terms of Service</font><font color=#000F08> and </font><font color=#1B5299> Privacy Policy</font>"
        terms.setText(Html.fromHtml(textPP))

        val textSign =
            "<font color=#000F08>Already have an account? </font> <font color=#1B5299>Log in</font>"
        loginButton.setText(Html.fromHtml(textSign))

        loginButton.setOnClickListener{
            finish()
        }

        btnApply.setOnClickListener{
            val name = evName.text.toString().trim()
            val phone = evPhone.text.toString().trim()
            val email = evEmail.text.toString().trim()
            val pgName = evPGName.text.toString().trim()

            if(name.length != 0 && phone.length != 0 &&
                email.length != 0 && pgName.length != 0){

                val loadingDialog  = LoadingDialog(this)
                loadingDialog.show()

                val request = VolleyRequest(this@ApplyNowActivity, object :
                    CallBack {
                    override fun responseCallback(response: JSONObject) {
                        loadingDialog.dismiss()
                        Toast.makeText(this@ApplyNowActivity, "Application send successfully!", Toast.LENGTH_LONG).show()
                        finish()


                    }

                    override fun errorCallback(error_message: JSONObject) {
                        loadingDialog.dismiss()
                        Toast.makeText(this@ApplyNowActivity, error_message.getString("error"), Toast.LENGTH_SHORT).show()
                    }

                    override fun responseStatus(response_code: NetworkResponse?) {
                    }
                })

                val body = JSONObject()
                body.put("pgname",pgName)
                body.put("email",email)
                body.put("name",name)
                body.put("phone",phone)


                request.postWithBody(Constants.APPLY_FOR_APP_ACCESS,body,"")
            }else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}