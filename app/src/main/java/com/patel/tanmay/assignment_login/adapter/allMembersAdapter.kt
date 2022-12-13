package com.patel.tanmay.assignment_login.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.Utils
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.activities.ChatActivity
import com.patel.tanmay.assignment_login.activities.LoadingDialog
import com.patel.tanmay.assignment_login.activities.MemberInfoActivity
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Member
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class allMembersAdapter(var context : Context, var list : ArrayList<Member>, val roomID : String,val user : JSONObject) : RecyclerView.Adapter<allMembersAdapter.ViewHolder>() {
    private val TAG = "RENT"
    private var RES_CODE = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): allMembersAdapter.ViewHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.all_members_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: allMembersAdapter.ViewHolder, position: Int) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        var check:String=""


        val listItem : Member = list[position]
        holder.name.text = listItem.name
        Glide.with(context).load(listItem.profileimage)
            .error(R.drawable.man)
            .diskCacheStrategy(
            DiskCacheStrategy.ALL).into(holder.profileImage);

        holder.paidCheckBox.isChecked = listItem.rentPaid

        holder.callButton.setOnClickListener(object : View.OnClickListener{
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

        holder.paidCheckBox.isEnabled = roomID.length != 0
        holder.paidCheckBox.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val loadingDialog = LoadingDialog(context)
                loadingDialog.setCancelable(false)
                if(holder.paidCheckBox.isChecked) {
                    val request = VolleyRequest(context, object :
                        CallBack {
                        override fun responseCallback(response: JSONObject) {
                            //fetchData()
                            Log.d(TAG,response.toString())
                            loadingDialog?.dismiss()
                            Toast.makeText(context, "Rent paid successfully 👍", Toast.LENGTH_SHORT).show()


                        }


                        override fun errorCallback(error_message: JSONObject) {
                            loadingDialog?.dismiss()
                            Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                            Toast.makeText(context, error_message.getString("error"), Toast.LENGTH_LONG).show()
                            //Toast.makeText(context, check.toString(), Toast.LENGTH_LONG).show()

                        }

                        override fun responseStatus(response_code: NetworkResponse?) {
                            if (response_code != null) {
                                RES_CODE = response_code.statusCode
                            }
                            Log.d(TAG,"RESPONCE_CODE : "+response_code)
                        }
                    })

                    val t = JSONObject(Utils.getTime(context))
                    val year = t.getString("year").toInt()
                    val month = t.getString("month").toInt()

                    val bodyData = JSONObject()
                    bodyData.put("roomid",roomID)
                    bodyData.put("userid",listItem._id)
                    bodyData.put("month",month)
                    bodyData.put("year",year)
                    bodyData.put("paid",holder.paidCheckBox.isChecked.toString())
                    loadingDialog.show()
                    request.postWithBody(Constants.PAY_RENT,bodyData,user.get("token").toString())
                }else{

                }
            }
        })


        holder.chatButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val gson = Gson()
                val i = Intent(context,ChatActivity::class.java)
                i.putExtra("USER",user.toString())
                Log.d("CHATS","SENDING UID :-> "+user.getString("id"))
                i.putExtra("ROOM_ID",roomID)
                i.putExtra("MEMBER",gson.toJson(listItem))
                context.startActivity(i)
            }
        })
        
        holder.itemView.setOnClickListener{
            val infoDialog = MemberInfoActivity(context,user.getString("token"),listItem._id)
            infoDialog.setCancelable(true)
            infoDialog.show()
        }


    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name = itemView.findViewById<TextView>(R.id.tvmemberName)
        val paidCheckBox = itemView.findViewById<CheckBox>(R.id.cbPaid)
        val profileImage = itemView.findViewById<ImageView>(R.id.receiver_profile_image)
        val callButton = itemView.findViewById<ImageView>(R.id.phone_icon)
        val chatButton = itemView.findViewById<ImageView>(R.id.img_chat_icon)



    }

    override fun getItemCount(): Int {
        return list.size
    }
}