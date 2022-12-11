package com.patel.tanmay.assignment_login

class Constants {
    companion object {


        val BASE_URL = "https://backend.pgconnect.in/api/"
        val DELETE_ROOM  = BASE_URL + "pgowner/deleteroom/"
        val ADD_USER_TO_ROOM  = BASE_URL + "pgowner/addusertoexistingroom/"
        val GIVE_ACCESS_TO_PG_ACC  = BASE_URL + "pgowner/pgapplyforaccess"
        val CHANGE_ROOM_USER  = BASE_URL + "pgowner/changeroomofuser"
        val ADD_NEW_ROOMS  = BASE_URL + "pgowner/addroom"
        val MODIFY_ROOM_DATA  = BASE_URL + "pgowner/modifyroom/"
        val CHANGE_COVER_IMAGE  = BASE_URL + "pgowner/changecoverimage"
        val CHANGE_PROFILE_IMAGE  = BASE_URL + "pgowner/changeprofileimage"
        val CHANGE_PROFILE  = BASE_URL + "pgowner/changeprofile"
        val GET_NOTIFICATIONS  = BASE_URL + "pgowner/getnotifications"
        val GET_USER_PROFILE_BY_ID  = BASE_URL + "pgowner/getprofileofuserbyid/"
        val ADD_DEVICE_TOKEN  = BASE_URL + "login/pgdevicetoken"
        val LOGIN_URL  = BASE_URL + "login/pg"
        val PROFILE_FETCH_URL  = BASE_URL + "pgowner/profile"
        val GET_ALL_USER = BASE_URL + "pgowner/getallusers"
        val GET_ROOMS_DATA  = BASE_URL + "pgowner/getrooms"
        val APPLY_FOR_APP_ACCESS  = BASE_URL + "pgowner/pgapplyforaccess"
        val REMOVE_USER_FROM_ROOM  = BASE_URL + "pgowner/removeuserfromroom"
        val PAY_RENT  = BASE_URL + "pgowner/payrent"
        val CHANGE_PASSWORD = BASE_URL + "user/changepassword"
        
      



    }
}