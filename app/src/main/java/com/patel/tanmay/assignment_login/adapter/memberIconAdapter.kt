package com.patel.tanmay.assignment_login.adapter

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import androidx.recyclerview.widget.RecyclerView
import com.patel.tanmay.assignment_login.R

import com.patel.tanmay.assignment_login.models.MemberIcon
import com.patel.tanmay.assignment_login.models.Room


class memberIconAdapter(var context : Context, var list : List<String>,val room : Room,val flag : Boolean) : RecyclerView.Adapter<memberIconAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): memberIconAdapter.ViewHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.room_box_text,parent,false)
        return ViewHolder(view)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: memberIconAdapter.ViewHolder, position: Int) {
        val listItem : String = list[position]
        holder.letter.text = listItem

            if(flag){
                if(listItem.equals("Nill")){
                    holder.letter.text = ""
                    holder.letter.setBackgroundResource(R.drawable.empty_block)
                }

                else if (position > room.rentPaidCount-1){
                    holder.letter.setBackgroundResource(R.drawable.room_empty_bg)
                    holder.letter.setTextColor(R.color.app_black)
                }


            }else{

                if(position > room.members.size-1){
                    holder.letter.setBackgroundResource(R.drawable.room_empty_bg)
                    holder.letter.setTextColor(R.color.app_black)
                }

            }




    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val letter = itemView.findViewById<TextView>(R.id.letter)


    }

    override fun getItemCount(): Int {
        return list.size
    }
}