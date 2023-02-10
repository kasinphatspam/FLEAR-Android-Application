package com.smartrefrig.flear.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Photo {

    @SerializedName("height")
    @Expose
    private var height: Int? = null
    @SerializedName("html_attributions")
    @Expose
    private var htmlAttributions: List<String> = ArrayList()
    @SerializedName("photo_reference")
    @Expose
    private var photoReference: String? = null
    @SerializedName("width")
    @Expose
    private var width: Int? = null

    /**
     *
     * @return
     * The height
     */
    fun getHeight(): Int? {
        return height
    }

    /**
     *
     * @param height
     * The height
     */
    fun setHeight(height: Int?) {
        this.height = height
    }

    /**
     *
     * @return
     * The htmlAttributions
     */
    fun getHtmlAttributions(): List<String> {
        return htmlAttributions
    }

    /**
     *
     * @param htmlAttributions
     * The html_attributions
     */
    fun setHtmlAttributions(htmlAttributions: List<String>) {
        this.htmlAttributions = htmlAttributions
    }

    /**
     *
     * @return
     * The photoReference
     */
    fun getPhotoReference(): String? {
        return photoReference
    }

    /**
     *
     * @param photoReference
     * The photo_reference
     */
    fun setPhotoReference(photoReference: String) {
        this.photoReference = photoReference
    }

    /**
     *
     * @return
     * The width
     */
    fun getWidth(): Int? {
        return width
    }

    /**
     *
     * @param width
     * The width
     */
    fun setWidth(width: Int?) {
        this.width = width
    }
}