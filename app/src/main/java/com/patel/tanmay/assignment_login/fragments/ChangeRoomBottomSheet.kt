package com.patel.tanmay.assignment_login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.activities.LoadingDialog
import com.patel.tanmay.assignment_login.interfaces.CallBack
import org.json.JSONObject

class ChangeRoomBottomSheet(val token : String,val currentRoomNumber : String,val roomHash : JSONObject,val uid : String,val fetchMembers: () -> Unit) : BottomSheetDialogFragment(){
        private lateinit var oldNumber : TextInputEditText
         private lateinit var newNumber : TextInputEditText
         private lateinit var changeButton : TextView
         private lateinit var cancelButton : TextView
         private lateinit var backButton : ImageView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_change_room_bottom_sheet, container, false)
        oldNumber = view.findViewById(R.id.evOldRoomNumber)
        newNumber = view.findViewById(R.id.evNewRoomNo)
        changeButton = view.findViewById(R.id.btnAllowAccess)
        cancelButton = view.findViewById(R.id.btnCancel)
        backButton = view.findViewById(R.id.back_btn)

        oldNumber.setText("Old Room : "+currentRoomNumber)
        oldNumber.isEnabled = false

        cancelButton.setOnClickListener{
            this@ChangeRoomBottomSheet.dismiss()
        }

        backButton.setOnClickListener{
            this@ChangeRoomBottomSheet.dismiss()
        }

        changeButton.setOnClickListener{



            val newRoomNumber = newNumber.text.toString().trim()
            if(newRoomNumber.length !=0 && roomHash.has(newRoomNumber)){
                val loadingDialog = context?.let { LoadingDialog(it) }
                loadingDialog?.setCancelable(false)
                loadingDialog?.show()


                val request = VolleyRequest(context, object :
                    CallBack {
                    override fun responseCallback(response: JSONObject) {
                       fetchMembers()
                        loadingDialog?.dismiss()
                        this@ChangeRoomBottomSheet.dismiss()

                    }

                    override fun errorCallback(error_message: JSONObject?) {
                        loadingDialog?.dismiss()
                        Toast.makeText(context, error_message?.getString("error"), Toast.LENGTH_SHORT).show()
                    }


                    override fun responseStatus(response_code: NetworkResponse?) {

                    }
                })

                val bodyData = JSONObject()
                bodyData.put("userid",uid)
                bodyData.put("newroomid",roomHash.getString(newRoomNumber))
                bodyData.put("oldroomid",roomHash.getString(currentRoomNumber))


                    request.putWithBody(Constants.CHANGE_ROOM_USER,bodyData,token)





            }else Toast.makeText(context, "Enter a valid room number.", Toast.LENGTH_SHORT).show()
        }










    return view
    }

}