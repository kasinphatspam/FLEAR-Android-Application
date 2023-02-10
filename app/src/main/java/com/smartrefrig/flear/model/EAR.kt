package com.smartrefrig.flear.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class EAR : Serializable{

    var date: String? = null
    var time: String? = null
    var ear: String? = null
    var key: String? = null

    fun getDetails(dataSnapshot: DataSnapshot){
        val earModel : EAR = dataSnapshot.getValue(EAR::class.java) as EAR
        date = earModel.date
        time= earModel.time
        ear = earModel.ear
        key = earModel.key
    }

    constructor(){}

    constructor (Date : String , Time : String , EAR : String , Key : String){
        date = Date
        time = Time
        ear = EAR
        key = Key
    }
}