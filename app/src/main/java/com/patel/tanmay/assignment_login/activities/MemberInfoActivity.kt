package com.patel.tanmay.assignment_login.activities

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.NetworkResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.interfaces.CallBack
import kotlinx.android.synthetic.main.activity_member_info.*
import org.json.JSONObject

class MemberInfoActivity(context: Context,val token : String, val memberID : String) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_info)

        maleRB.isEnabled = false
        femaleRB.isEnabled = false
        username_input.isEnabled = false
        phone_input.isEnabled = false
        evAltNumber.isEnabled = false
        vegRB.isEnabled = false
        nonvegRB.isEnabled = false
        evOfficeStart.isEnabled = false
        evOfficeEnds.isEnabled = false
        evHomeAddress.isEnabled = false
        evEducation.isEnabled = false

        fetchMemberDetails()


        close.setOnClickListener{
            this.dismiss()
        }
    }


    fun fetchMemberDetails(){

        val request = VolleyRequest(context, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                Log.d("INFO",response.toString())
                if(response.has("name")) memberName.setText(response.getString("name"))
                else memberName.setText("No Name")

                if(response.has("email")) username_input.setText(response.getString("email"))
                else memberName.setText("Not set")

                if(response.has("phone")) phone_input.setText(response.getString("phone"))
                else memberName.setText("0000000000")

                if(response.has("alternatenumber")) evAltNumber.setText(response.getString("alternatenumber"))
                else memberName.setText("0000000000")

                if(response.has("officehoursstart")) evOfficeStart.setText(response.getString("officehoursstart"))
                else memberName.setText("00")

                if(response.has("officehoursend")) evOfficeEnds.setText(response.getString("officehoursend"))
                else memberName.setText("00")

                if(response.has("homeplace")) evHomeAddress.setText(response.getString("homeplace"))
                else memberName.setText("Not set")

                if(response.has("education")) evEducation.setText(response.getString("education"))
                else memberName.setText("Not set")

                if(response.has("gender")){
                    when(response.getString("gender")) {
                        "Male" -> maleRB.isChecked = true
                        else -> femaleRB.isChecked = true
                    }
                }

                if(response.has("foodtype")){
                    when(response.getString("foodtype")) {
                        "veg" -> vegRB.isChecked = true
                        else -> nonvegRB.isChecked = true
                    }
                }

                if (response.has("dnd")) {
                    if(response.getBoolean("dnd")) dndStatus.setText("DND : ON")
                    else dndStatus.setText("DND : OFF")
                }else dndStatus.setText("DND : NOT SET")

                if(response.has("profileimage")){
                    Glide.with(context).load(response.getString("profileimage"))
                        .error(R.drawable.man)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(member_image)
                }




            }

            override fun errorCallback(error_message: JSONObject) {


            }

            override fun responseStatus(response_code: NetworkResponse?) {

            }
        })

        request.getRequest(Constants.GET_USER_PROFILE_BY_ID+memberID,token)

    }
}