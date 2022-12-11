package com.patel.tanmay.assignment_login.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.Utils
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.adapter.rentsAdapter
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Member
import com.patel.tanmay.assignment_login.models.MonthyRent
import com.patel.tanmay.assignment_login.models.Room
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Rents : AppCompatActivity() {
    private lateinit var roomRV : RecyclerView
    private lateinit var roomList : ArrayList<Room>
    private lateinit var rentsAdapter: rentsAdapter
    private lateinit var user : JSONObject
    private lateinit var calendarButton : ImageView
    private var totalMembers = 0
    private var totalRendPaid = 0
    val TAG = "RENTS"
    private var RES_CODE = -1
    private lateinit var rentsHeading : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rents)

        calendarButton = findViewById(R.id.calenderPickerButton)
        rentsHeading = findViewById(R.id.rentsHeading)
        roomRV = findViewById(R.id.recyclerViewRooms)
        user = JSONObject(intent.getStringExtra("USER"))
        roomList = ArrayList()
        rentsAdapter = rentsAdapter(this,roomList,user, ::fetchData)
        roomRV.adapter = rentsAdapter
        val t = JSONObject(Utils.getTime(this))
        val year = t.getString("year").toInt()
        val month = t.getString("month").toInt()
        fetchData(month,year)

        calendarButton.setOnClickListener{
            val newFragment = RentDatePicker( ::fetchData)
            newFragment.show(supportFragmentManager, "datePicker")
        }


        findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@Rents,HomeActivity::class.java)
                i.putExtra("USER",user.toString())
                startActivity(i)
                finish()
                finishAffinity()
            }
        })



    }


    private fun fetchData(setMonth : Int, setYear : Int){
        roomList.clear()
        totalRendPaid = 0
        totalMembers = 0
        val loadingDialog = LoadingDialog(this)
        loadingDialog.setCancelable(false)
        loadingDialog.show()

        val request = VolleyRequest(this, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                val roomsObj = response.get("rooms") as JSONArray
                if(roomsObj.length() > 0){
                    for (i in 0..roomsObj.length()-1) {
                        val roomItem = roomsObj.get(i) as JSONObject
                        val roomNumber = roomItem.get("roomnumber") as Int
                        val floorNumber = roomItem.get("floornumber") as Int
                        val beds = roomItem.get("beds") as Int
                        val roomId = roomItem.get("_id").toString()
                        val userList = roomItem.get("users") as JSONArray
                        val userArrayList = ArrayList<Member>()
                        var rentPaidCount = 0
                        if(userList.length() > 0){
                            for(i in 0..userList.length()-1){
                                val user = (userList.get(i) as JSONObject).get("user") as JSONObject
                                val name = user.get("name").toString()
                                val phone = user.get("phone").toString()
//                                val occupation = user.get("occupation").toString()
                          val occupation = ""

                                val email = user.get("email").toString()
                                val id = user.get("_id").toString()
                                val profileimage = user.get("profileimage").toString()
                                val rentPaidArray =(userList.get(i) as JSONObject).getJSONArray("rentpaid")
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
                                       val mr = MonthyRent(month,year,paid)
                                        rentHistoryList.add(mr)
                                        Log.d(TAG,"IS PAID : "+mr.toString())
                                    }
                                }
                                val mem = Member(name,phone,id,occupation,email,rentHistoryList,profileimage)
                                mem.setRentPaid(setMonth,setYear)
                                Log.d(TAG,"RENT PAID? -> "+mem.rentPaid)
                                userArrayList.add(mem)
                                if(mem.rentPaid) rentPaidCount++
                                Log.d(TAG,"RENT CONT : "+rentPaidCount)
                            }
                        }
                        totalMembers += userArrayList.size
                        totalRendPaid += rentPaidCount
                        val room = Room(roomNumber,floorNumber,beds,rentPaidCount,roomId,userArrayList)
                        roomList.add(room)
                    }

                    rentsAdapter.notifyDataSetChanged()



                }else Toast.makeText(this@Rents, "No rooms found!", Toast.LENGTH_SHORT).show()
                rentsHeading.setText("Rents ("+totalRendPaid+"/"+totalMembers+")")
                loadingDialog.cancel()

            }

            override fun errorCallback(error_message: JSONObject) {
                loadingDialog.cancel()
               try {
                   Toast.makeText(this@Rents, error_message.getString("error"), Toast.LENGTH_LONG).show()
               }catch (e : Exception){
                   Toast.makeText(this@Rents, "Try again after sometime!", Toast.LENGTH_LONG).show()
               }

            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                    RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })

        request.getRequest(Constants.GET_ROOMS_DATA,user.get("token").toString())
    }
}