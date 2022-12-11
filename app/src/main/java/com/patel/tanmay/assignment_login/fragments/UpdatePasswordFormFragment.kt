package com.patel.tanmay.assignment_login.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.patel.tanmay.assignment_login.Constants
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.VolleyRequest
import com.patel.tanmay.assignment_login.activities.LoadingDialog
import com.patel.tanmay.assignment_login.interfaces.CallBack
import kotlinx.android.synthetic.main.change_pass_fragment.*
import kotlinx.android.synthetic.main.fragment_room_form.*
import org.json.JSONObject


class UpdatePasswordFormFragment(val token : String) : BottomSheetDialogFragment() {
    private lateinit var addButton : TextView

    val TAG = "ALLROOM"
    var RES_CODE = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.change_pass_fragment, container, false)

        addButton = view.findViewById(R.id.btnAdd)


        view.findViewById<ImageView>(R.id.back_btn).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                this@UpdatePasswordFormFragment.dismiss()
            }
        })

        addButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                
                val oldPassword = evOldPassword.text.toString().trim()
                val newPassword = evNewPassword.text.toString().trim()
                val confirmNewPassword = evConfirmNewPassword.text.toString().trim()

                if(oldPassword.length != 0 && newPassword.length !=0 && confirmNewPassword.length != 0){

                    if(newPassword.equals(confirmNewPassword)){
                        val loadingDialog = context?.let { LoadingDialog(it) }
                        loadingDialog?.setCancelable(false)
                        loadingDialog?.show()

                        val request = VolleyRequest(context, object :
                            CallBack {
                            override fun responseCallback(response: JSONObject) {
                                loadingDialog?.dismiss()
                                Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show()
                                this@UpdatePasswordFormFragment.dismiss()

                            }

                            override fun errorCallback(error_message: JSONObject?) {
                                if (loadingDialog != null) {
                                    loadingDialog.cancel()
                                }
                                if (error_message != null) {
                                    Toast.makeText(context, error_message.getString("error"), Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun responseStatus(response_code: NetworkResponse?) {

                            }
                            })

                        val bodyData = JSONObject()
                        bodyData.put("oldpassword",oldPassword)
                        bodyData.put("password",newPassword)

                        request.putWithBody(Constants.CHANGE_PASSWORD,bodyData,token)


                    }else Toast.makeText(context, "Password mismatch!", Toast.LENGTH_SHORT).show()


                    
                }
    


            }
        })

        return view
    }


}