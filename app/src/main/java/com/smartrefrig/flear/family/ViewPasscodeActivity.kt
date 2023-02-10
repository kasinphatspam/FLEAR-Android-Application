package com.smartrefrig.flear.family

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.R
import com.smartrefrig.flear.method.ToastMakeTextMethod
import com.smartrefrig.flear.model.Family
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.activity_view_passcode.*


class ViewPasscodeActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference
    lateinit var mRef2: DatabaseReference
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_passcode)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        init()


        setOnClickListener()
        getUserProfile()
    }

    private fun getUserProfile() {
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)
                getFamilyDetails(user.family)
            }

            private fun getFamilyDetails(family: String?) {
                mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(family.toString())

                mRef2.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                    override fun onDataChange(dataSnapshot2: DataSnapshot) {
                        val familyModel = Family()
                        familyModel.getDetails(dataSnapshot2)
                        passcodeTextView.text = familyModel.key.toString()
                        familyNameTextView.text = familyModel.familyName.toString()
                    }

                })
            }

        })
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)
        toastMakeTextMethod = ToastMakeTextMethod()
    }

    private fun setOnClickListener() {
        cancelImageButton.setOnClickListener {
            finish()
        }
        copyTextView.setOnClickListener {
            val text = passcodeTextView.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copy Text:", text)
            clipboard.primaryClip = clip
            toastMakeTextMethod.copyClipBoardPasscode(this)
        }
        passwordTextView.setOnClickListener {

        }
    }
}
