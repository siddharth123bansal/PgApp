package com.patel.tanmay.assignment_login.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.Utils
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.adapter.allMembersAdapter
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Member
import com.patel.tanmay.assignment_login.models.MonthyRent
import com.patel.tanmay.assignment_login.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AllMembersActivity : AppCompatActivity() {
    val TAG = "ALLMEMBERS"
    var RES_CODE = -1
    lateinit var loadingDialog : LoadingDialog
    lateinit var user : JSONObject
    lateinit var allMemberAdapter: allMembersAdapter
    lateinit var allMemberRV : RecyclerView
    lateinit var pgid:String
    private lateinit var memberList : ArrayList<Member>
    private lateinit var headingText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_members)
        loadingDialog = LoadingDialog(this)
        loadingDialog.setCancelable(false)
        val gson = Gson()
        user = JSONObject(intent.getStringExtra("USER"))
        pgid=intent.getStringExtra("_id").toString()
        try {
            Log.d("HOME","SENDING UID :-> "+user.getString("id"))
        }catch (e : Exception){
            Toast.makeText(this, "An error occurred please re-login!", Toast.LENGTH_SHORT).show()
            Utils.deleteUser(this)
            startActivity(Intent(this,LoginActivity::class.java))
        }

        headingText = findViewById(R.id.allMemberHeading)
        allMemberRV = findViewById(R.id.recyclerViewALlMembers)
        memberList = ArrayList()
        allMemberAdapter = allMembersAdapter(this,memberList,"",user)
        allMemberRV.adapter = allMemberAdapter

        findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@AllMembersActivity, HomeActivity::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                i.putExtra("_id",pgid.toString())
                startActivity(i)
                finish()
                finishAffinity()
            }
        })



        val request = VolleyRequest(this, object :

            CallBack {

            override fun responseCallback(response: JSONObject)
            {
                memberList.clear()
                val userArray = response.get("users") as JSONArray
                if(userArray.length() > 0){
                    for(i in 0..userArray.length()-1){
                       val mem = userArray.get(i) as JSONObject
                        val name = mem.get("name").toString()
                        val _id = mem.get("_id").toString()
                        val email = mem.get("email").toString()
                        val phone = mem.get("phone").toString()
                        //val occupation = mem.get("occupation").toString()
                        val occupation = ""
                        //val rentPaid = mem.get("rentpaid") as Boolean
                        val profileimage = mem.get("profileimage").toString()
                        val rentPaidArray =mem.getJSONArray("rentpaid")
                        val rentHistoryList = ArrayList<MonthyRent>()
                        if(rentPaidArray.length() > 0){
                            for(i in 0..rentPaidArray.length()-1){
                                var month = -1
                                var year = -1
                                val obj = rentPaidArray.get(i) as JSONObject
                                if(obj.has("month"))
                                    month = obj.get("month").toString().toInt()
                                if (obj.has("year"))
                                    year = obj.get("year").toString().toInt()
                                val paid = obj.get("paid") as Boolean
                                rentHistoryList.add(MonthyRent(month,year,paid))
                            }
                        }
                        val t = JSONObject(Utils.getTime(this@AllMembersActivity))
                        val member = Member(name,phone,_id,occupation,email,rentHistoryList,profileimage)
                        member.setRentPaid(t.getInt("month"),t.getInt("year"))
                        memberList.add(member)

                    }


                    allMemberAdapter.notifyDataSetChanged()

                }
                else {
                    Toast.makeText(this@AllMembersActivity, "No members found!", Toast.LENGTH_SHORT).show()
                }
                headingText.setText("All Members ("+memberList.size+")")
                loadingDialog.cancel()
            }





            override fun errorCallback(error_message: JSONObject) {
               loadingDialog.cancel()
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@AllMembersActivity, "Try again after sometime!", Toast.LENGTH_LONG).show()

            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                    RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })

        request.getRequest(Constants.GET_ALL_USER+pgid.toString(),user.get("token").toString())
        loadingDialog.show()

    }
}