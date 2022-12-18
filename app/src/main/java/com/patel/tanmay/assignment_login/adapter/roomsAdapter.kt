package com.patel.tanmay.assignment_login.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.activities.RoomActivity
import com.patel.tanmay.assignment_login.fragments.RoomFormFragment
import com.patel.tanmay.assignment_login.models.Room
import org.json.JSONObject
import java.io.Serializable


class roomsAdapter(var context : Context, var list : ArrayList<Room>,val pgid:String, val supportFragmentManager: FragmentManager, val user : JSONObject, val fetchData: () -> Unit, val roomIdHash : JSONObject) : RecyclerView.Adapter<roomsAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): roomsAdapter.ViewHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.room_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: roomsAdapter.ViewHolder, position: Int) {
        val listItem : Room = list[position]


        holder.roomNo.text = listItem.roomNumber.toString()
        holder.beds.text = listItem.beds.toString()+" BHK"
        holder.floorNo.text = "Floor No - "+listItem.floorNumber

       val list = listOf<String>("A","B","C","D","E")
        val memberIconAdapter = memberIconAdapter(context,list.subList(0,listItem.beds),listItem,false)

        holder.current_state.adapter = memberIconAdapter
        memberIconAdapter.notifyDataSetChanged()

        holder.editRoomBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {

                    val bundle = Bundle()
                    bundle.putString("roomNumber", listItem.roomNumber.toString())
                    bundle.putString("floorNumber",listItem.floorNumber.toString())
                    bundle.putString("beds",listItem.beds.toString())


                    val roomFormFragment = RoomFormFragment(user.get("token").toString(),pgid.toString(),fetchData,listItem.roomID)
                    roomFormFragment.arguments = bundle
                    roomFormFragment.show(supportFragmentManager,roomFormFragment.tag)


            }
        })


        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
//                val args = Bundle()
//                args.putSerializable("ROOM_LIST", list as Serializable)
                val i = Intent(context,RoomActivity::class.java)
                val gson = Gson()
                val itemDataString = gson.toJson(listItem)
                i.putExtra("ROOM_DATA",itemDataString)
                i.putExtra("USER",user.toString())
                i.putExtra("_id",pgid.toString())
                i.putExtra("ROOM_LIST",roomIdHash.toString())

                context.startActivity(i)
             //   (context as  Activity).finish()
//                (context as Activity).finish()
            }
        })

        val margin = dpToPx(8)
        val layoutParams = holder.cardView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(margin,margin,margin,margin)
        holder.cardView.layoutParams = layoutParams


    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), Serializable{
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