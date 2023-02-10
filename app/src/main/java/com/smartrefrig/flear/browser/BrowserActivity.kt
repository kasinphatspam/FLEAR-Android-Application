package com.smartrefrig.flear.browser

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.R
import com.smartrefrig.flear.model.Cars
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.activity_browser.*



class BrowserActivity : AppCompatActivity() {

    lateinit var mRef : DatabaseReference
    lateinit var mAuth : FirebaseAuth
    lateinit var webViewClient: WebViewClient

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //request window to use fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_browser)

        init()

        val key = intent.getStringExtra("key")

        if (isNetworkAvailable()) {
            mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

            mRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //...
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = User()
                    user.getProfile(p0)

                    val mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(user.family.toString()).child("cars").child(key)

                    mRef2.addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            //...
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.value != null) {
                                val cars = Cars()
                                cars.getCarDetails(dataSnapshot)

                                val uri = "https://www.${cars.brand.toString()}.com"
                                webview.loadUrl(uri)
                                uriTextView.text = uri
                                webview.webViewClient =
                                    WebViewClient()
                                webview.settings.javaScriptEnabled = true
                            }
                        }
                    })
                }

            })
        }else{
            Toast.makeText(
                this,
                "Internet Disconnected",
                Toast.LENGTH_LONG
            ).show()

        }

        setOnClickListener()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }

    private fun init() {
        webViewClient = WebViewClient()
        mAuth = FirebaseAuth.getInstance()
    }

    private fun setOnClickListener() {
        backButton.setOnClickListener {
            finish()
        }
    }
}
