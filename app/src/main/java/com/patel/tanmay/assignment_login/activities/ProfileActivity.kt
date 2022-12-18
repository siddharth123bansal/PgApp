package com.patel.tanmay.assignment_login.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.JsonReader
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.JsonArray
import com.patel.tanmay.assignment_login.*
import com.patel.tanmay.assignment_login.fragments.GrantAccessFromFragment
import com.patel.tanmay.assignment_login.fragments.UpdatePasswordFormFragment
import com.patel.tanmay.assignment_login.interfaces.CallBack
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_add_new_pg.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class ProfileActivity : AppCompatActivity() {

    private lateinit var logoutBtn :TextView
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var profilePic : CircleImageView
    private lateinit var userEmail : EditText
    private lateinit var userPhone : EditText
    private lateinit var userAddress : EditText
    private lateinit var mGetContent : ActivityResultLauncher<String>
    private lateinit var menuButton : ImageView
    private lateinit var resultUri : Uri
    private lateinit var updateBanner : ImageView
    private lateinit var bannerView : ConstraintLayout
    val TAG = "PROFILE"
    var RES_CODE = -1
    private lateinit var user : JSONObject
    private lateinit var token : String
    private lateinit var bitmap: Bitmap
    private lateinit var updateChangesButton : TextView
    private var imageRef = "PROFILE"
    lateinit var pgid:String
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        user = JSONObject(intent.getStringExtra("USER"))
        fetchDetails()
        pgid=intent.getStringExtra("_id").toString()
        toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        updateBanner = findViewById(R.id.editBanner)

        updateBanner.setOnClickListener{
            imageRef = "BANNER"
            mGetContent.launch("image/*")
        }
//        pgid=intent.getStringExtra("_id").toString()
//        Log.d("profileid",pgid.toString())


        bannerView = findViewById(R.id.bannerLayout)
        updateChangesButton = findViewById(R.id.UpdateAddressButton)
        userAddress = findViewById(R.id.address_input)
        logoutBtn = findViewById(R.id.Logout)
        userEmail = findViewById(R.id.username_input)
        userPhone = findViewById(R.id.phone_input)
        profilePic = findViewById(R.id.receiver_profile_image)
        menuButton = findViewById(R.id.profile_menuButton)

        userEmail.isEnabled = false
        userPhone.isEnabled = false

        findViewById<ImageView>(R.id.uploadProfileImage).setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //Toast.makeText(this@ProfileActivity, "Up Img", Toast.LENGTH_SHORT).show()
                    imageRef = "PROFILE"
                    mGetContent.launch("image/*")

            }
        }

        )


        mGetContent =  registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            val i = Intent(this@ProfileActivity,CropperActivity::class.java)
            i.putExtra("IMGURI",uri.toString())
            i.putExtra("IMGTYPE",imageRef)
            startActivityForResult(i,101)

        }

        findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val i = Intent(this@ProfileActivity, HomeActivity::class.java)
                i.putExtra("USER",user.toString())
                i.putExtra("_id",pgid.toString())
                startActivity(i)
                finish()
            }
        }

        )

        logoutBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Utils.deleteUser(this@ProfileActivity)
                logoutfrom(user.get("token").toString())
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
        }
        )
        menuButton.setOnClickListener{
            val popupMenu = PopupMenu(this,menuButton)
            popupMenu.menuInflater.inflate(R.menu.profile_menu,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                override fun onMenuItemClick(item: MenuItem?): Boolean {

                    if(item?.title == "Grant Access" ){
                        val accessForm = GrantAccessFromFragment(user.get("token").toString())
                        accessForm.show(supportFragmentManager,accessForm.tag)
                    }
                    else if(item?.title == "Change Password" ){
                        val changepassForm = UpdatePasswordFormFragment(user.get("token").toString())
                        changepassForm.show(supportFragmentManager,changepassForm.tag)
                    }

                    return true
                }
            })

            popupMenu.show()
        }
        if(user.has("profileid")) {
                profile_heading.setText("Building id: " + user.get("profileid").toString().uppercase())
            }
            userEmail.setText(user.get("email").toString())
            userPhone.setText(user.get("phone").toString())

        if(user.has("pgaddress"))
            userAddress.setText(user.getString("pgaddress"))
        else  userAddress.setText("No address set")

        Glide.with(this@ProfileActivity).load(user.get("profileimage"))
            .error(R.drawable.man)
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePic);

        if(user.has("coverimage")){

            Glide.with(this).load(user.get("coverimage"))
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            bannerView.setBackground(resource)
                        }
                    }
                })

        } else {
            Glide.with(this).load(R.drawable.banner)
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            bannerView.setBackground(resource)
                        }
                    }
                })
        }


            token = user.get("token").toString()


        updateChangesButton.setOnClickListener{
          val addr = userAddress.text.toString().trim()
            if(addr.length != 0){
                updateChanges()
            }else
                Toast.makeText(this, "Empty address field", Toast.LENGTH_SHORT).show()
        }
        }
    private fun updateChanges() {
        val loadingDialog = LoadingDialog(this)
        loadingDialog.setCancelable(false)
        loadingDialog.show()

            val body = JSONObject()
            val pgType = when (pgtype_radiogrp.checkedRadioButtonId){
                R.id.maleRB -> "male"
                R.id.femaleRB -> "female"
                R.id.colivingRB -> "coliving"
                else -> ""
            }

        val roomSharingOpt = JSONObject()
        roomSharingOpt.put("one",oneMem.isChecked)
        roomSharingOpt.put("two",twoMem.isChecked)
        roomSharingOpt.put("three",threeMem.isChecked)
        roomSharingOpt.put("four",fourMem.isChecked)

        val food = when(foodTimesRadioGroup.checkedRadioButtonId){
            R.id.twoTimes -> 2
            R.id.threeTimes -> 3
            else  -> 0
        }
        val facilities = JSONObject()
        facilities.put("hotwater",hotwater.isChecked)
        facilities.put("parkingspace",parking.isChecked)
        facilities.put("wifi",wifi.isChecked)
        facilities.put("customcooking",cooking.isChecked)
        facilities.put("washingmachine",wash.isChecked)
        facilities.put("gym",gym.isChecked)
        facilities.put("lift",lift.isChecked)
        facilities.put("studychairs",chairs.isChecked)
        facilities.put("smarttv",tv.isChecked)
        facilities.put("gamingroom",gaming.isChecked)
        facilities.put("cctv",cctv.isChecked)
        facilities.put("dailyroomcleaning",dailycleaning.isChecked)
        facilities.put("fridge",fridge.isChecked)
        facilities.put("ac",Ac.isChecked)
        facilities.put("alternativeroomcleaning",alternativecleaning.isChecked)

        val rules = JSONObject()
        rules.put("nosmoking",nosomke.isChecked)
        rules.put("noparties",noparties.isChecked)
        rules.put("novisitors",visitors.isChecked)




        body.put("pgid",pgid.toString())
        body.put("address",address_input.text.toString().trim())
        body.put("pgtype",pgType)
        body.put("roomsharingoptions",roomSharingOpt)
        body.put("food",food)
        body.put("facilities",facilities)
        body.put("rules",rules)
        val request = VolleyRequest(this, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                fetchData()
                loadingDialog?.dismiss()
                Toast.makeText(this@ProfileActivity, "Changes updated 👍", Toast.LENGTH_SHORT).show()
            }

            override fun errorCallback(error_message: JSONObject?) {
                loadingDialog?.dismiss()
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@ProfileActivity, error_message?.getString("error"), Toast.LENGTH_LONG).show()
            }


            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                    RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })

        request.putWithBody(Constants.CHANGE_PROFILE,body,token)
    }
    private fun logoutfrom(token:String)
    {
        val request = VolleyRequest(this,object : CallBack {
            override fun responseCallback(response: JSONObject?) {
                val res= JSONObject(response.toString())
                val msg=res.getString("message")
                Log.d("asd",msg.toString())
                Toast.makeText(this@ProfileActivity,msg.toString(),Toast.LENGTH_LONG).show()
            }
            override fun errorCallback(error_message: JSONObject?) {
                Log.d("asd","error in adding Pg")
            }
            override fun responseStatus(response_code: NetworkResponse?) {
                Log.d("asd",response_code.toString())
            }
        })
        val bodyData = JSONObject()
        request.postWithBody(Constants.remove,bodyData,token)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == -1 && requestCode == 101){
            val result = data?.getStringExtra("RESULT")

            if(result != null){
                resultUri = Uri.parse(result)
                if(imageRef.equals("PROFILE"))
                Glide.with(this@ProfileActivity).load(resultUri).diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePic)
                else{
                    Glide.with(this).load(resultUri)
                        .into(object : SimpleTarget<Drawable?>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable?>?
                            ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    bannerView.setBackground(resource)
                                }
                            }
                        })
                    }
                val  inputStream =this.contentResolver?.openInputStream(resultUri)
                bitmap = BitmapFactory.decodeStream(inputStream)
                uploadBitmap(bitmap)
            }
        }
    }
    private fun uploadBitmap(bitmap: Bitmap) {
        var URL = Constants.CHANGE_PROFILE_IMAGE
        if(!imageRef.equals("PROFILE")) URL = Constants.CHANGE_COVER_IMAGE
        val loading = LoadingDialog(this)
        loading.setCancelable(false)
        loading.show()
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.PUT,URL ,
            object : Response.Listener<NetworkResponse?> {
                override fun onResponse(response: NetworkResponse?) {
                    Log.d(TAG,"Upload success")
                    Toast.makeText(this@ProfileActivity, "Upload success", Toast.LENGTH_SHORT).show()
                    Log.d(TAG,response.toString())
                    fetchData()
                    loading.cancel()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    loading.cancel()
                    Toast.makeText(this@ProfileActivity, "Try again later!", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
//                    Log.e("GotError", "" + error.message)
                }
            }) {
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                val imagename = System.currentTimeMillis()
                if(imageRef == "PROFILE"){
                    params["profileimage"] = DataPart("$imagename.png", getFileDataFromDrawable(bitmap))

                } else
                    params["coverimage"] = DataPart("$imagename.png", getFileDataFromDrawable(bitmap))

                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = java.util.HashMap()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        volleyMultipartRequest.setRetryPolicy(
            DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }


    private fun fetchData(){
        val request = VolleyRequest(this@ProfileActivity, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                val resUser = response.get("user") as JSONObject
                val userMap = HashMap<String,String>()
                userMap.put("name",user.get("name").toString())
                userMap.put("email", user.get("email").toString())
                userMap.put("usertype", user.get("usertype").toString())
                userMap.put("token", user.get("token").toString())
                userMap.put("phone" , resUser.get("phone").toString())
                userMap.put("profileimage" , resUser.get("profileimage").toString())
                userMap.put("pgname", resUser.getString("pgname"))
                userMap.put("pgaddress", resUser.getString("pgaddress"))
                userMap.put("coverimage", resUser.getString("coverimage"))

                val updatedUser = JSONObject(userMap as Map<*, *>?).toString()
                user = JSONObject(updatedUser)
                Utils.saveUser(this@ProfileActivity,updatedUser)

            }

            override fun errorCallback(error_message: JSONObject) {
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@ProfileActivity, "Try again after sometime!", Toast.LENGTH_LONG).show()

            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                    RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })
        pgid=intent.getStringExtra("_id").toString()
        Log.d("PRo",Constants.PROFILE_FETCH_URL+pgid.toString())
        request.getRequest(Constants.PROFILE_FETCH_URL+pgid.toString(),user.get("token").toString())
    }
    private fun fetchDetails(){
        val request = VolleyRequest(this@ProfileActivity, object :
            CallBack {
            override fun responseCallback(response: JSONObject) {
                val res = response.get("user") as JSONObject

                if (res.has("pgtype")){
                    when(res.getString("pgtype")){
                        "male" -> pgtype_radiogrp.check(R.id.maleRB)
                        "female" -> pgtype_radiogrp.check(R.id.femaleRB)
                        "coliving" -> pgtype_radiogrp.check(R.id.colivingRB)
                    }
                }


                val roomShareObj = res.getJSONObject("roomsharingoptions")
                oneMem.isChecked = roomShareObj.getBoolean("one")
                twoMem.isChecked = roomShareObj.getBoolean("two")
                threeMem.isChecked = roomShareObj.getBoolean("three")
                fourMem.isChecked = roomShareObj.getBoolean("four")

                if (res.has("food")){
                    when(res.getInt("food")){
                        2 -> foodTimesRadioGroup.check(R.id.twoTimes)
                        3 -> foodTimesRadioGroup.check(R.id.threeTimes)
                    }
                }



                val facilities = res.getJSONObject("facilities")

                hotwater.isChecked = facilities.getBoolean("hotwater")
                parking.isChecked = facilities.getBoolean("parkingspace")
                wifi.isChecked = facilities.getBoolean("wifi")
                cooking.isChecked = facilities.getBoolean("customcooking")
                wash.isChecked = facilities.getBoolean("washingmachine")
                gym.isChecked = facilities.getBoolean("gym")
                lift.isChecked = facilities.getBoolean("lift")
                chairs.isChecked = facilities.getBoolean("studychairs")
                tv.isChecked = facilities.getBoolean("smarttv")
                gaming.isChecked = facilities.getBoolean("gamingroom")
                cctv.isChecked =  facilities.getBoolean("cctv")
                dailycleaning.isChecked =  facilities.getBoolean("dailyroomcleaning")
                fridge.isChecked =  facilities.getBoolean("fridge")
                Ac.isChecked=facilities.getBoolean("ac")
                alternativecleaning.isChecked = facilities.getBoolean("alternativeroomcleaning")

                val rulesObj = res.getJSONObject("rules")
                nosomke.isChecked = rulesObj.getBoolean("nosmoking")
                noparties.isChecked = rulesObj.getBoolean("noparties")
                visitors.isChecked = rulesObj.getBoolean("novisitors")
            }

            override fun errorCallback(error_message: JSONObject) {
                Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                Toast.makeText(this@ProfileActivity, "Try again after sometime!", Toast.LENGTH_LONG).show()

            }

            override fun responseStatus(response_code: NetworkResponse?) {
                if (response_code != null) {
                    RES_CODE = response_code.statusCode
                }
                Log.d(TAG,"RESPONCE_CODE : "+response_code)
            }
        })
        pgid=intent.getStringExtra("_id").toString()
        Log.d("PRo",Constants.PROFILE_FETCH_URL+pgid.toString())
        request.getRequest(Constants.PROFILE_FETCH_URL+pgid.toString(),user.get("token").toString())
    }


    override fun onBackPressed() {
        val i = Intent(this@ProfileActivity, HomeActivity::class.java)
        i.putExtra("USER",user.toString())
        i.putExtra("_id",pgid.toString())
        startActivity(i)
        finish()
    }

}




