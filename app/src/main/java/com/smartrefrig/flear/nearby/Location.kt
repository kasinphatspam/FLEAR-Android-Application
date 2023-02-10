package com.smartrefrig.flear.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Location {

    @SerializedName("lat")
    @Expose
    private var lat: Double? = null
    @SerializedName("lng")
    @Expose
    private var lng: Double? = null

    /**
     *
     * @return
     * The lat
     */
    fun getLat(): Double? {
        return lat
    }

    /**
     *
     * @param lat
     * The lat
     */
    fun setLat(lat: Double?) {
        this.lat = lat
    }

    /**
     *
     * @return
     * The lng
     */
    fun getLng(): Double? {
        return lng
    }

    /**
     *
     * @param lng
     * The lng
     */
    fun setLng(lng: Double?) {
        this.lng = lng
    }

}