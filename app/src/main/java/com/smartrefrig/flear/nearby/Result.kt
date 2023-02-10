package com.smartrefrig.flear.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Result {

    @SerializedName("geometry")
    @Expose
    private var geometry: Geometry? = null
    @SerializedName("icon")
    @Expose
    private var icon: String? = null
    @SerializedName("id")
    @Expose
    private var id: String? = null
    @SerializedName("name")
    @Expose
    private var name: String? = null
    @SerializedName("opening_hours")
    @Expose
    private var openingHours: OpeningHours? = null
    @SerializedName("photos")
    @Expose
    private var photos: List<Photo> = ArrayList<Photo>()
    @SerializedName("place_id")
    @Expose
    private var placeId: String? = null
    @SerializedName("rating")
    @Expose
    private var rating: Double? = null
    @SerializedName("reference")
    @Expose
    private var reference: String? = null
    @SerializedName("scope")
    @Expose
    private var scope: String? = null
    @SerializedName("types")
    @Expose
    private var types: List<String> = ArrayList()
    @SerializedName("vicinity")
    @Expose
    private var vicinity: String? = null
    @SerializedName("price_level")
    @Expose
    private var priceLevel: Int? = null

    /**
     *
     * @return
     * The geometry
     */
    fun getGeometry(): Geometry? {
        return geometry
    }

    /**
     *
     * @param geometry
     * The geometry
     */
    fun setGeometry(geometry: Geometry) {
        this.geometry = geometry
    }

    /**
     *
     * @return
     * The icon
     */
    fun getIcon(): String? {
        return icon
    }

    /**
     *
     * @param icon
     * The icon
     */
    fun setIcon(icon: String) {
        this.icon = icon
    }

    /**
     *
     * @return
     * The id
     */
    fun getId(): String? {
        return id
    }

    /**
     *
     * @param id
     * The id
     */
    fun setId(id: String) {
        this.id = id
    }

    /**
     *
     * @return
     * The name
     */
    fun getName(): String? {
        return name
    }

    /**
     *
     * @param name
     * The name
     */
    fun setName(name: String) {
        this.name = name
    }

    /**
     *
     * @return
     * The openingHours
     */
    fun getOpeningHours(): OpeningHours? {
        return openingHours
    }

    /**
     *
     * @param openingHours
     * The opening_hours
     */
    fun setOpeningHours(openingHours: OpeningHours) {
        this.openingHours = openingHours
    }

    /**
     *
     * @return
     * The photos
     */
    fun getPhotos(): List<Photo> {
        return photos
    }

    /**
     *
     * @param photos
     * The photos
     */
    fun setPhotos(photos: List<Photo>) {
        this.photos = photos
    }

    /**
     *
     * @return
     * The placeId
     */
    fun getPlaceId(): String? {
        return placeId
    }

    /**
     *
     * @param placeId
     * The place_id
     */
    fun setPlaceId(placeId: String) {
        this.placeId = placeId
    }

    /**
     *
     * @return
     * The rating
     */
    fun getRating(): Double? {
        return rating
    }

    /**
     *
     * @param rating
     * The rating
     */
    fun setRating(rating: Double?) {
        this.rating = rating
    }

    /**
     *
     * @return
     * The reference
     */
    fun getReference(): String? {
        return reference
    }

    /**
     *
     * @param reference
     * The reference
     */
    fun setReference(reference: String) {
        this.reference = reference
    }

    /**
     *
     * @return
     * The scope
     */
    fun getScope(): String? {
        return scope
    }

    /**
     *
     * @param scope
     * The scope
     */
    fun setScope(scope: String) {
        this.scope = scope
    }

    /**
     *
     * @return
     * The types
     */
    fun getTypes(): List<String> {
        return types
    }

    /**
     *
     * @param types
     * The types
     */
    fun setTypes(types: List<String>) {
        this.types = types
    }

    /**
     *
     * @return
     * The vicinity
     */
    fun getVicinity(): String? {
        return vicinity
    }

    /**
     *
     * @param vicinity
     * The vicinity
     */
    fun setVicinity(vicinity: String) {
        this.vicinity = vicinity
    }

    /**
     *
     * @return
     * The priceLevel
     */
    fun getPriceLevel(): Int? {
        return priceLevel
    }

    /**
     *
     * @param priceLevel
     * The price_level
     */
    fun setPriceLevel(priceLevel: Int?) {
        this.priceLevel = priceLevel
    }

}