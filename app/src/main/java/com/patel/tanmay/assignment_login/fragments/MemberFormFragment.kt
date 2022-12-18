package com.patel.tanmay.assignment_login.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyMultipartRequest
import com.patel.tanmay.assignment_login.activities.LoadingDialog
import kotlinx.android.synthetic.main.fragment_member_form.*
import kotlinx.android.synthetic.main.fragment_member_form.view.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.security.spec.PSSParameterSpec.DEFAULT
import java.util.*


class MemberFormFragment(val token : String,val roomID :String,val fetchRoomMembers: () -> Unit) : BottomSheetDialogFragment() {
    lateinit var memberName : TextInputEditText
    lateinit var memberPhone : TextInputEditText
    lateinit var memberOccupation : TextInputEditText
    lateinit var rentAmount : TextInputEditText
    lateinit var advRentAmt : TextInputEditText
    lateinit var doj : TextInputEditText
    lateinit var loca : TextInputEditText
    lateinit var genRG: RadioGroup
    lateinit var addBtn : TextView
    lateinit var encodeImageString: String
    lateinit var memberEmail : TextInputEditText
    lateinit var loadingDialog: LoadingDialog
    lateinit var bitmap: Bitmap
    val TAG ="ADDMEMB"
    var RES_CODE = -1
    private var gender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_member_form, container, false)
        memberName = view.findViewById(R.id.evMemberName)
        memberPhone = view.findViewById(R.id.evMemberPhone)
        memberOccupation = view.findViewById(R.id.evMemberOccupation)
        //browseBtn = view.findViewById(R.id.browseBtn)
        memberEmail = view.findViewById(R.id.evMemberEmail)
        addBtn = view.findViewById(R.id.btnAllowAccess)
        genRG = view.findViewById(R.id.genderRadioGroup)
        rentAmount = view.findViewById(R.id.evMemberRentAmt)
        advRentAmt  = view.evMemberAdvRentAmt
        doj=view.findViewById(R.id.evMemberdateofjoin)
        loca=view.findViewById(R.id.evMemberlocation)
        val c=Calendar.getInstance()
        val y=c.get(Calendar.YEAR)
        var m=c.get(Calendar.MONTH)
        val da=c.get(Calendar.DAY_OF_MONTH)
       // Toast.makeText(context, (""+y+"/"+m+"/"+da).toString(), Toast.LENGTH_LONG).show()
        encodeImageString=""
        view.findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                this@MemberFormFragment.dismiss()
            }
        })
            doj.setOnClickListener {
            val pdp = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, iy, im, id ->
                val mo=im+1
                doj.setText(""+iy+"/"+mo+"/"+id)
                //Toast.makeText(this@HomeActivity,(""+finalcount+""+totalMembers.toString()),Toast.LENGTH_LONG).show()

            },y,m,da)
                pdp.datePicker.minDate=c.timeInMillis
            pdp.show()

        }

        view.findViewById<TextView>(R.id.btnCancel).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                this@MemberFormFragment.dismiss()
            }
        })

        addBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val name = memberName.text.toString().trim()
                val phone = memberPhone.text.toString().trim()
                val occupation = memberOccupation.text.toString().trim()
                val rent = rentAmount.text.toString().trim()
                val advRent = advRentAmt.text.toString().trim()
                val dateofj =evMemberdateofjoin.text.toString().trim()
                val loc=evMemberlocation.text.toString().trim()

                when(genRG.checkedRadioButtonId){
                    R.id.rbtnMale -> gender = "Male"
                    R.id.rbtnFemale -> gender = "Female"
                }

                if(name.length != 0 &&
                    phone.length != 0 &&
                    occupation.length != 0 &&
                    gender.length != 0 &&
                      rent.length != 0 &&
                            dateofj.length!=0 &&
                            loc.length!=0){
                    if (::loadingDialog.isInitialized) {Toast.makeText(context, "Yes loading initialized", Toast.LENGTH_SHORT).show()}

                    loadingDialog = context?.let { LoadingDialog(it) }!!
                    loadingDialog?.setCancelable(false)
                    loadingDialog?.show()

                    uploadBitmap()



//                    val URL_ADD_MEM = "https://pg-app-backend.herokuapp.com/api/pgowner/addusertoexistingroom/"+token
//                    request.postWithBody(URL_ADD_MEM,bodyData,token)

                }else Toast.makeText(context, "Invalid credentials!", Toast.LENGTH_SHORT).show()

            }
        })

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            Log.d("IMG","HI on result")
            val filePath =  data?.data as Uri
            try {

               val  inputStream = context?.contentResolver?.openInputStream(filePath)
                if (::bitmap.isInitialized) {bitmap = BitmapFactory.decodeStream(inputStream)
                }
                else{
                    Toast.makeText(context, " No bitmap also", Toast.LENGTH_SHORT).show()
                }

//                encodeBitmapImage(bitmap)

            }catch (e : Exception){
                Log.d("IMG","HI on BITMAP ERROR")
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show()
            }

        }

    }
//
//    private fun encodeBitmapImage(bitmap: Bitmap?) {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
//
//        val bytesOfImg = byteArrayOutputStream.toByteArray()
//        encodeImageString = android.util.Base64.encodeToString(bytesOfImg,Base64.DEFAULT)
//        Log.d("IMG",encodeImageString)
//
//    }
    private fun uploadBitmap() {
        val URL = Constants.ADD_USER_TO_ROOM+roomID
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, URL,
            object : Response.Listener<NetworkResponse?> {
                override fun onResponse(response: NetworkResponse?) {
                    Log.d(TAG,"Upload success")
                    Log.d(TAG,response.toString())
                    fetchRoomMembers()
                    this@MemberFormFragment.dismiss()
                    loadingDialog.cancel()

                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    loadingDialog.cancel()
                    val networkResponse = error.networkResponse
                    if (networkResponse != null && networkResponse.data != null) {
                       val jsonError =String(networkResponse.data)
                        Toast.makeText(context, "User already exits or try reducing file size.", Toast.LENGTH_LONG).show()
                    }


                    error.printStackTrace()
                }
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params.put("email",memberEmail.text.toString())
                params.put("phone",memberPhone.text.toString())
                params.put("occupation",memberOccupation.text.toString())
                params.put("gender",gender)
                params.put("name",memberName.text.toString())
                params.put("rent",rentAmount.text.toString())
                params.put("advance",advRentAmt.text.toString())
                params.put("dateofjoining",doj.text.toString())
                params.put("location",loca.text.toString())
                Log.d("IMG","HI on VALUE_PARMS")
                return params
            }
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                val imagename = System.currentTimeMillis()
                Log.d("IMG","HI on IMAGE_PARMS")
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = java.util.HashMap()
                headers["Authorization"] = "Bearer $token"
                Log.d("IMG","HI on HEADERS_PARMS")
                return headers
            }
        }

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(
            DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        Volley.newRequestQueue(context).add(volleyMultipartRequest)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }


}