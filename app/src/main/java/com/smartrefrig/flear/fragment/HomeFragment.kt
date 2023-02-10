package com.smartrefrig.flear.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.NoInternetActivity
import com.smartrefrig.flear.R
import com.smartrefrig.flear.SearchActivity
import com.smartrefrig.flear.SettingsActivity
import com.smartrefrig.flear.family.HistoryActivity
import com.smartrefrig.flear.family.SettingHardwareActivity
import com.smartrefrig.flear.method.CrashLogManager
import com.smartrefrig.flear.method.DeviceLocationManager
import com.smartrefrig.flear.model.Cars
import com.smartrefrig.flear.model.Sensor
import com.smartrefrig.flear.model.User
import com.smartrefrig.flear.nearby.Example
import com.smartrefrig.flear.nearby.RetrofitMaps
import kotlinx.android.synthetic.main.bottom_sheet_maps.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment(),OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView : MapView
    private lateinit var line : Polyline
    private val PROXIMITY_RADIUS = 10000

    private var layoutBottomSheet: LinearLayout? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var mContext : Context
    private lateinit var myContext : FragmentActivity

    private lateinit var mRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mListener : ValueEventListener

    private lateinit var locationManager : LocationManager
    private lateinit var deviceLocationManager: DeviceLocationManager
    private lateinit var crashLogManager : CrashLogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContext = activity!!.applicationContext
        myContext = activity as FragmentActivity

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try{
            init(savedInstanceState, view)
            bottomSheetUserDetails()
            bottomSheetStateChanged()
            setOnClickListener()
            setSensorDatabase()
            setMarker()
        }catch (e : Exception){}

        if(!verifyAvailableNetwork(activity)){
            val intent = Intent(mContext,NoInternetActivity::class.java)
            startActivity(intent)
        }

        if (!checkGooglePlayServices()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.")
            activity.finish()
        }
        else {
            Log.d("onCreate", "Google Play Services available. Continuing.")
        }

    }

    private fun init(savedInstanceState: Bundle?,view: View) {
        mAuth = FirebaseAuth.getInstance()
        crashLogManager = CrashLogManager()

        mMapView = view.findViewById(R.id.map)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        layoutBottomSheet = view.findViewById(R.id.bottomSheetMaps)
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
        deviceLocationManager = DeviceLocationManager()

        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        MapsInitializer.initialize(mContext)
        mMap = googleMap!!

        if(isLocationEnabled(mContext)) {
            try {
                setMapStyle(googleMap)
                getLocation(mMap)

                mMap.setOnInfoWindowClickListener { marker ->
                    if(isLocationEnabled(mContext)) {

                        if(deviceLocationManager.getDeviceLocation(mContext,locationManager) != null){
                            val latLng = deviceLocationManager.getDeviceLocation(mContext,locationManager)
                            openGoogleMapDirectionMode(latLng!!,marker)
                        }
                    }
                }

            } catch (e: Resources.NotFoundException) {
                Log.e(TAG, "Can't find style. Error: ", e)
            }

        }else{
            Log.i("isPermissionCheck","Application can't get device location because this device turn off gps or cancel request permission")
        }

    }

    private fun setMarker() {

        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = User()
                user.getProfile(p0)

                mMap.clear()
                getCarsInFamily(user)

            }

        })
    }

    private fun getCarsInFamily(user: User) {
        val mRef2 = FirebaseDatabase.getInstance().reference.child("family")
            .child(user.family.toString()).child("cars")

        mRef2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(carsDataSnapshot: DataSnapshot) {
                mMap.clear()

                if(carsDataSnapshot.value != null) {
                    for (postSnapshot in carsDataSnapshot.children) {
                        val upload = postSnapshot.getValue(Cars::class.java)
                        getSensorLatLng(upload)
                    }
                }
            }

        })
    }

    private fun getSensorLatLng(upload: Cars?) {
        val mRef3 = FirebaseDatabase.getInstance().reference.child("sensor")
            .child(upload!!.sensor.toString())

        mRef3.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(sensorDataSnapshot: DataSnapshot) {
                if(sensorDataSnapshot.value != null) {
                    val sensor = Sensor()
                    sensor.getDetails(sensorDataSnapshot)
                    if (sensor.latitude != "-" && sensor.longitude != "-") {
                        val latLng = LatLng(
                            sensor.latitude!!.toDouble(),
                            sensor.longitude!!.toDouble()
                        )
                        setMarkerOfSearch(latLng, upload.carName.toString())
                    }
                }
            }

        })
    }

    private fun setMarkerOfSearch(latLng: LatLng,positionName : String) {
        try {
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title(positionName)
            markerOptions.icon(
                BitmapDescriptorFactory.fromBitmap(
                    changeDrawableSize()
                )
            )
            markerOptions.position
            mMap.addMarker(markerOptions)
        }catch (e : Exception){
            //...
        }
    }

    private fun setSensorDatabase() {

        mListener = mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                val mRef2 = FirebaseDatabase.getInstance().reference
                    .child("family")
                    .child(user.family.toString())
                    .child("cars")
                    .child(dataSnapshot.child("cars").value.toString())

                mRef2.addValueEventListener(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot2: DataSnapshot) {
                        if(dataSnapshot2.value != null){
                            if(dataSnapshot2.child("sensor").value != null){

                                val mRef3 = FirebaseDatabase.getInstance().reference
                                    .child("sensor")
                                    .child(dataSnapshot2.child("sensor").value.toString())

                                mRef3.addValueEventListener(object : ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {
                                        //...
                                    }

                                    @SuppressLint("SetTextI18n")
                                    override fun onDataChange(dataSnapshot3: DataSnapshot) {
                                        val sensor = Sensor()
                                        sensor.getDetails(dataSnapshot3)

                                        try {
                                            val heartRate : Int = sensor.heartRate!!.toInt()
                                            heartRateTextView.text = "Heart Rate : $heartRate"
                                            earTextView.text = "EAR : " + sensor.ear.toString()
                                            statusTextView.text = sensor.status
                                        }catch (e : Exception){ }
                                    }

                                })
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                })
            }
            override fun onCancelled(p0: DatabaseError) {
                //...
            }
        })
    }

    private fun setMapStyle(googleMap: GoogleMap?) {
        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        val success = googleMap!!.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                mContext, R.raw.mapstyle2
            )
        )

        if (!success) {
            Log.e(TAG, "Style parsing failed.")
        }
    }

    private fun openGoogleMapDirectionMode(finalLoc: Location,marker: Marker) {
        //Convert latLong
        val longitude = finalLoc.longitude
        val latitude = finalLoc.latitude

        val myLatLng = LatLng(latitude,longitude)
        val latLng = LatLng(marker.position.latitude, marker.position.longitude)

        //Open google map direction mode
        requestDirection(myLatLng,latLng)
        navigationButton.visibility = View.INVISIBLE

        //remove line in map view
        try {
            line.remove()
        }catch (e : Exception){}

    }

    @SuppressLint("MissingPermission")
    private fun setOnClickListener() {

        refreshImageView.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                refreshImageView.visibility = View.INVISIBLE
                refreshAvi.visibility = View.VISIBLE

                Handler().postDelayed({
                    setMarker()
                    if (deviceLocationManager.getDeviceLocation(
                            mContext,
                            locationManager
                        ) != null
                    ) {
                        val finalLoc =
                            deviceLocationManager.getDeviceLocation(mContext, locationManager)
                        val longitude = finalLoc!!.longitude
                        val latitude = finalLoc.latitude

                        try {
                            val latLng = LatLng(latitude, longitude)

                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                            )

                            refreshImageView.visibility = View.VISIBLE
                            refreshAvi.visibility = View.INVISIBLE

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    clearMarkerButton.visibility = View.INVISIBLE
                    navigationButton.visibility = View.INVISIBLE
                }, 2000)
            }
        }
        historyConstraintLayout.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val intent = Intent(mContext, HistoryActivity::class.java)
                startActivity(intent)
            }
        }
        menuImageView.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            listener?.onNavigationDrawerListener(true)
        }
        searchConstraintLayout.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val intent = Intent(mContext, SearchActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivityForResult(intent, 1)
            }
        }
        searchLocationConstraintLayout.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                if (isLocationEnabled(mContext)) {
                    if (deviceLocationManager.getDeviceLocation(
                            mContext,
                            locationManager
                        ) != null
                    ) {
                        val latLng =
                            deviceLocationManager.getDeviceLocation(mContext, locationManager)
                        queryNearbyParking(latLng!!)
                    }
                }
            }
        }
        settingConstraintLayout.setOnClickListener {
            val intent = Intent(mContext,SettingsActivity::class.java)
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            startActivity(intent)
        }
        settingHardWareConstraintLayout.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val user = User()
                        user.getProfile(p0)

                        if (p0.child("cars").value != null) {
                            val mRef2 = FirebaseDatabase.getInstance().reference.child("family")
                                .child(user.family.toString()).child("cars")
                                .child(p0.child("cars").value.toString())

                            mRef2.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    //...
                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.value != null) {
                                        val key = p0.child("cars").value.toString()
                                        val intent =
                                            Intent(mContext, SettingHardwareActivity::class.java)
                                        intent.putExtra("key", key)
                                        startActivity(intent)
                                    }else{
                                        Toast.makeText(mContext,"รถของคุณถูกลบไปแล้ว",Toast.LENGTH_SHORT).show()
                                    }
                                }

                            })
                        }else{
                            Toast.makeText(mContext,"คุณยังไม่ได้เลือกใช้งานรถในขณะนี้",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        clearMarkerButton.setOnClickListener {
            if(isLocationEnabled(mContext)) {
                if(deviceLocationManager.getDeviceLocation(mContext,locationManager) != null){
                    val latLng = deviceLocationManager.getDeviceLocation(mContext,locationManager)
                    clearMarkerOnMapView(latLng)
                }
            }
        }

        clearMarkerButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3

                if(event!!.action == MotionEvent.ACTION_UP) {
                    if(event.rawX >= (clearMarkerButton.right - clearMarkerButton.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {

                        if(isLocationEnabled(mContext)) {

                            if(deviceLocationManager.getDeviceLocation(mContext,locationManager) != null) {
                                val finalLoc = deviceLocationManager.getDeviceLocation(mContext,locationManager)
                                val latLng = LatLng(finalLoc!!.latitude,finalLoc.longitude)
                                removeDirectionLine(latLng)
                                clearMarkerButton.visibility = View.INVISIBLE
                                navigationButton.visibility = View.INVISIBLE
                            }
                        }
                        return true
                    }
                }
                return false
            }
        })

        navigationButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3

                if(event!!.action == MotionEvent.ACTION_UP) {
                    if(event.rawX >= (navigationButton.right - navigationButton.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        if(isLocationEnabled(mContext)) {
                            if(deviceLocationManager.getDeviceLocation(mContext,locationManager) != null) {
                                val finalLoc = deviceLocationManager.getDeviceLocation(mContext,locationManager)
                                val latLng = LatLng(finalLoc!!.latitude,finalLoc.longitude)
                                removeDirectionLine(latLng)
                                navigationButton.visibility = View.INVISIBLE
                                clearMarkerButton.visibility = View.INVISIBLE
                            }
                        }
                        return true
                    }
                }
                return false
            }
        })

        navigationButton.setOnClickListener {
            if(isLocationEnabled(mContext)) {

                if(deviceLocationManager.getDeviceLocation(mContext,locationManager) != null){
                    val finalLoc = deviceLocationManager.getDeviceLocation(mContext,locationManager)
                    val latLng = LatLng(finalLoc!!.latitude,finalLoc.longitude)
                    requestSearchCarDirectionMode(latLng)
                }
            }
        }

        searchMyCarConstraintLayout.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                if (isLocationEnabled(mContext)) {

                    if (deviceLocationManager.getDeviceLocation(mContext, locationManager) != null) {

                        val finalLoc = deviceLocationManager.getDeviceLocation(mContext, locationManager)
                        val latLng = LatLng(finalLoc!!.latitude, finalLoc.longitude)
                        searchCarsLocation(latLng)
                    }
                }
            }
        }
    }

    private fun searchCarsLocation(latLng: LatLng) {

        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = User()
                user.getProfile(p0)

                if (p0.child("cars").value != null) {

                    val mRef2 = FirebaseDatabase.getInstance().reference.child("family")
                        .child(user.family.toString()).child("cars")
                        .child(p0.child("cars").value.toString())

                    mRef2.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            //...
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.value != null) {
                                val cars = Cars()
                                cars.getCarDetails(dataSnapshot)

                                val mRef3 = FirebaseDatabase.getInstance().reference.child("sensor")
                                    .child(cars.sensor.toString())

                                mRef3.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        //...
                                    }

                                    override fun onDataChange(sensorDataSnapshot: DataSnapshot) {
                                        if (cars.sensor != null) {
                                            val sensor = Sensor()
                                            sensor.getDetails(sensorDataSnapshot)

                                            if (sensor.latitude != "-" && sensor.longitude != "-") {
                                                val latLng1 = LatLng(
                                                    sensor.latitude!!.toDouble(),
                                                    sensor.longitude!!.toDouble()
                                                )
                                                mMap.clear()

                                                try {
                                                    val markerOptions = MarkerOptions()
                                                    markerOptions.position(latLng1)
                                                    markerOptions.title(cars.carName)
                                                    markerOptions.icon(
                                                        BitmapDescriptorFactory.fromBitmap(
                                                            changeDrawableSize()
                                                        )
                                                    )
                                                    markerOptions.position
                                                    mMap.addMarker(markerOptions)

                                                    navigationButton.visibility = View.VISIBLE
                                                    clearMarkerButton.visibility = View.INVISIBLE
                                                } catch (e: Exception) {
                                                    //...
                                                }

                                                line = mMap.addPolyline(
                                                    PolylineOptions().geodesic(true).add(latLng).add(
                                                        latLng1
                                                    )
                                                )

                                                mMap.animateCamera(
                                                    CameraUpdateFactory.newLatLngZoom(latLng1, 15f)
                                                )
                                            } else {
                                                Toast.makeText(
                                                    mContext,
                                                    "ยังไม่ได้ติดตั้งอุปกรณ์",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                mContext,
                                                "ยังไม่ได้ติดตั้งอุปกรณ์",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                                })
                            } else {
                                Toast.makeText(mContext, "รถของคุณถูกลบไปแล้ว", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    })
                }else{
                    Toast.makeText(mContext, "คุณยังไม่ได้เลือกใช้งานรถในขณะนี้", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun requestSearchCarDirectionMode(latLng: LatLng) {

        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = User()
                user.getProfile(p0)

                val mRef2 = FirebaseDatabase.getInstance().reference.child("family")
                    .child(user.family.toString()).child("cars")
                    .child(p0.child("cars").value.toString())

                mRef2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != null) {
                            val cars = Cars()
                            cars.getCarDetails(dataSnapshot)

                            if (cars.sensor != null) {

                                val mRef3 = FirebaseDatabase.getInstance().reference.child("sensor")
                                    .child(cars.sensor.toString())

                                mRef3.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        //...
                                    }

                                    override fun onDataChange(sensorDataSnapshot: DataSnapshot) {
                                        if (sensorDataSnapshot.value != null) {
                                            val sensor = Sensor()
                                            sensor.getDetails(sensorDataSnapshot)

                                            if (sensor.latitude != "-" && sensor.longitude != "-") {
                                                val latLng1 = LatLng(
                                                    sensor.latitude!!.toDouble(),
                                                    sensor.longitude!!.toDouble()
                                                )
                                                try {
                                                    requestDirection(latLng, latLng1)
                                                    mMap.animateCamera(
                                                        CameraUpdateFactory.newLatLngZoom(
                                                            latLng,
                                                            15f
                                                        )
                                                    )
                                                } catch (e: Exception) {
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }

                })
            }
        })
    }

    private fun removeDirectionLine(latLng: LatLng) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        try {
            line.remove()
        }catch (e : Exception){
            //...
        }
    }

    private fun clearMarkerOnMapView(finalLoc: Location?) {

        val latLng = LatLng(finalLoc!!.latitude, finalLoc.longitude)

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        try {
            mMap.clear()
        }catch (e : Exception){
            //...
        }
        clearMarkerButton.visibility = View.INVISIBLE
    }

    private fun queryNearbyParking(finalLoc: Location) {

        clearMarkerButton.visibility = View.VISIBLE
        navigationButton.visibility = View.INVISIBLE
        val longitude = finalLoc.longitude
        val latitude = finalLoc.latitude
        build_retrofit_and_get_response("parking",latitude,longitude)

    }

    private fun bottomSheetUserDetails() {

        mListener = mRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val user = User()
                user.getProfile(dataSnapshot)
                try {
                    usernameTextView.text = user.name.toString()
                    introductoryTextView.text = user.introductory.toString()

                    Glide.with(mContext)
                        .load(user.userUrl)
                        .centerCrop()
                        .placeholder(R.color.gray_slide_bar)
                        .into(userCircleImageView)

                }catch (e : java.lang.Exception){
                    crashLogManager.piacssoError()
                }
            }

        })
    }

    private fun bottomSheetStateChanged() {
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                //...
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })
    }

    private fun checkGooglePlayServices() : Boolean{
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(mContext)
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result,
                        0).show()
            }
            return false
        }
        return true
    }

    private fun build_retrofit_and_get_response(type: String,latitude : Double , longitude : Double) {

        val url = "https://maps.googleapis.com/maps/"

        listener!!.onDialogLoadingListener(true)

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RetrofitMaps::class.java)

        val call = service.getNearbyPlaces(type, "$latitude,$longitude", PROXIMITY_RADIUS)

        call.enqueue(object : Callback<Example> {
            override fun onResponse(response: Response<Example>?, retrofit: Retrofit?) {
                try {
                    mMap.clear()

                    for (i in response!!.body()!!.results.indices) {
                        val lat = response.body()!!.results[i].getGeometry()!!.getLocation()!!.getLat()
                        val lng = response.body()!!.results[i].getGeometry()!!.getLocation()!!.getLng()
                        val placeName = response.body()!!.results[i].getName()
                        val vicinity = response.body()!!.results[i].getVicinity()
                        val markerOptions = MarkerOptions()
                        val latLng = LatLng(lat!!, lng!!)
                        markerOptions.position(latLng)
                        markerOptions.title("$placeName : $vicinity")

                        if(type == "parking"){
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    changeDrawableSizeParking()
                                )
                            )
                        }else if(type == "gas_station") {
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    changeDrawableSizeGasStation()
                                )
                            )
                        }else if(type == "store") {
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    changeDrawableSizeStore()
                                )
                            )
                        }else if(type == "restaurant") {
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    changeDrawableSizeRestaurant()
                                )
                            )
                        }else if(type == "cafe"){
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    changeDrawableSizeCafe()
                                )
                            )
                        }else{
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    changeDrawableSize()
                                )
                            )
                        }
                        mMap.addMarker(markerOptions)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))

                        if(i == response.body().results.lastIndex){
                            listener!!.onDialogLoadingListener(false)
                        }
                    }

                }catch (e : Exception){
                    //...
                }
            }

            override fun onFailure(t: Throwable?) {
                //...
            }

        })
    }

    private fun getLocation(mMap: GoogleMap) {

        try {
            if(isLocationEnabled(mContext)) {
                mMap.isMyLocationEnabled = true

                getLastLocation()
            }else{
                Log.i("isPermissionCheck","Application can't get device location because this device turn off gps or cancel request permission")
            }
        }catch (e : Exception){
            Log.i("HomeFragment","System can't get your location please check device permission of gps")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if(deviceLocationManager.getDeviceLocation(mContext,locationManager) != null){
            val finalLoc = deviceLocationManager.getDeviceLocation(mContext,locationManager)
            moveCameraToMyLocation(finalLoc!!)
        }
    }

    private fun moveCameraToMyLocation(finalLoc: Location) {
        val longitude = finalLoc.longitude
        val latitude = finalLoc.latitude

        try {
            val latLng = LatLng(latitude, longitude)

            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(latLng,15f)
            )

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
        fun onNavigationDrawerListener(boolean: Boolean)
        fun onDialogLoadingListener(boolean: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch(ex : Exception) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch(ex : Exception) {}

        if(!gps_enabled && !network_enabled) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return false
        }
        return true
    }

    private fun requestDirection(origin: LatLng,destination: LatLng) {
        val url = "http://maps.google.com/maps?saddr="+origin.latitude+","+origin.longitude+"&daddr="+destination.latitude+","+destination.longitude+"&mode=driving"
        val gmmIntentUri = Uri.parse(url)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mapIntent)
    }

    private fun changeDrawableSize() : Bitmap{
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.marker)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        return smallMarker
    }

    private fun changeDrawableSizeParking() : Bitmap{
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.parking)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        return smallMarker
    }

    private fun changeDrawableSizeGasStation() : Bitmap{
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.gas_station)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        return smallMarker
    }

    private fun changeDrawableSizeCafe() : Bitmap{
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.cafe)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        return smallMarker
    }

    private fun changeDrawableSizeRestaurant() : Bitmap{
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.restaurant)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        return smallMarker
    }

    private fun changeDrawableSizeStore() : Bitmap{
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.store)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        return smallMarker
    }

    private fun verifyAvailableNetwork(activity: Activity):Boolean{
        val connectivityManager= activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo= connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
        mRef.onDisconnect()
        mRef.removeEventListener(mListener)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val type = data.getStringExtra("type")
            if(isLocationEnabled(mContext)){
                if(deviceLocationManager.getDeviceLocation(mContext,locationManager) != null){
                    val finalLoc = deviceLocationManager.getDeviceLocation(mContext,locationManager)
                    clearMarkerButton.visibility = View.VISIBLE
                    build_retrofit_and_get_response(type,finalLoc!!.latitude,finalLoc.longitude)
                }
            }
        }
    }
}