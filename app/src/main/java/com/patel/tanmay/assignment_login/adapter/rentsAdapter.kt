package com.patel.tanmay.assignment_login.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.activities.RentMemberActivity
import com.patel.tanmay.assignment_login.activities.RoomActivity
import com.patel.tanmay.assignment_login.fragments.RoomFormFragment
import com.patel.tanmay.assignment_login.models.Room
import org.json.JSONObject
import java.io.Serializable
import kotlin.reflect.KFunction2


class rentsAdapter(var context: Context, var list: ArrayList<Room>, val user: JSONObject, val fetchData: KFunction2<Int, Int, Unit>) : RecyclerView.Adapter<rentsAdapter.ViewHolder>(){
    private lateinit var memberIconAdapter : memberIconAdapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rentsAdapter.ViewHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.room_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: rentsAdapter.ViewHolder, position: Int) {
        val listItem : Room = list[position]


        holder.roomNo.text = listItem.roomNumber.toString()
        holder.beds.text = listItem.beds.toString()+" BHK"
        holder.floorNo.text = "Floor No - "+listItem.floorNumber

        if(listItem.members.size == 0){
            val list = listOf<String>("Nill")
             memberIconAdapter = memberIconAdapter(context,list,listItem,true)
        }else{
            val list = listOf<String>("A","B","C","D","E")
            memberIconAdapter = memberIconAdapter(context,list.subList(0,listItem.members.size),listItem,true)
        }
        holder.current_state.adapter = memberIconAdapter
        memberIconAdapter.notifyDataSetChanged()
        holder.editRoomBtn.visibility = View.GONE
        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val i = Intent(context, RentMemberActivity::class.java)
                val gson = Gson()
                val itemDataString = gson.toJson(listItem)
                i.putExtra("ROOM_DATA",itemDataString)
                i.putExtra("USER", user.toString())
                context.startActivity(i)
            }
        })
        val margin = dpToPx(8)
        val layoutParams = holder.cardView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(margin,margin,margin,margin)
        holder.cardView.layoutParams = layoutParams
    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), Serializable {
        var roomNo = itemView.findViewById<TextView>(R.id.tvRoomNo)
        var floorNo = itemView.findViewById<TextView>(R.id.tvFloorNo)
        val beds = itemView.findViewById<TextView>(R.id.tvBeds)
        val editRoomBtn = itemView.findViewById<ImageView>(R.id.imvEditRoom)
        val current_state = itemView.findViewById<RecyclerView>(R.id.memberIconRecyclerView)
        val cardView = itemView.findViewById<CardView>(R.id.roomsCardView)

    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun dpToPx(dp : Int) : Int {
        val px = dp * context.resources.displayMetrics.density
        return Math.round(px)
    }

}