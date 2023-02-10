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
import com.google.firebase.database.*
import com.smartrefrig.flear.R
import com.smartrefrig.flear.method.ToastMakeTextMethod
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.activity_edit_account.*

class EditAccountActivity : AppCompatActivity() {

    lateinit var toastMakeTextMethod : ToastMakeTextMethod
    lateinit var mRef : DatabaseReference
    lateinit var mAuth : FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_account)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)


        init()
        setDefaultDetails()
        setOnClickListener()
    }

    private fun setDefaultDetails() {
        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)
                telephoneEditText.setText(user.telephone.toString())
                nameEditText.setText(user.name.toString())
            }

        })
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        toastMakeTextMethod = ToastMakeTextMethod()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)
    }

    private fun setOnClickListener() {
        cancelImageButton.setOnClickListener {
            finish()
        }
        confirmButton.setOnClickListener{
            if(checkTheNumberOfCharacters()){
                val telephone = telephoneEditText.text.toString()
                val name = nameEditText.text.toString()

                //Send Value to firebase
                mRef.child("name").setValue(name)
                mRef.child("telephone").setValue(telephone)

                toastMakeTextMethod.editAccountComplete(this)
                finish()

            }else{
                toastMakeTextMethod.theNumberOfCharactersIsNull(this)
            }
        }

        telephoneEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                if(checkTheNumberOfCharacters()){
                    val telephone = telephoneEditText.text.toString()
                    val name = nameEditText.text.toString()

                    //Send Value to firebase
                    mRef.child("name").setValue(name)
                    mRef.child("telephone").setValue(telephone)

                    toastMakeTextMethod.editAccountComplete(this)
                    finish()
                }else{
                    toastMakeTextMethod.theNumberOfCharactersIsNull(this)
                }
            }
            false
        }
    }

    private fun checkTheNumberOfCharacters(): Boolean {
        return !(nameEditText.length() == 0 && telephoneEditText.length() == 0)
    }
}
