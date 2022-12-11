package com.patel.tanmay.assignment_login.models

import android.service.autofill.FillEventHistory
import java.time.Year

class Member(val name : String,
             val phone : String,
             val _id: String
             , val occupation : String,
             val email : String,
             val rentPaidHistory: ArrayList<MonthyRent>,
             val profileimage : String,
) {

    var rentPaid = false

    fun setRentPaid(month : Int, year: Int){
        for(rent in rentPaidHistory){
            if(rent.year == year && rent.month == month) rentPaid = rent.paid
        }
    }
}