package com.smartrefrig.flear.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geometry {

    @SerializedName("location")
    @Expose
    private var location: Location? = null

    /**
     *
     * @return
     * The location
     */
    fun getLocation(): Location? {
        return location
    }

    /**
     *
     * @param location
     * The location
     */
    fun setLocation(location: Location) {
        this.location = location
    }
}