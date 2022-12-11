package com.patel.tanmay.assignment_login.models

class MonthyRent(val month : Int, val year : Int, val paid : Boolean) {

    override fun toString(): String {
        return "m : "+month+" y "+year+" paid "+paid
    }
}