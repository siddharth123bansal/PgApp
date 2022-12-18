package com.patel.tanmay.assignment_login.activities

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.adapter.PgNameAdapter
import com.patel.tanmay.assignment_login.models.PgNameModel
import org.json.JSONArray
import org.json.JSONObject

class PgnameLogin(context: Context,val lists:ArrayList<PgNameModel>,val email:String,val pass:String,val user: JSONObject,val rememberCB: CheckBox): Dialog(context) {

    lateinit var rec:RecyclerView
    lateinit var recadapter:PgNameAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pgname_login)
        rec=findViewById(R.id.PgnameRec)
        recadapter= PgNameAdapter(context,lists,email,pass,user,rememberCB)
        rec.adapter=recadapter
        rec.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)

    }
}