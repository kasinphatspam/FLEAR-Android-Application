package com.smartrefrig.flear.family

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.R
import com.smartrefrig.flear.method.ToastMakeTextMethod
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.activity_setting_hardware.*

class SettingHardwareActivity : AppCompatActivity() {

    lateinit var mRef : DatabaseReference
    lateinit var mRef2 : DatabaseReference
    lateinit var mRef3 : DatabaseReference
    lateinit var mAuth : FirebaseAuth
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_hardware)

        init()
        val key = intent.getStringExtra("key")

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        setOnClickListener(key)
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)
        toastMakeTextMethod = ToastMakeTextMethod()
    }

    private fun setOnClickListener(key : String) {
        cancelImageButton.setOnClickListener {
            finish()
        }
        passcodeEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                setPasscodeToFirebase(key)
            }
            false
        }
        confirmButton.setOnClickListener {
            setPasscodeToFirebase(key)
        }
    }

    private fun setPasscodeToFirebase(key: String) {
        if(passcodeEditText.length() != 0){

            val passcode= passcodeEditText.text.toString()

            mRef3 = FirebaseDatabase.getInstance().reference.child("sensor").child(passcode)

            mRef3.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //...
                }

                override fun onDataChange(dataSnapshot3: DataSnapshot) {

                    if(dataSnapshot3.value != null) {
                        mRef.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                //...
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val user = User()
                                user.getProfile(p0)

                                mRef2 = FirebaseDatabase.getInstance().reference.child("family")
                                    .child(user.family.toString())

                                mRef2.child("cars").child(key).child("sensor").setValue(passcode)
                                finish()
                            }

                        })
                    }else{
                        toastMakeTextMethod.cannotSetHardware(this@SettingHardwareActivity)
                    }
                }

            })

        }else{
            toastMakeTextMethod.theNumberOfCharactersIsNull(this)
        }
    }
}
