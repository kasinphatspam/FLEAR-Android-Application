package com.smartrefrig.flear.nearby

import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query

interface RetrofitMaps {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyBy_tQUR62cDJd-Xsb8-o01FEAih3lsFXw")
    fun getNearbyPlaces(@Query("type") type: String, @Query("location") location: String, @Query("radius") radius: Int): Call<Example>
}
