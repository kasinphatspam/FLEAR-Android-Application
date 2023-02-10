package com.smartrefrig.flear.model

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class Cars : Serializable {

    var carName: String? = null
    var licensePlate: String? = null
    var brand: String? = null
    var key: String? = null
    var sensor: String? = null


    fun getCarDetails(dataSnapshot: DataSnapshot){
        val car : Cars = dataSnapshot.getValue(Cars::class.java) as Cars
        carName = car.carName
        licensePlate= car.licensePlate
        brand = car.brand
        key = car.key
        sensor = car.sensor
    }

    constructor(){}

    constructor (CarName : String , LicensePlate : String , Brand : String , Key : String, Sensor : String){
        carName = CarName
        licensePlate = LicensePlate
        brand = Brand
        key = Key
        sensor = Sensor
    }
}