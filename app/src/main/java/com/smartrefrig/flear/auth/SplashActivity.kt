package com.smartrefrig.flear.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.smartrefrig.flear.MainActivity
import com.smartrefrig.flear.R

class SplashActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth
    lateinit var mRef : DatabaseReference

    private val TAG = "checkPermission"
    private val REQUESTCODE_FINE_LOCATION = 101
    private val REQUESTCODE_COARSE_LOCATION = 102

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        setContentView(R.layout.activity_splash)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

    }

    private fun checkCurrentUser(){
        //Delay
        Handler().postDelayed({
            if(mAuth.currentUser != null){
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        // Get new Instance ID token
                        val token = task.result!!.token
                        mRef.child(mAuth.currentUser!!.uid).child("token").setValue(token)
                    })

                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
        }, 2000.toLong())
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user")
        setUpPermission()
    }

    private fun setUpPermission() {
        val findPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if ((coarsePermission != PackageManager.PERMISSION_GRANTED) && (findPermission != PackageManager.PERMISSION_GRANTED)) {
            Log.i(TAG, "Permission denied")
            requestPermission()

        } else if (coarsePermission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission denied")
            requestPermission()

        } else if (findPermission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission denied")
            requestPermission()

        } else {
            Log.i(TAG, "Permission granted")
            checkCurrentUser()
        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUESTCODE_COARSE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUESTCODE_FINE_LOCATION -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    checkCurrentUser()
                }
            }
            REQUESTCODE_COARSE_LOCATION -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUESTCODE_FINE_LOCATION)
                }
            }
        }
    }

}
