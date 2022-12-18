package com.patel.tanmay.assignment_login.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.patel.tanmay.assignment_login.R
import com.patel.tanmay.assignment_login.adapter.PgNameAdapter
import com.patel.tanmay.assignment_login.models.PgNameModel

class SelectPg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_pg)

    }
}