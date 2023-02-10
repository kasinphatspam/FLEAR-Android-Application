package com.smartrefrig.flear.family

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import com.smartrefrig.flear.R
import com.smartrefrig.flear.method.FirebaseManager
import com.smartrefrig.flear.method.ToastMakeTextMethod
import kotlinx.android.synthetic.main.activity_create_new_family.*

class CreateNewFamilyActivity : AppCompatActivity() {

    lateinit var firebaseManager: FirebaseManager
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_new_family)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)


        init()
        setOnClickListener()
    }

    private fun init() {
        firebaseManager = FirebaseManager()
        toastMakeTextMethod = ToastMakeTextMethod()
    }

    private fun setOnClickListener() {
        confirmButton.setOnClickListener {
            if(checkTheNumberOfCharacters()) {
                val familyName = familyNameEditText.text.toString()
                val telephone = telephoneEditText.text.toString()
                val address = addressEditText.text.toString()
                val location = locationEditText.text.toString()

                firebaseManager.createNewFamily(this,familyName,telephone,address,location)
            }else{
                toastMakeTextMethod.theNumberOfCharactersIsNull(this)
            }
        }
        cancelImageButton.setOnClickListener {
            finish()
        }
        locationEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                if(checkTheNumberOfCharacters()) {
                    val familyName = familyNameEditText.text.toString()
                    val telephone = telephoneEditText.text.toString()
                    val address = addressEditText.text.toString()
                    val location = locationEditText.text.toString()

                    firebaseManager.createNewFamily(this,familyName,telephone,address,location)
                }else{
                    toastMakeTextMethod.theNumberOfCharactersIsNull(this)
                }
            }
            false
        }
    }

    private fun checkTheNumberOfCharacters() : Boolean {
        //Check the number of characters in your EditText
        return !(familyNameEditText.length() == 0
                && telephoneEditText.length() == 0
                && addressEditText.length() == 0
                && locationEditText.length() == 0)
    }
}
