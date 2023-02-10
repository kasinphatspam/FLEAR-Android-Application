package com.smartrefrig.flear.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private var openNow: Boolean? = null
    @SerializedName("weekday_text")
    @Expose
    private var weekdayText: List<Any> = ArrayList()

    /**
     *
     * @return
     * The openNow
     */
    fun getOpenNow(): Boolean? {
        return openNow
    }

    /**
     *
     * @param openNow
     * The open_now
     */
    fun setOpenNow(openNow: Boolean?) {
        this.openNow = openNow
    }

    /**
     *
     * @return
     * The weekdayText
     */
    fun getWeekdayText(): List<Any> {
        return weekdayText
    }

    /**
     *
     * @param weekdayText
     * The weekday_text
     */
    fun setWeekdayText(weekdayText: List<Any>) {
        this.weekdayText = weekdayText
    }
}