package com.smartrefrig.flear.auth

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.R
import com.smartrefrig.flear.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    lateinit var mRef : DatabaseReference
    lateinit var mAuth : FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //request window to use fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_profile)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        val userUid = intent.getStringExtra("userUid")

        init(userUid)
        setOnClickListener()

    }

    private fun setOnClickListener() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun init(userUid: String) {
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(userUid)

        getUserProfile(mRef)
    }

    private fun getUserProfile(mRef: DatabaseReference) {
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                try {
                    usernameTextView.text = user.name.toString()
                    if(user.introductory != "-"){
                        introductoryTextView.text = user.introductory.toString()
                    }
                    telephoneTextView.text = user.telephone.toString()
                    emailTextView.text = user.email.toString()

                    Picasso.get()
                        .load(user.userUrl)
                        .fit()
                        .centerCrop()
                        .into(userCircleImageView)

                    Picasso.get()
                        .load(user.background)
                        .fit()
                        .centerCrop()
                        .into(backgroundImageView)
                }catch (e : Exception){
                    //...
                }
            }

        })
    }
}
