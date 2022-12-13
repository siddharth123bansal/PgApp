package com.patel.tanmay.assignment_login.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.patel.tanmay.assignment_login.*
import com.patel.tanmay.assignment_login.interfaces.CallBack
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    lateinit var loginBtn : TextView
    lateinit var emailED : TextInputEditText
    lateinit var passwordED : TextInputEditText
    lateinit var rememberCB : CheckBox
    lateinit var loading : LinearLayout

    val TAG =  "LOGIN"
    var RES_CODE = -1
    
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn = findViewById(R.id.btnLogin)
        emailED = findViewById(R.id.evEmail)
        passwordED = findViewById(R.id.evPassword)
        rememberCB = findViewById(R.id.cbPaid)
        loading = findViewById(R.id.loading)
            if (Utils.isUserPresent(this)){
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.putExtra("USER",Utils.getUser(this))
                startActivity(intent)
                finish()
            }

        val text = "<font color=#000F08>Interested to join? </font> <font color=#1B5299>Apply now</font>"
        applyButton.setText(Html.fromHtml(text))

        applyButton.setOnClickListener{
            startActivity(Intent(this@LoginActivity,ApplyNowActivity::class.java))
        }
        
        loginBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {

                val userEmail = emailED.text.toString().trim()
                val userPass = passwordED.text.toString().trim()

                if(userEmail.length !=0 && userPass.length != 0){

                    loading.visibility = View.VISIBLE
                    loginBtn.isEnabled = false

                    Log.d(TAG,"EMAIL : "+userEmail+"  Pass : "+userPass)

                    val request = VolleyRequest(this@LoginActivity, object :
                        CallBack {
                        override fun responseCallback(response: JSONObject) {

                            val userObj = response.get("user") as JSONObject
                            sendDeviceToken(userObj)
                            fetchData(userObj)
                        }
                        override fun errorCallback(error_message: JSONObject) {
                            loading.visibility = View.GONE
                            loginBtn.isEnabled = true
                            Log.d(TAG,error_message.getString("error"))
                            Toast.makeText(this@LoginActivity, error_message.getString("error"), Toast.LENGTH_LONG).show()
                        }
                        override fun responseStatus(response_code: NetworkResponse?) {
                            if (response_code != null) {
                                RES_CODE = response_code.statusCode
                            }
                            Log.d(TAG,"RESPONCE_CODE : "+response_code)
                        }
                    })
                    val bodyData = JSONObject()
                    bodyData.put("email",userEmail)
                    bodyData.put("password",userPass)
                    request.postWithBody(Constants.LOGIN_URL,bodyData,"")
                }
                else {
                    Toast.makeText(this@LoginActivity, "Invaild Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun sendDeviceToken(res: JSONObject) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val deviceToken = task.result
            Log.d(TAG,"DEV TOKEN :->    "+deviceToken)

            val request = VolleyRequest(this@LoginActivity, object :
                CallBack {
                override fun responseCallback(response: JSONObject) {
                    Log.d(TAG,response.toString())
                }

                override fun errorCallback(error_message: JSONObject) {
                    Log.d(TAG,error_message.getString("error"))
                    Toast.makeText(this@LoginActivity, error_message.getString("error"), Toast.LENGTH_LONG).show()

                }

                override fun responseStatus(response_code: NetworkResponse?) {
                    if (response_code != null) {
                        RES_CODE = response_code.statusCode
                    }
                    Log.d(TAG,"RESPONCE_CODE : "+response_code)
                }
            })

            val body = JSONObject()
            body.put("devicetoken",deviceToken)

            request.postWithBody(Constants.ADD_DEVICE_TOKEN,body,res.get("token").toString())




        })    }

    private fun fetchData(res : JSONObject){
        val request = VolleyRequest(this@LoginActivity, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                val resUser = response.get("user") as JSONObject
                val userMap = HashMap<String,String>()
                userMap.put("id",res.get("_id").toString())
                Log.d(TAG,"LOGIN UID : :-> "+userMap.get("id"))
                try{
                    userMap.put("name",res.get("name").toString())
                    userMap.put("email", res.get("email").toString())
                    userMap.put("usertype", res.get("usertype").toString())
                    userMap.put("token", res.get("token").toString())
                    userMap.put("phone" , resUser.get("phone").toString())
                    userMap.put("profileimage" , resUser.get("profileimage").toString())
                    userMap.put("pgname", resUser.getString("pgname"))
                    userMap.put("pgaddress", resUser.getString("pgaddress"))
                    userMap.put("coverimage", resUser.getString("coverimage"))
                    userMap.put("profileid", resUser.getString("profileid"))
                }catch (e : Exception){
                }



                if(rememberCB.isChecked){
                    Utils.saveUser(this@LoginActivity,JSONObject(userMap as Map<*, *>?).toString())
                }
                loading.visibility = View.GONE
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.putExtra("USER",JSONObject(userMap as Map<*, *>?).toString())
                startActivity(intent)
                finish()

            }

            override fun errorCallback(error_message: JSONObject) {
                Log.d(TAG,error_message.getString("error"))
                Toast.makeText(this@LoginActivity, error_message.getString("error"), Toast.LENGTH_LONG).show()

            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                    RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })

        request.getRequest(Constants.PROFILE_FETCH_URL,res.get("token").toString())
    }
}