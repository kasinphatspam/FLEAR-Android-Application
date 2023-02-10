package com.smartrefrig.flear.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class Sensor : Serializable {

    var ear: String? = null
    var heartRate: String? = null
    var latitude : String? = null
    var longitude : String? = null
    var status: String? = null

    fun getDetails(dataSnapshot: DataSnapshot){
        val car : Sensor = dataSnapshot.getValue(Sensor::class.java) as Sensor
        ear = car.ear
        heartRate= car.heartRate
        latitude = car.latitude
        longitude = car.longitude
        status = car.status
    }

    constructor(){}

    constructor (Ear : String , HeartRate : String , Latitude : String , Longitude : String, Status : String ){
        ear = Ear
        heartRate = HeartRate
        latitude = Latitude
        longitude = Longitude
        status = Status
    }
}