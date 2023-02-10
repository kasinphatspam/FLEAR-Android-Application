package com.smartrefrig.flear.auth

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.smartrefrig.flear.R
import com.smartrefrig.flear.method.ToastMakeTextMethod
import kotlinx.android.synthetic.main.activity_edit_introductory.*

class EditIntroductoryActivity : AppCompatActivity() {

    lateinit var mRef : DatabaseReference
    lateinit var mAuth : FirebaseAuth
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_introductory)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        init()
        setOnClickListener()
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
        confirmButton.setOnClickListener {
            if(textEditText.length() != 0) {
                mRef.child("introductory").setValue(textEditText.text.toString())
                finish()
            }else{
                toastMakeTextMethod.theNumberOfCharactersIsNull(this)
            }
        }
        textEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                if(textEditText.length() != 0) {
                    mRef.child("introductory").setValue(textEditText.text.toString())
                    finish()
                }else{
                    toastMakeTextMethod.theNumberOfCharactersIsNull(this)
                }
            }
            false
        }
    }
}
