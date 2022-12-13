package com.patel.tanmay.assignment_login.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.models.Notification

class notificationAdapter(val context: Context, val notificationList : ArrayList<Notification>) : RecyclerView.Adapter<notificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notificationAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_notifications,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification : Notification = notificationList.get(position)
        holder.notificationText.text = notification.notification
    }

    override fun getItemCount(): Int {
      return  notificationList.size
    }


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val notificationText = itemView.findViewById<TextView>(R.id.notification_text)

    }
}