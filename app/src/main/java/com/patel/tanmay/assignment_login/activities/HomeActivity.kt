package com.patel.tanmay.assignment_login.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.Utils
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Member
import com.patel.tanmay.assignment_login.models.MonthyRent
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.xml.transform.ErrorListener


class HomeActivity : AppCompatActivity() {
    lateinit var profileImageView: CircleImageView
    private lateinit var pgName : TextView
    private lateinit var userObj : JSONObject
    private lateinit var roomsText : TextView
    private lateinit var memberText : TextView
    private lateinit var rentsText : TextView
    private var totalBeds = 0
    private var totalMembers = 0
    private var totalRentPaid = 0;
    var bell:String="true"
    var url:String ="https://siddharthbansal.000webhostapp.com/user.json"
    var y:Int=0
    var m:Int=0
    var day:Int=0
    var c:Int=0
    var finalcount:Int=0
    private lateinit var timeObj : JSONObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        timeObj = JSONObject()
        timeObj.put("month",month+1)
        timeObj.put("year",year)
        Utils.saveDate(this,timeObj.toString())

        roomsText = findViewById(R.id.roomText)
        memberText = findViewById(R.id.memberText)
        rentsText = findViewById(R.id.rentText)

        pgName = findViewById(R.id.pgname_textView)

        userObj = JSONObject(intent.getStringExtra("USER"))
        fetchData()
        calendar.setOnClickListener {
            val pdp =DatePickerDialog(this,DatePickerDialog.OnDateSetListener { view, iy, im, id ->
                y=iy
                m=im
                day=id
            },2022,12,12)
            Toast.makeText(this@HomeActivity,"present count is "+(finalcount),Toast.LENGTH_LONG).show()
            pdp.show()
        }
        fetchNotifications(userObj.get("token").toString())

        if(userObj.has("pgname"))
        pgName.setText(userObj.getString("pgname"))
        else pgName.setText("No name")

        try {
            Log.d("HOME","SENDING UID :-> "+userObj.getString("id"))
        }catch (e : Exception){
            Toast.makeText(this, "An error occurred please re-login!", Toast.LENGTH_SHORT).show()
            Utils.deleteUser(this)
            startActivity(Intent(this@HomeActivity,LoginActivity::class.java))
        }
        profileImageView = findViewById(R.id.receiver_profile_image)

        Glide.with(this@HomeActivity).load(userObj.get("profileimage"))
            .error(R.drawable.man)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(profileImageView)



            profileImageView.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val i = Intent(this@HomeActivity,ProfileActivity::class.java)
                    i.putExtra("USER",intent.getStringExtra("USER"))
                    startActivity(i)
                    finish()
                }
            })

        pgName.setOnClickListener{
            val i = Intent(this@HomeActivity,PGInfoActivity::class.java)
            i.putExtra("TOKEN",userObj.getString("token"))
            startActivity(i)
        }

        findViewById<CardView>(R.id.allRoomsCard).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@HomeActivity,AllRoomsActivity::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                startActivity(i)


            }
        })

        findViewById<CardView>(R.id.rentsCard).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@HomeActivity,Rents::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                startActivity(i)


            }
        })

        findViewById<ImageView>(R.id.notificationBtn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                markasread(userObj.get("token").toString())
                val i = Intent(this@HomeActivity,NotificationActivity::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                startActivity(i)
            }
        })


        findViewById<CardView>(R.id.allMembersCard).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@HomeActivity,AllMembersActivity::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                startActivity(i)


            }
        })
    }

    fun markasread(userToken : String){

        Toast.makeText(this@HomeActivity," marked as read",Toast.LENGTH_LONG).show()
        val request =VolleyRequest(this,object :CallBack{
            override fun responseCallback(response: JSONObject?) {
                val res=JSONObject(response.toString())
                val msg=res.getString("message")
                Log.d("asd",msg)
//                Toast.makeText(this@HomeActivity,"Marked as read",Toast.LENGTH_LONG).show()
            }
            override fun errorCallback(error_message: JSONObject?) {
                Log.d("asd","error")
            }
            override fun responseStatus(response_code: NetworkResponse?) {
                Log.d("asd",response_code.toString())
            }
        })
        val bodyData = JSONObject()
        request.putWithBody(Constants.markAsRead,bodyData,userToken)
    }

    fun fetchNotifications(userToken : String){
        //Toast.makeText(this@HomeActivity,"Checking Red Dot",Toast.LENGTH_LONG).show()
        val request = VolleyRequest(this, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                //notificationList.clear()
                val res=JSONObject(response.toString())
                Log.d("resads",res.toString())
                val bellicon=res.getString("message")
                //Toast.makeText(this@HomeActivity,"response is "+res.toString(),Toast.LENGTH_LONG).show()

                //Toast.makeText(this@HomeActivity," "+bellicon,Toast.LENGTH_LONG).show()
                if(bellicon.equals("No notifications")) {
                    //Toast.makeText(this@HomeActivity, "NO Red Dot", Toast.LENGTH_LONG).show()
                    notificationBtn.setImageResource(R.drawable.bell)
                }
                else {
                    notificationBtn.setImageResource(R.drawable.notification_icon)
                    //Toast.makeText(this@HomeActivity, "Yes Red Dot", Toast.LENGTH_LONG).show()
                }
            }
            override fun errorCallback(error_message: JSONObject) {
                Toast.makeText(this@HomeActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun responseStatus(response_code: NetworkResponse?) {
                //Toast.makeText(this@HomeActivity,"NO Red Dot",Toast.LENGTH_LONG).show()
            }
        })
        request.getRequest(Constants.GET_NOTIFICATIONS,userToken)
    }
    private fun fetchData(){
        totalBeds = 0
        totalMembers = 0
        totalRentPaid = 0
        val request = VolleyRequest(this@HomeActivity, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                val roomsObj = response.get("rooms") as JSONArray
                if(roomsObj.length() > 0){
                    for (i in 0..roomsObj.length()-1){
                        val roomItem = roomsObj.get(i) as JSONObject
                        Log.d("HOME",roomItem.toString())
                        val beds = roomItem.get("beds") as Int
                        val userList = roomItem.get("users") as JSONArray
                        val userArrayList = ArrayList<Member>()

                        totalBeds += beds

                        if(userList.length() > 0){
                            for(i in 0..userList.length()-1){

                                val userOBJ = userList.getJSONObject(i)
                                var user = JSONObject()
                                user = userOBJ.getJSONObject("user")
                                val name = user.get("name").toString()
                                val phone = user.get("phone").toString()
                                val occupation = ""
                                val email = user.get("email").toString()
                                val id = user.get("_id").toString()
                                val profileimage = user.get("profileimage").toString()
                                val dnd=user.get("dnd").toString()
                                if(dnd=="false")c++

                                val rentPaidArray =(userList.get(i) as JSONObject).get("rentpaid") as JSONArray
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
                                val m = Member(name,phone,id,occupation,email,rentHistoryList,profileimage)
                                m.setRentPaid(timeObj.getInt("month"),timeObj.getInt("year"))
                                if(m.rentPaid) totalRentPaid++
                                userArrayList.add(m)
                            }
                        }
                        totalMembers += userArrayList.size
//                        Toast.makeText(this@HomeActivity,"present count is "+(totalMembers-c),Toast.LENGTH_LONG).show()
                        finalcount=(totalMembers-c)
                    }






                }else {

                }

               // loadingDialog.cancel()

                memberText.setText("("+(totalMembers-c)+")")
                animation_view2.visibility = View.GONE
                memberText.visibility = View.VISIBLE

                roomsText.setText("("+totalMembers+"/"+totalBeds+")")
                animation_view1.visibility = View.GONE
                roomsText.visibility = View.VISIBLE

                rentsText.setText("("+totalRentPaid+"/"+totalMembers+")")
                animation_view_3.visibility = View.GONE
                rentsText.visibility = View.VISIBLE

            }

            override fun errorCallback(error_message: JSONObject) {
               // loadingDialog.cancel()
                Toast.makeText(this@HomeActivity, "Try again after sometime!", Toast.LENGTH_LONG).show()
            }

            override fun responseStatus(response_code: NetworkResponse?) {

            }
        })

        request.getRequest(Constants.GET_ROOMS_DATA,userObj.get("token").toString())
    }


    override fun onBackPressed() {
        finish()
        finishAffinity()
    }

    override fun onRestart() {
        super.onRestart()
        fetchData()
    }
}