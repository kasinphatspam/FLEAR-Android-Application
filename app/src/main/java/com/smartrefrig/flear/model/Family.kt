package com.smartrefrig.flear.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class Family : Serializable{

    var familyName: String? = null
    var address: String? = null
    var telephone: String? = null
    var location: String? = null
    var key: String? = null

    fun getDetails (dataSnapshot: DataSnapshot){
        val user : Family = dataSnapshot.getValue(Family::class.java) as Family
        familyName = user.familyName
        address= user.address
        telephone = user.telephone
        location = user.location
        key = user.key
    }

    constructor(){}

    constructor (FamilyName : String , Address : String , Telephone : String , Location : String , Key : String){
        familyName = FamilyName
        address = Address
        telephone = Telephone
        location = Location
        key = Key
    }
}