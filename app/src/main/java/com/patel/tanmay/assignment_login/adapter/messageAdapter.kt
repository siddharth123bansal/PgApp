package com.patel.tanmay.assignment_login.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.models.Member
import com.patel.tanmay.assignment_login.models.Message
import org.json.JSONObject

class messageAdapter(val context : Context, val messageList : ArrayList<Message>, val user: JSONObject, val member : Member) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            // inflate receive
            var view = LayoutInflater.from(context).inflate(R.layout.message_received_layout,parent,false)
            return ReceivedViewHolder(view)
        }else{
            // inflate sent
            var view = LayoutInflater.from(context).inflate(R.layout.message_sent_layout,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList.get(position)
       if(holder.javaClass == SentViewHolder::class.java){
           // sent view holder
           val viewHolder = holder as SentViewHolder
           viewHolder.sentMessage.text = currentMessage.message
           Glide.with(context).load(user.get("profileimage"))
               .error(R.drawable.man)
               .diskCacheStrategy(
               DiskCacheStrategy.ALL).into(viewHolder.senderImage);
       }else{
           // receive view holder
           val viewHolder = holder as ReceivedViewHolder
           viewHolder.receivedMessage.text = currentMessage.message
           Glide.with(context).load(member.profileimage)
               .error(R.drawable.man)
               .diskCacheStrategy(
               DiskCacheStrategy.ALL).into(viewHolder.receiverImage);
       }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList.get(position)
        if(user.getString("id").equals(currentMessage.senderID)) return ITEM_SENT
        else return ITEM_RECEIVE
    }

    override fun getItemCount(): Int {
       return messageList.size

    }



    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.sentMessageText)
        val senderImage = itemView.findViewById<ImageView>(R.id.sender_profile_image)

    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receivedMessage = itemView.findViewById<TextView>(R.id.receivedMessageText)
        val receiverImage = itemView.findViewById<ImageView>(R.id.receiver_profile_image)

    }
}