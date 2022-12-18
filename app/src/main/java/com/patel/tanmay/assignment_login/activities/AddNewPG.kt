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
import org.json.JSONObject

class AddNewPG(context:Context, val token:String) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_pg)
        addpgbtn.setOnClickListener {
            if(newpgname.text.toString().length!=0){
                AddNewPg()
            }
            else{
                Toast.makeText(context,"Required a PG Name",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun AddNewPg() {
        val request = VolleyRequest(context,object : CallBack {
            override fun responseCallback(response: JSONObject?) {
                val res= JSONObject(response.toString())
                val msg=res.getString("message")
                Log.d("asd",msg.toString())
                Toast.makeText(context,msg.toString(),Toast.LENGTH_LONG).show()
            }
            override fun errorCallback(error_message: JSONObject?) {
                Log.d("asd","error in adding Pg")
            }
            override fun responseStatus(response_code: NetworkResponse?) {
                Log.d("asd",response_code.toString())
            }
        })
        val bodyData = JSONObject()
        bodyData.put("pgname",newpgname.text.toString().trim())
        request.postWithBody(Constants.Add_New_Pg,bodyData,token)
    }


}