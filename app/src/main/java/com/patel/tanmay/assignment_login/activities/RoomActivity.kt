package com.patel.tanmay.assignment_login.activities

import android.annotation.SuppressLint
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
import com.google.gson.Gson
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.models.Member
import com.patel.tanmay.assignment_login.adapter.roomMemberAdapter
import com.patel.tanmay.assignment_login.fragments.MemberFormFragment
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.MonthyRent
import com.patel.tanmay.assignment_login.models.Room
import org.json.JSONArray
import org.json.JSONObject

class RoomActivity : AppCompatActivity() {
    lateinit var room: Room
    lateinit var user : JSONObject
    lateinit var memberRecyclerView: RecyclerView
    lateinit var roomMemberAdapter: roomMemberAdapter
    val TAG = "ROOMMEB"
    lateinit var pgid:String
    var RES_CODE = -1


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        val gson = Gson()
        room =gson.fromJson(intent.getStringExtra("ROOM_DATA"), Room::class.java)
        user = JSONObject(intent.getStringExtra("USER").toString())
        pgid=intent.getStringExtra("_id").toString()

        val roomHash = JSONObject(intent.getStringExtra("ROOM_LIST"))


        if (room.members.size == 0) Toast.makeText(this@RoomActivity, "No members added to the room yet.", Toast.LENGTH_LONG).show()


        memberRecyclerView = findViewById(R.id.membersRV)
        roomMemberAdapter = roomMemberAdapter(this,room.members,room.roomID,::fetchRoomMembers,user.get("token").toString(),supportFragmentManager,roomHash,room.roomNumber)
        memberRecyclerView.adapter = roomMemberAdapter
        roomMemberAdapter.notifyDataSetChanged()

        findViewById<TextView>(R.id.roomHeading).text = "Floor No "+room.floorNumber+", Room No "+room.roomNumber
        findViewById<TextView>(R.id.roomSubHeading).text = ""+room.beds+" Beds Per Room, TV, Washroom"

            findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val i = Intent(this@RoomActivity,AllRoomsActivity::class.java)
                    i.putExtra("USER",user.toString())
                    i.putExtra("_id",pgid.toString())
                    finish()
                    startActivity(i)

                }
            })


        findViewById<ImageView>(R.id.btnAllowAccess).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if (room.members.size < room.beds){
                    val memberFormFragment = MemberFormFragment(user.get("token").toString(),room.roomID, ::fetchRoomMembers)
                    memberFormFragment.show(supportFragmentManager,memberFormFragment.tag)
                } else
            Toast.makeText(this@RoomActivity, "Room Full! No beds empty in this room", Toast.LENGTH_LONG).show()


            }
        })




    }

    fun fetchRoomMembers(){
        val loadingDialog = LoadingDialog(this)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        val request = VolleyRequest(this, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                room.members.clear()
                val roomsObj = response.get("rooms") as JSONArray
                if(roomsObj.length() > 0){
                    for (i in 0..roomsObj.length()-1){
                        val roomItem = roomsObj.get(i) as JSONObject
                        if(roomItem.get("_id") == room.roomID){
                            val userList = roomItem.get("users") as JSONArray
                            if(userList.length() > 0){
                                for(i in 0..userList.length()-1){
                                    val user = (userList.get(i) as JSONObject).get("user") as JSONObject
                                    val name = user.get("name").toString()
                                    val phone = user.get("phone").toString()
                                    val occupation = user.get("occupation").toString()
                                    val email = user.get("email").toString()
                                    val id = user.get("_id").toString()
                                    val profileimage = user.get("profileimage").toString()
                                  //  val rentPaid =(userList.get(i) as JSONObject).get("rentpaid") as Boolean
                                    val list = ArrayList<MonthyRent>()

                                    room.members.add(Member(name,phone,id,occupation,email,list,profileimage))
                                }
                            }else{
                                 Toast.makeText(this@RoomActivity, "No members added to the room yet.", Toast.LENGTH_LONG).show()
                            }

                        }


                    }

                    roomMemberAdapter.notifyDataSetChanged()



                }


                loadingDialog.cancel()
            }

            override fun errorCallback(error_message: JSONObject) {
                loadingDialog.cancel()
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@RoomActivity, "Try again after sometime!", Toast.LENGTH_LONG).show()
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