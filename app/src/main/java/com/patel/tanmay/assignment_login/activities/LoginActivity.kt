package com.patel.tanmay.assignment_login.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.google.android.material.textfield.TextInputEditText
import com.patel.tanmay.assignment_login.*
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.PgNameModel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    lateinit var loginBtn : TextView
    lateinit var emailED : TextInputEditText
    lateinit var passwordED : TextInputEditText
    lateinit var rememberCB : CheckBox
    lateinit var loading : LinearLayout
    lateinit var list:ArrayList<PgNameModel>

    val TAG =  "LOGIN"
    var RES_CODE = -1
    
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn = findViewById(R.id.btnLogin)
        list= ArrayList()
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
        forget.setOnClickListener {
            val i=Intent(this@LoginActivity,SendOtp::class.java)
            startActivity(i)
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
                if(userEmail.length !=0 && userPass.length != 0)
                {
                    loading.visibility = View.VISIBLE
                    loginBtn.isEnabled = false
                    Log.d(TAG,"EMAIL : "+userEmail+"  Pass : "+userPass)
                    val request = VolleyRequest(this@LoginActivity, object :
                        CallBack {
                        override fun responseCallback(response: JSONObject) {

                            val userObj = response.get("user") as JSONObject
                            val pgdata=userObj.get("pgdata") as JSONArray
                            Log.d("assssssssss",pgdata.toString())
                            for(i in 0..pgdata.length()-1){
                                val res=pgdata.get(i) as JSONObject

                                Log.d("dasssss",res.getString("_id").toString())
                                list.add(PgNameModel(res.getString("_id"),res.getString("pgname").toString()))
                            }
                            val ja:JSONArray
                            val pop=this?.let { PgnameLogin(this@LoginActivity,list,userEmail,userPass,userObj,rememberCB) }
                            pop?.setCancelable(true)
                            pop?.show()
                            loading.visibility=View.GONE
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
   //sendtoken

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when (item?.itemId) {

                        R.id.a1 -> {

                            true
                        }
                        R.id.a2 ->{
                            true
                        }
                        R.id.a3->{
                            true
                        }
                        R.id.a4->{
                            true
                        }
                        R.id.a5-> {
                            true
                        }

                        else -> {false}
                    }
                }

            })
            inflate(R.menu.pgowner)
            show()
        }
    }


    fun getpgnames(emails:String, pases:String){
        loading.visibility = View.VISIBLE
        loginBtn.isEnabled = false
        Log.d(TAG,"EMAIL : "+emails+"  Pass : "+pases)
        val request = VolleyRequest(this@LoginActivity, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {

                val userObj = response.get("user") as JSONObject
                val pgdata=userObj.get("pgdata") as JSONArray
                Log.d("assssssssss",pgdata.toString())
                for(i in 0..pgdata.length()-1){
                    val res=pgdata.get(i) as JSONObject

                    Log.d("dasssss",res.getString("_id").toString())
                    list.add(PgNameModel(res.getString("_id"),res.getString("pgname").toString()))
                }
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
        bodyData.put("email","pgowner1@gmail.com")
        bodyData.put("password","23456789")
        request.postWithBody(Constants.LOGIN_URL,bodyData,"")

    }
}