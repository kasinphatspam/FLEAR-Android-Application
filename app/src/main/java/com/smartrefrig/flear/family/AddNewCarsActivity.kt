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
import com.smartrefrig.flear.R
import com.smartrefrig.flear.method.FirebaseManager
import com.smartrefrig.flear.method.ToastMakeTextMethod
import kotlinx.android.synthetic.main.activity_add_new_cars.*

class AddNewCarsActivity : AppCompatActivity() {

    lateinit var firebaseManager : FirebaseManager
    lateinit var toastMakeTextMethod: ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_new_cars)

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
        cancelImageButton.setOnClickListener{
            finish()
        }
        confirmButton.setOnClickListener {
            if(checkTheNumberOfCharacters()){
                val carName = carNameEditText.text.toString()
                val licensePlate = licensePlateEditText.text.toString()
                val brand = brandEditText.text.toString()

                firebaseManager.createNewCars(this,this,carName,licensePlate,brand)
            }else{
                toastMakeTextMethod.theNumberOfCharactersIsNull(this)
            }
        }
        licensePlateEditText.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i("EditText","Enter pressed")
                if(checkTheNumberOfCharacters()) {
                    val carName = carNameEditText.text.toString()
                    val licensePlate = licensePlateEditText.text.toString()
                    val brand = brandEditText.text.toString()

                    firebaseManager.createNewCars(this,this,carName,licensePlate,brand)
                }else{
                    toastMakeTextMethod.theNumberOfCharactersIsNull(this)
                }
            }
            false
        }
    }

    private fun checkTheNumberOfCharacters () : Boolean {

        return !(carNameEditText.length() == 0 && licensePlateEditText.length() == 0 && brandEditText.length() == 0)
    }
}
