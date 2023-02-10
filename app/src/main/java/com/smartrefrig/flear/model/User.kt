package com.smartrefrig.flear.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class User : Serializable{

    var name: String? = null
    var email: String? = null
    var telephone: String? = null
    var userUrl: String? = null
    var userUid: String? = null
    var token: String? = null
    var introductory: String? = null
    var background: String? = null
    var family: String? = null

    fun getProfile (dataSnapshot: DataSnapshot){
        val user : User = dataSnapshot.getValue(User::class.java) as User
        name = user.name
        email= user.email
        telephone = user.telephone
        userUrl = user.userUrl
        userUid = user.userUid
        token = user.token
        introductory = user.introductory
        background = user.background
        family = user.family
    }

    constructor(){}

    constructor (Name : String , Email : String , Telephone : String , UserUrl : String , UserUid : String , Token : String , Introductory : String , Background : String , Family : String){
        name = Name
        email = Email
        telephone = Telephone
        userUid = UserUrl
        userUid = UserUid
        token = Token
        introductory = Introductory
        background = Background
        family = Family
    }
}