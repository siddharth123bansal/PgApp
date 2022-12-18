package com.patel.tanmay.assignment_login.activities

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.interfaces.CallBack
import kotlinx.android.synthetic.main.activity_add_new_pg.*
import kotlinx.android.synthetic.main.activity_broad_cast_message.*
import org.json.JSONObject

class BroadCastMessage(context: Context, var pgid:String,var token:String) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broad_cast_message)
        bbtn.setOnClickListener {

            if(btitle.text.toString().length!=0 && bmessage.text.toString().length!=0){
                Broadcast()
            }
            else if (btitle.text.toString().isEmpty()){
                Toast.makeText(context,"Title is required",Toast.LENGTH_SHORT).show()
            }

            else if (bmessage.text.toString().isEmpty()){
                Toast.makeText(context,"Message is required",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"All fields is required",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun Broadcast()
    {
        val load=LoadingDialog(context)
        load.show()
        val request = VolleyRequest(context,object : CallBack {
            override fun responseCallback(response: JSONObject?) {
                val res= JSONObject(response.toString())
                val msg=res.getString("message")
                Log.d("asd",msg.toString())
                Toast.makeText(context,msg.toString(), Toast.LENGTH_LONG).show()
                load.dismiss()
            }
            override fun errorCallback(error_message: JSONObject?) {
                Log.d("asd","error in adding Pg")
                Toast.makeText(context,error_message.toString(), Toast.LENGTH_LONG).show()
                load.dismiss()
            }
            override fun responseStatus(response_code: NetworkResponse?) {
                Log.d("asd",response_code.toString())
            }
        })
        val bodyData = JSONObject()
        bodyData.put("pgid",pgid.toString())
        bodyData.put("title",btitle.text.toString().trim())
        bodyData.put("message",bmessage.text.toString().trim())
        request.postWithBody(Constants.broadcast,bodyData,token)
    }
}