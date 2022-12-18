package com.patel.tanmay.assignment_login.models

class PgNameModel {

    var id : String? = null
    var name : String? = null

    constructor(id: String, name: String) {
        this.id = id.toString()
        this.name = name.toString()
    }

}