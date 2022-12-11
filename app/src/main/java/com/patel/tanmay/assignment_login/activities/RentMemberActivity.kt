package com.patel.tanmay.assignment_login.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.adapter.allMembersAdapter
import com.patel.tanmay.assignment_login.models.Room
import kotlinx.android.synthetic.main.activity_rent_member.*
import org.json.JSONObject

class RentMemberActivity : AppCompatActivity() {
    private lateinit var memberRentListRv : RecyclerView
    lateinit var roomMemberAdapter: allMembersAdapter
    lateinit var room: Room
    lateinit var user : JSONObject
    val TAG = "ROOMMEB"
    var RES_CODE = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_member)

        val gson = Gson()
        room =gson.fromJson(intent.getStringExtra("ROOM_DATA"), Room::class.java)
        user = JSONObject(intent.getStringExtra("USER").toString())

        roomSubHeading.setText(""+room.beds+" Beds Per Room, TV, Washroom")


        memberRentListRv = findViewById(R.id.rentMembersRV)
        roomMemberAdapter = allMembersAdapter(this,room.members,room.roomID,user)
        memberRentListRv.adapter = roomMemberAdapter
        roomMemberAdapter.notifyDataSetChanged()

        findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(this@RentMemberActivity,Rents::class.java)
                i.putExtra("USER",user.toString())
                startActivity(i)
            }
        })

    }
}