package com.patel.tanmay.assignment_login.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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


class GrantAccessFromFragment(val token : String) : BottomSheetDialogFragment() {
    private lateinit var emailInput : TextInputEditText
    private lateinit var passwordInput : TextInputEditText
    private lateinit var allowButton : TextView
    private lateinit var cancelButton : TextView
    private lateinit var backButton : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_grant_access_from, container, false)
        emailInput = view.findViewById(R.id.evAccessEmail)
        passwordInput = view.findViewById(R.id.evAccessPass)
        allowButton = view.findViewById(R.id.btnAllowAccess)
        cancelButton = view.findViewById(R.id.btnCancel)
        backButton = view.findViewById(R.id.back_btn)

        cancelButton.setOnClickListener{
            this@GrantAccessFromFragment.dismiss()
        }

        backButton.setOnClickListener{
            this@GrantAccessFromFragment.dismiss()
        }


        allowButton.setOnClickListener{
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()
            if(email.length != 0 && pass.length != 0){
                val loadingDialog = context?.let { LoadingDialog(it) }
                loadingDialog?.setCancelable(false)
                loadingDialog?.show()

                val request = VolleyRequest(context, object :
                    CallBack {
                    override fun responseCallback(response: JSONObject) {
                        Toast.makeText(context, "Access Granted 👍", Toast.LENGTH_SHORT).show()
                        loadingDialog?.dismiss()
                        this@GrantAccessFromFragment.dismiss()

                    }

                    override fun errorCallback(error_message: JSONObject?) {
                        loadingDialog?.dismiss()
                        Toast.makeText(context, error_message?.getString("error"), Toast.LENGTH_LONG).show()
                    }


                    override fun responseStatus(response_code: NetworkResponse?) {

                    }
                })

                val bodyData = JSONObject()
                bodyData.put("email",email)
                bodyData.put("password",pass)


                    request.postWithBody(Constants.GIVE_ACCESS_TO_PG_ACC,bodyData,token)






            }else Toast.makeText(context, "Invalid credentials!", Toast.LENGTH_SHORT).show()
        }



    return view;
    }

}