package com.smartrefrig.flear.family

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.smartrefrig.flear.method.FirebaseManager
import com.smartrefrig.flear.method.ToastMakeTextMethod
import kotlinx.android.synthetic.main.activity_join_family.*



class JoinFamilyActivity : AppCompatActivity() {

    lateinit var firebaseManager: FirebaseManager
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(com.smartrefrig.flear.R.layout.activity_join_family)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, com.smartrefrig.flear.R.color.white)


        init()
        setOnClickListener()
    }

    private fun init() {
        toastMakeTextMethod = ToastMakeTextMethod()
        firebaseManager = FirebaseManager()
        passcodeEditText.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
    }

    private fun setOnClickListener() {
        confirmButton.setOnClickListener {
            if(passcodeEditText.length() != 0){
                firebaseManager.joinFamily(this,passcodeEditText.text.toString())
            }else{
                toastMakeTextMethod.theNumberOfCharactersIsNull(this)
            }
        }
        cancelImageButton.setOnClickListener {
            finish()
        }
        passcodeEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                if(passcodeEditText.length() != 0){
                    firebaseManager.joinFamily(this,passcodeEditText.text.toString())
                }else{
                    toastMakeTextMethod.theNumberOfCharactersIsNull(this)
                }
            }
            false
        }
    }
}
