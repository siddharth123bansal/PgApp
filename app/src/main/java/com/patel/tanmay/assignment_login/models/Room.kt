package com.patel.tanmay.assignment_login.models

import java.io.Serializable

class Room(val roomNumber : Int,
           val floorNumber : Int,
           val beds : Int,
           val rentPaidCount : Int,
           val roomID : String,
            val members : ArrayList<Member>)  {
}