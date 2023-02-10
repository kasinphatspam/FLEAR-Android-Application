package com.smartrefrig.flear.auth

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var authMethod: AuthMethod
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackGray2)

        init()
        setOnClickListener()
    }

    private fun init() {
        authMethod = AuthMethod()
        toastMakeTextMethod = ToastMakeTextMethod()
        FirebaseApp.initializeApp(this)
    }

    private fun setOnClickListener() {
        confirmButton.setOnClickListener {
            if(checkTheNumberOfCharacters()){
                authMethod.loginWithEmailAndPassword(this,this,emailEditText,passwordEditText)
            }else{
                toastMakeTextMethod.theNumberOfCharactersIsNull(this)
            }
        }
        createAccountTextView.setOnClickListener {
            val intent = Intent(this,CreateAccountActivity::class.java)
            startActivity(intent)
        }

        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                if(checkTheNumberOfCharacters()){
                    authMethod.loginWithEmailAndPassword(this,this,emailEditText,passwordEditText)
                }else{
                    toastMakeTextMethod.theNumberOfCharactersIsNull(this)
                }
            }
            false
        }
    }

    private fun checkTheNumberOfCharacters () : Boolean{
        return !(emailEditText.length() == 0 && passwordEditText.length() == 0)
    }
}
