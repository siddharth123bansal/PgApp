package com.patel.tanmay.assignment_login.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.finishAffinity
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.patel.tanmay.assignment_login.*
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Member
import com.patel.tanmay.assignment_login.models.MonthyRent
import com.patel.tanmay.assignment_login.models.Notification
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
    var y:Int=0
    lateinit var pgid:String
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
        val da = c.get(Calendar.DAY_OF_MONTH)
        timeObj = JSONObject()
        timeObj.put("month",month+1)
        timeObj.put("year",year)
        Utils.saveDate(this,timeObj.toString())
        pgid=intent.getStringExtra("_id").toString()
        roomsText = findViewById(R.id.roomText)
        memberText = findViewById(R.id.memberText)
        rentsText = findViewById(R.id.rentText)

        pgName = findViewById(R.id.pgname_textView)

        userObj = JSONObject(intent.getStringExtra("USER"))
        Log.d("userObj",userObj.toString())
        getMembersdata()
        fetchData()
        getMembersdata()
        fetchNotifications(userObj.get("token").toString())
        calendar.setOnClickListener {
            val pdp =DatePickerDialog(this,DatePickerDialog.OnDateSetListener { view, iy, im, id ->
                y=iy
                m=im
                day=id
                //Toast.makeText(this@HomeActivity,(""+finalcount+""+totalMembers.toString()),Toast.LENGTH_LONG).show()
                memberText.setText("("+finalcount.toString()+"/"+totalMembers.toString()+")")
            },year,month,da)
            pdp.datePicker.minDate=c.timeInMillis
            pdp.show()
        }
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
        broadcast.setOnClickListener {
            val pop=this?.let { BroadCastMessage(this@HomeActivity,pgid.toString(),userObj.getString("token").toString()) }
            pop?.setCancelable(true)
            pop?.show()
        }

            AddPg.setOnClickListener {
                val pop=this?.let { AddNewPG(this@HomeActivity,userObj.getString("token").toString()) }
                pop?.setCancelable(true)
                pop?.show()
            }
            profileImageView.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val i = Intent(this@HomeActivity,ProfileActivity::class.java)
                    i.putExtra("USER",intent.getStringExtra("USER"))
                    i.putExtra("_id",pgid)
                    startActivity(i)
                    finish()
                }
            })
        Log.d("homeid",pgid.toString())
        pgName.setOnClickListener{
            val i = Intent(this@HomeActivity,PGInfoActivity::class.java)
            i.putExtra("TOKEN",userObj.getString("token"))
            i.putExtra("_id",pgid)
            startActivity(i)
        }
        findViewById<CardView>(R.id.allRoomsCard).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@HomeActivity,AllRoomsActivity::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                i.putExtra("_id",pgid.toString())
                startActivity(i)


            }
        })
        getMembersdata()
        findViewById<CardView>(R.id.rentsCard).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@HomeActivity,Rents::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                i.putExtra("_id",pgid.toString())
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
                i.putExtra("_id",pgid.toString())
                startActivity(i)
            }
        })

    }


    fun markasread(userToken : String){
        //Toast.makeText(this@HomeActivity," marked as read",Toast.LENGTH_LONG).show()
        val request =VolleyRequest(this,object :CallBack{
            override fun responseCallback(response: JSONObject?) {
                val res=JSONObject(response.toString())
                val msg=res.getString("message")
                Log.d("asd",msg.toString())
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
                if(response.has("message") && response.getString("message").equals("No notifications")){
                    notificationBtn.setImageResource(R.drawable.bells)
                }else {
                    val res=JSONObject(response.toString())
                    val bellicon=res.getString("read")
                    if(bellicon.equals("true")) {
                        notificationBtn.setImageResource(R.drawable.bells)
                    }
                    else {
                        notificationBtn.setImageResource(R.drawable.notification_icon)
                    }
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
        c=0
        //Toast.makeText(this, "Hello World", Toast.LENGTH_LONG).show()
        val request = VolleyRequest(this@HomeActivity, object :
            CallBack {
            @SuppressLint("SuspiciousIndentation")
            override fun responseCallback(response: JSONObject) {
               // val res=response.getJSONObject("rooms")
                Log.d("us",response.getString("rooms").toString())
               // Log.d("nill",response.getJSONObject("rooms").getString("rooms"))
                if(response.getString("rooms").toString().equals("null")){
                    totalBeds = 0
                    totalMembers = 0
                    totalRentPaid = 0
                    c=0
                }
                else {
                    val roomsObj = response.get("rooms") as JSONObject

                    if (roomsObj.length() > 0) {
                        Log.d("HOME", roomsObj.toString())
                        val beds = roomsObj.get("beds") as Int
                        val userList = roomsObj.get("users") as JSONArray
                        val userArrayList = ArrayList<Member>()
                        totalBeds += beds

                        if (userList.length() > 0) {
                            //Toast.makeText(this@HomeActivity, ""+userList.length().toString(), Toast.LENGTH_SHORT).show()
                            for (i in 0..userList.length() - 1) {

                                val userOBJ = userList.getJSONObject(i)
                                var user = JSONObject()
                                user = userOBJ.getJSONObject("user")
                                val name = user.get("name").toString()
                                val phone = user.get("phone").toString()
                                val occupation = ""
                                val email = user.get("email").toString()
                                val id = user.get("_id").toString()
                                val profileimage = user.get("profileimage").toString()
                                val dnd = user.get("dnd").toString()
                                if (dnd.toString() == "false") c++

                                val rentPaidArray =
                                    (userList.get(i) as JSONObject).get("rentpaid") as JSONArray
                                val rentHistoryList = ArrayList<MonthyRent>()
                                if (rentPaidArray.length() > 0) {
                                    for (i in 0..rentPaidArray.length() - 1) {
                                        var month = -1
                                        var year = -1
                                        val obj = rentPaidArray.get(i) as JSONObject
                                        if (obj.has("month"))
                                            month = obj.get("month").toString().toInt()
                                        if (obj.has("year"))
                                            year = obj.get("year").toString().toInt()
                                        val paid = obj.get("paid") as Boolean
                                        rentHistoryList.add(MonthyRent(month, year, paid))
                                    }
                                }
                                val m = Member(
                                    name,
                                    phone,
                                    id,
                                    occupation,
                                    email,
                                    rentHistoryList,
                                    profileimage
                                )
                                m.setRentPaid(timeObj.getInt("month"), timeObj.getInt("year"))
                                if (m.rentPaid) totalRentPaid++
                                userArrayList.add(m)
                            }
                        } else {
                            c = 0
                        }
                        totalMembers += userArrayList.size
//                        Toast.makeText(this@HomeActivity,"present count is "+(totalMembers-c),Toast.LENGTH_LONG).show()
                        finalcount = c
                        memberText.setText("(" + c + "/" + totalMembers.toString() + ")")
                    } else {
                        c = 0
                        totalMembers = 0
                    }
                }
               // loadingDialog.cancel()


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
        Log.d("renturl",Constants.GET_ROOMS_DATA+pgid.toString())
        request.getRequest(Constants.GET_ROOMS_DATA+pgid.toString(),userObj.get("token").toString())
    }
    fun getMembersdata(){
        var count:Int=0;
        var total:Int=0
        val request = VolleyRequest(this, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
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
                        var dnd=mem.get("dnd")
                        //Toast.makeText(this@HomeActivity, dnd.toString(), Toast.LENGTH_SHORT).show()
                        if(dnd.toString()=="false") {
                            count++
                        }
                        Log.d("asd",dnd.toString())
                        Log.d("asd",count.toString())
                        //val rentPaid = mem.get("rentpaid") as Boolean
                        val profileimage = mem.get("profileimage").toString()
                        val rentPaidArray =mem.getJSONArray("rentpaid")
                        val rentHistoryList = ArrayList<MonthyRent>()
                        val t = JSONObject(Utils.getTime(this@HomeActivity))
                    }
                }else {
                    memberText.setText("("+"0"+"/"+"0"+")")
                    //Toast.makeText(this@HomeActivity, "No members found!", Toast.LENGTH_SHORT).show()
                }
                total=userArray.length()
                //Toast.makeText(this@HomeActivity, c.toString(), Toast.LENGTH_LONG).show()
                memberText.setText("("+count.toString()+"/"+total.toString()+")")
            }
            override fun errorCallback(error_message: JSONObject) {
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@HomeActivity, "Try again after sometime!", Toast.LENGTH_LONG).show()

            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                   var RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })

        request.getRequest(Constants.GET_ALL_USER+pgid.toString(),userObj.get("token").toString())

    }


    override fun onBackPressed() {
        finish()
        finishAffinity()
    }

    override fun onRestart() {
        super.onRestart()
        fetchData()
        getMembersdata()
    }
}