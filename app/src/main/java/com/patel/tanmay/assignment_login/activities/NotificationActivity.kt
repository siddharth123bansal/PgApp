package com.patel.tanmay.assignment_login.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.adapter.notificationAdapter
import com.patel.tanmay.assignment_login.interfaces.CallBack
import com.patel.tanmay.assignment_login.models.Message
import com.patel.tanmay.assignment_login.models.Notification
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {
    val TAG = "NOTIFI"
    private lateinit var notificationAdapter: notificationAdapter
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationList : ArrayList<Notification>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationList = ArrayList()
        val user = JSONObject(intent.getStringExtra("USER"))
        notificationRecyclerView = findViewById(R.id.recyclerViewNotifications)
        notificationAdapter = notificationAdapter(this,notificationList)
        notificationRecyclerView.adapter = notificationAdapter

        fetchNotifications(user.getString("token"))

        findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                finish()
            }
        })
    }

    fun fetchNotifications(userToken : String){
        val loadingDialog = LoadingDialog(this)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        val request = VolleyRequest(this, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                notificationList.clear()
                if(response.has("message") && response.getString("message").equals("No notifications")){
                    Toast.makeText(this@NotificationActivity, response.toString(), Toast.LENGTH_LONG).show()
                }else {
                    val notifications = response.getJSONArray("notifications")
                    if(notifications.length() > 0){
                        for(i in 0..notifications.length()-1){
                            val notification = notifications.get(i) as JSONObject
                            val notificationObj = Notification(notification.getString("message"))
                            notificationList.add(notificationObj)
                        }
                    }
                }

                notificationAdapter.notifyDataSetChanged()
                loadingDialog.cancel()



            }


            override fun errorCallback(error_message: JSONObject) {
                loadingDialog.cancel()
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@NotificationActivity, "Notifications not found!", Toast.LENGTH_LONG).show()

            }

            override fun responseStatus(response_code: NetworkResponse?) {

            }

        })

        request.getRequest(Constants.GET_NOTIFICATIONS,userToken)
    }

}