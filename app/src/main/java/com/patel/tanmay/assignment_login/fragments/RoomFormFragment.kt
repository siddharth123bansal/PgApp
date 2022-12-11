package com.patel.tanmay.assignment_login.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.NetworkResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.activities.LoadingDialog
import com.patel.tanmay.assignment_login.interfaces.CallBack
import org.json.JSONObject


class RoomFormFragment(val token : String, val fetchData: () -> Unit,val roomID: String) : BottomSheetDialogFragment() {
    private lateinit var floorNumber : TextInputEditText
    private lateinit var roomNumber : TextInputEditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var addButton : TextView
    private lateinit var deleteRoom : TextView

    val TAG = "ALLROOM"
    var RES_CODE = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_room_form, container, false)

        floorNumber = view.findViewById(R.id.evFloorNo)
        roomNumber = view.findViewById(R.id.evRoomNo)
        radioGroup = view.findViewById(R.id.bedRadioGroup)
        addButton = view.findViewById(R.id.btnAllowAccess)
        deleteRoom = view.findViewById(R.id.deleteRoomButton);

        if(arguments != null){
            addButton.text = "Update"
            deleteRoom.visibility = View.VISIBLE

        }
        else{
            addButton.text = "Add"
            deleteRoom.visibility = View.GONE
        }



        
        if(arguments?.isEmpty != true){

            val floor = arguments?.getString("floorNumber")
            val room = arguments?.getString("roomNumber")
            val beds = arguments?.getString("beds")
            when(beds){
                "1" -> radioGroup.check(R.id.bed1)
                "2" -> radioGroup.check(R.id.bed2)
                "3" -> radioGroup.check(R.id.bed3)
                "4" -> radioGroup.check(R.id.bed4)
                "5" -> radioGroup.check(R.id.bed5)
            }
            floorNumber.setText(floor)
            roomNumber.setText(room)

        }

        deleteRoom.setOnClickListener{
            val loadingDialog = context?.let { LoadingDialog(it) }
            loadingDialog?.setCancelable(false)
            loadingDialog?.show()

            val request = VolleyRequest(context, object :
                CallBack {
                override fun responseCallback(response: JSONObject) {
                    fetchData()
                    loadingDialog?.dismiss()
                    this@RoomFormFragment.dismiss()

                }

                override fun errorCallback(error_message: JSONObject?) {
                    loadingDialog?.dismiss()
                    Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                    Toast.makeText(context, "Try again after sometime!", Toast.LENGTH_LONG).show()
                }


                override fun responseStatus(response_code: NetworkResponse?) {
                    if (response_code != null) {
                        RES_CODE = response_code.statusCode
                    }
                    Log.d(TAG,"RESPONCE_CODE : "+response_code)
                }
            })

            val URL_DELETE_ROOM = Constants.DELETE_ROOM+roomID
            request.deleteRequest(URL_DELETE_ROOM,token)

        }



        view.findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                this@RoomFormFragment.dismiss()
            }
        })

        view.findViewById<TextView>(R.id.btnCancel).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                this@RoomFormFragment.dismiss()
            }
        })


        addButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                
                val roomNo = roomNumber.text.toString().trim()
                val floorNo = floorNumber.text.toString().trim()
                var bed = 0
                when(radioGroup.checkedRadioButtonId){
                    R.id.bed1 -> bed = 1
                    R.id.bed2 -> bed = 2
                    R.id.bed3 -> bed = 3
                    R.id.bed4 -> bed = 4
                    R.id.bed5 -> bed = 5
                }
                if(floorNo.length != 0 && roomNo.length !=0 && bed != 0){
                    val loadingDialog = context?.let { LoadingDialog(it) }
                    loadingDialog?.setCancelable(false)
                    loadingDialog?.show()

                    val request = VolleyRequest(context, object :
                        CallBack {
                        override fun responseCallback(response: JSONObject) {
                            fetchData()
                            loadingDialog?.dismiss()
                            this@RoomFormFragment.dismiss()

                        }

                        override fun errorCallback(error_message: JSONObject?) {
                            loadingDialog?.dismiss()
                            Log.d(TAG,"ERROR : ->  "+error_message?.toString())
                            Toast.makeText(context, "Try again after sometime!", Toast.LENGTH_LONG).show()
                        }


                        override fun responseStatus(response_code: NetworkResponse?) {
                            if (response_code != null) {
                                RES_CODE = response_code.statusCode
                            }
                            Log.d(TAG,"RESPONCE_CODE : "+response_code)
                        }
                    })

                    val bodyData = JSONObject()
                    bodyData.put("roomnumber",roomNo)
                    bodyData.put("floornumber",floorNo)
                    bodyData.put("beds",bed.toString())



                    if(arguments == null){
                        request.postWithBody(Constants.ADD_NEW_ROOMS,bodyData,token)
                    }else {
                        val URL_UPDATE_ROOM = Constants.MODIFY_ROOM_DATA+roomID
                        request.putWithBody(URL_UPDATE_ROOM,bodyData,token)
                    }


                    
                }else Toast.makeText(context, "Invalid credentials!", Toast.LENGTH_SHORT).show()
    


            }
        })

        return view
    }


}