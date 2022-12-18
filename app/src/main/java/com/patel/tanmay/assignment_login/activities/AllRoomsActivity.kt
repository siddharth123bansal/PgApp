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
import com.android.volley.VolleyError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.Utils
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.adapter.roomsAdapter
import com.patel.tanmay.assignment_login.fragments.RoomFormFragment
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Member
import com.patel.tanmay.assignment_login.models.MonthyRent
import com.patel.tanmay.assignment_login.models.Room
import com.patel.tanmay.assignment_login.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class AllRoomsActivity : AppCompatActivity() {
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var roomRV : RecyclerView
    private lateinit var user: JSONObject
    private val roomList = ArrayList<Room>()
    private lateinit var adapter : roomsAdapter
    private lateinit var roomIDHash : JSONObject
    private lateinit var roomHeading : TextView
    lateinit var pgid:String
    val TAG = "ALLROOM"
    var RES_CODE = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_rooms)
        pgid=intent.getStringExtra("_id").toString()
        user = JSONObject(intent.getStringExtra("USER"))
        roomIDHash = JSONObject()

        roomHeading = findViewById(R.id.allRoomsHeading)

        findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@AllRoomsActivity, HomeActivity::class.java)
                i.putExtra("USER",intent.getStringExtra("USER"))
                i.putExtra("_id",pgid.toString())
                startActivity(i)
                finish()
                finishAffinity()
            }
        })
        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val roomsForm = RoomFormFragment(user.get("token").toString(), pgid.toString(),:: fetchData,"")
                roomsForm.show(supportFragmentManager,roomsForm.tag)
            }
        })
        toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)
        roomRV = findViewById(R.id.recyclerViewRooms)

        adapter = roomsAdapter(this,roomList,pgid.toString(),supportFragmentManager,user,:: fetchData, roomIDHash)
        roomRV.adapter = adapter

        fetchData()
    }

    private fun fetchData(){
        roomList.clear()
        val loadingDialog = LoadingDialog(this)
        loadingDialog.setCancelable(false)
        loadingDialog.show()

        val request = VolleyRequest(this@AllRoomsActivity, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {

                if(response.getString("rooms").toString().equals("null")){
                    roomList.clear()
                }else {
                    val roomsObj = response.get("rooms") as JSONObject
                    if (roomsObj.length() > 0) {
                        val roomNumber = roomsObj.get("roomnumber") as Int
                        val floorNumber = roomsObj.get("floornumber") as Int
                        val beds = roomsObj.get("beds") as Int
                        val roomId = roomsObj.getString("_id").toString()
                        roomIDHash.put(roomNumber.toString(), roomId)
                        val userList = roomsObj.get("users") as JSONArray
                        val userArrayList = ArrayList<Member>()
                        if (userList.length() > 0) {
                            for (i in 0..userList.length() - 1) {
                                val user = (userList.get(i) as JSONObject).get("user") as JSONObject
                                val name = user.get("name").toString()
                                val phone = user.get("phone").toString()
                                //val occupation = user.get("occupation").toString()
                                val occupation = ""
                                val email = user.get("email").toString()
                                val id = user.get("_id").toString()
                                val profileimage = user.get("profileimage").toString()
                                //val rentPaid =(userList.get(i) as JSONObject).get("rentpaid") as Boolean
                                val tempList = ArrayList<MonthyRent>()

                                userArrayList.add(
                                    Member(
                                        name,
                                        phone,
                                        id,
                                        occupation,
                                        email,
                                        tempList,
                                        profileimage
                                    )
                                )
                            }
                        }

                        val room = Room(roomNumber, floorNumber, beds, 0, roomId, userArrayList)
                        roomList.add(room)

                        adapter.notifyDataSetChanged()


                    } else {
                        roomList.clear()
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this@AllRoomsActivity, "No rooms found!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                roomHeading.setText("All Rooms ("+roomList.size+")")
                loadingDialog.cancel()

            }

            override fun errorCallback(error_message: JSONObject) {
                loadingDialog.cancel()
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@AllRoomsActivity, "Try again after sometime!", Toast.LENGTH_LONG).show()
            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                    RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })
        val pgid=intent.getStringExtra("_id").toString()
        request.getRequest(Constants.GET_ROOMS_DATA+pgid.toString(),user.get("token").toString())
    }

    override fun onRestart() {
        super.onRestart()
        fetchData()
        Log.d(TAG,"ALL ROOMS RESTARTED")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"ALL ROOMS RESUME")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"ALL ROOMS START")
    }

}