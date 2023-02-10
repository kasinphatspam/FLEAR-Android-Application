package com.smartrefrig.flear.auth

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import com.smartrefrig.flear.R
import com.smartrefrig.flear.method.AuthMethod
import com.smartrefrig.flear.method.ToastMakeTextMethod
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    lateinit var authMethod : AuthMethod
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_account)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackGray2)


        init()
        setOnClickListener()

    }

    private fun setOnClickListener() {
        confirmButton.setOnClickListener {
            if(checkTheNumberOfCharacters()){
                //EditText is non-null can do somethings
                authMethod.createUserAccount(this,this,emailEditText,passwordEditText,nameEditText,telephoneEditText)
            }else{
                //EditText is null can do somethings
                toastMakeTextMethod.cannotCreateYourAccount(this)
            }
        }
        cancelImageButton.setOnClickListener {
            finish()
        }
        telephoneEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                if(checkTheNumberOfCharacters()){
                    //EditText is non-null can do somethings
                    authMethod.createUserAccount(this,this,emailEditText,passwordEditText,nameEditText,telephoneEditText)
                }else{
                    //EditText is null can do somethings
                    toastMakeTextMethod.cannotCreateYourAccount(this)
                }
            }
            false
        }
    }

    private fun init() {
        authMethod = AuthMethod()
        toastMakeTextMethod = ToastMakeTextMethod()
        FirebaseApp.initializeApp(this)
    }

    private fun checkTheNumberOfCharacters() : Boolean {
        //Check the number of characters in your EditText
        return !(emailEditText.length() == 0
                && passwordEditText.length() == 0
                && nameEditText.length() == 0
                && telephoneEditText.length() == 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}
