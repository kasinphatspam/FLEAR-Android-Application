package com.smartrefrig.flear.method

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

open class DeviceLocationManager {

    var gps_enabled = false
    var network_enabled = false

    var net_loc: Location? = null
    var gps_loc: Location? = null
    var finalLoc: Location? = null

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(context: Context, locationManager : LocationManager) : Location? {

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (gps_enabled) {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (network_enabled) {
            net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        if (gps_loc != null && net_loc != null) {

            if (gps_loc!!.accuracy > net_loc!!.accuracy) {
                finalLoc = net_loc

                return finalLoc!!
            } else {
                finalLoc = gps_loc

                return finalLoc!!
            }

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc

                return finalLoc!!

            } else if (net_loc != null) {
                finalLoc = net_loc

                return finalLoc!!
            }
        }
        return null
    }
}