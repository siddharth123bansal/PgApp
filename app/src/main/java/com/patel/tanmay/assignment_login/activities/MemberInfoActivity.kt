package com.patel.tanmay.assignment_login.activities

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
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
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

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
        dateOfJoining.isEnabled=false
        roomnum.isEnabled=false
        roomsharing.isEnabled=false
        Rentpaid.isEnabled=false
        Rent.isEnabled=false
        RentAdv.isEnabled=false
        adharnumber.isEnabled=false
        Ocpt.isEnabled=false
        //idproof_image

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

                if(response.has("dateofjoining")) dateOfJoining.setText(response.getString("dateofjoining"))
                else dateOfJoining.setText("Not set")

                if(response.has("roomnumber")) roomnum.setText(response.getString("roomnumber"))
                else roomnum.setText("Not set")

                if(response.has("roomsharingoptions")) roomsharing.setText(response.getString("roomsharingoptions"))
                else roomsharing.setText("Not set")

                if(response.has("rentpaid")) Rentpaid.setText(response.getString("rentpaid"))
                else Rentpaid.setText("Not set")

                if(response.has("education")) evEducation.setText(response.getString("education"))
                else memberName.setText("Not set")

                if(response.has("rent")) Rent.setText(response.getString("rent"))
                else Rent.setText("Not set")

                if(response.has("homeplace")) evHomeAddress.setText(response.getString("homeplace"))
                else evHomeAddress.setText("Not set")

                if(response.has("advance")) RentAdv.setText(response.getString("advance"))
                else RentAdv.setText("Not set")

                if(response.has("occupation")) Ocpt.setText(response.getString("occupation"))
                else Ocpt.setText("Not set")

                if(response.has("aadharnumber")) {

//                    val d=decrypt("8n3g0Wdt7d1I7xYIx2Z42w==")
                    adharnumber.setText(response.getString("aadharnumber").toString())
                }else adharnumber.setText("Not set")

                if(response.has("idproof")){
                    Glide.with(context).load(response.getString("idproof"))
                        .error(R.drawable.banner)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(idproof_image)
                }
//
//                if(response.has("idproof")) idproof_image.set
//                else Rent.setText("Not set")

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
    fun decrypt(strToDecrypt : String) : String? {
        val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
        val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
        val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX
        try
        {

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return  String(cipher.doFinal(android.util.Base64.decode(strToDecrypt, android.util.Base64.DEFAULT)))
        }
        catch (e : Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }
}