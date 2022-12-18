package com.patel.tanmay.assignment_login.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.activities.LoadingDialog
import com.patel.tanmay.assignment_login.fragments.ChangeRoomBottomSheet
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Member
import org.json.JSONObject


class roomMemberAdapter(var context : Context, var list : List<Member>,var roomID : String, val fetchMembers: () -> Unit, val token : String, val fm : FragmentManager, val roomHash : JSONObject, val rNumber: Int) : RecyclerView.Adapter<roomMemberAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): roomMemberAdapter.ViewHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.member_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: roomMemberAdapter.ViewHolder, position: Int) {
        val listItem : Member = list[position]
        holder.name.text = listItem.name
        holder.phone.text="+91 "+listItem.phone

        Glide.with(context).load(listItem.profileimage).diskCacheStrategy(
            DiskCacheStrategy.ALL).into(holder.profileImage);

        holder.phoneButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
               // Toast.makeText(context, "Call!", Toast.LENGTH_SHORT).show()
                val phoneIntent =  Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:"+listItem.phone))
                if (ContextCompat.checkSelfPermission(context,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        context as Activity, arrayOf(android.Manifest.permission.CALL_PHONE),
                        91)

                } else {
                    context.startActivity(phoneIntent);

                }

            }
        })

        holder.menuButton.setOnClickListener{
            val popupMenu = PopupMenu(context,holder.menuButton)
            popupMenu.menuInflater.inflate(R.menu.user_popup_menu,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                @SuppressLint("SuspiciousIndentation")
                override fun onMenuItemClick(item: MenuItem?): Boolean {

                  if(item?.title == "Delete Member" ) deleteRoomMember(listItem)
                    else if (item?.title == "Change Room") {
                      val changeFormFragment = ChangeRoomBottomSheet(token,rNumber.toString(),roomHash,listItem._id,fetchMembers)
                      changeFormFragment.show(fm,changeFormFragment.tag)
                  }else ""


                    return true
                }
            })

            popupMenu.show()

        }


    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.tvmemberName)
        val phone = itemView.findViewById<TextView>(R.id.img_chat_icon)
        val profileImage = itemView.findViewById<ImageView>(R.id.receiver_profile_image)
        val phoneButton = itemView.findViewById<ImageView>(R.id.phone_icon)
        val menuButton = itemView.findViewById<ImageView>(R.id.member_menu_icon)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun deleteRoomMember(listItem : Member){
        val loadingDialog = context?.let { LoadingDialog(it) }
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()

        val request = VolleyRequest(context, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                fetchMembers()
                loadingDialog?.dismiss()

            }

            override fun errorCallback(error_message: JSONObject?) {
                loadingDialog?.dismiss()
                Toast.makeText(context, "Try again after sometime!", Toast.LENGTH_LONG).show()
            }


            override fun responseStatus(response_code: NetworkResponse?) {
            }
        })

        val URL_DELETE_ROOM_MEMBER = Constants.REMOVE_USER_FROM_ROOM+"?roomid="+roomID+"&userid="+listItem._id
        request.deleteRequest(URL_DELETE_ROOM_MEMBER,token)
    }
}