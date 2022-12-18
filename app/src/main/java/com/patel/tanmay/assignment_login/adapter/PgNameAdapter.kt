package com.patel.tanmay.assignment_login.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.Utils
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.activities.HomeActivity
import com.patel.tanmay.assignment_login.activities.PgnameLogin
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.PgNameModel
import org.json.JSONArray
import org.json.JSONObject

class PgNameAdapter(val context: Context, val list:ArrayList<PgNameModel>, val email:String,val pass:String, val user:JSONObject,val rememberCB:CheckBox ): RecyclerView.Adapter<PgNameAdapter.ViewHolder>() {

    lateinit var pgname:String
    lateinit var pgid:String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PgNameAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.samplepgname, parent, false)
        return PgNameAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PgNameAdapter.ViewHolder, position: Int) {
        val pg: PgNameModel = list.get(position)
        holder.npg.text = pg.name
        holder.itemView.setOnClickListener {
            pgname=pg.name.toString()
            pgid=pg.id.toString()
            //Toast.makeText(context,(""+pgname+" "+pgid).toString(),Toast.LENGTH_SHORT).show()
            sendDeviceToken(user)
            fetchData(user)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val npg = itemView.findViewById<TextView>(R.id.spgname)

    }
    private fun sendDeviceToken(res: JSONObject) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val deviceToken = task.result
            Log.d("TAG","DEV TOKEN :->    "+deviceToken)

            val request = VolleyRequest(context, object :
                CallBack {
                override fun responseCallback(response: JSONObject) {
                    Log.d("TAG",response.toString())
                }

                override fun errorCallback(error_message: JSONObject) {
                    Log.d("TAG",error_message.getString("error"))
                    Toast.makeText(context, error_message.getString("error"), Toast.LENGTH_LONG).show()

                }

                override fun responseStatus(response_code: NetworkResponse?) {
                    if (response_code != null) {
                    }
                    Log.d("TAG","RESPONCE_CODE : "+response_code)
                }
            })

            val body = JSONObject()
            body.put("devicetoken",deviceToken.toString())
            request.postWithBody(Constants.ADD_DEVICE_TOKEN,body,res.get("token").toString())
        })
    }
    private fun fetchData(res : JSONObject){
        val request = VolleyRequest(context, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                Log.d("response","asd")
                val resUser = response.get("user") as JSONObject
                val userMap = HashMap<String,String>()
                userMap.put("id",res.get("_id").toString())
                Log.d("asdf","LOGIN UID : :-> "+userMap.get("id"))
                try{
                    userMap.put("name",res.get("name").toString())
                    userMap.put("email", res.get("email").toString())
                    userMap.put("usertype", res.get("usertype").toString())
                    userMap.put("token", res.get("token").toString())
                    userMap.put("phone" , resUser.get("phone").toString())
                    userMap.put("profileimage" , resUser.get("profileimage").toString())
                    userMap.put("pgname", pgname)
                    userMap.put("pgaddress", resUser.getString("pgaddress"))
                    userMap.put("coverimage", resUser.getString("coverimage"))
                    userMap.put("profileid", resUser.getString("profileid"))
                }catch (e : Exception){
                }
                if(rememberCB.isChecked){
                    Utils.saveUser(context,JSONObject(userMap as Map<*, *>?).toString())
                }
                val intent = Intent(context, HomeActivity::class.java)
                Log.d("UserMap",userMap.toString())
                intent.putExtra("USER",JSONObject(userMap as Map<*, *>?).toString())
                intent.putExtra("_id",pgid.toString())
                context.startActivity(intent)
            }
            override fun errorCallback(error_message: JSONObject) {
                Log.d("TAG",error_message.getString("error"))
                Toast.makeText(context, error_message.getString("error"), Toast.LENGTH_LONG).show()

            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                }
                Log.d("TAG","RESPONCE_CODE : "+response_code)
            }
        })
        var u=Constants.PROFILE_FETCH_URL+pgid.toString()
        Log.d("Tos",u)
        request.getRequest(u.toString(),res.get("token").toString())
    }
}
