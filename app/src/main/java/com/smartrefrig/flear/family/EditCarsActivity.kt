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
import com.smartrefrig.flear.method.FirebaseManager
import com.smartrefrig.flear.method.ToastMakeTextMethod
import com.smartrefrig.flear.model.Cars
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.activity_edit_cars.*

class EditCarsActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mRef : DatabaseReference
    private lateinit var mRef2 : DatabaseReference
    private lateinit var firebaseManager : FirebaseManager
    private lateinit var toastMakeTextMethod : ToastMakeTextMethod

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_cars)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        val key = intent.getStringExtra("key")

        init()
        setDefaultCarsValue(key)
        setOnClickListener(key)
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        firebaseManager = FirebaseManager()
        toastMakeTextMethod = ToastMakeTextMethod()
    }

    private fun setDefaultCarsValue(key: String) {

        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(userSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(userSnapshot)

                mRef2 = FirebaseDatabase.getInstance().reference
                    .child("family")
                    .child(user.family.toString())
                    .child("cars")
                    .child(key)

                mRef2.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                    override fun onDataChange(carSnapshot: DataSnapshot) {
                        if(carSnapshot.value != null){
                            val cars = Cars()
                            cars.getCarDetails(carSnapshot)

                            carNameEditText.setText(cars.carName.toString())
                            brandEditText.setText(cars.brand.toString())
                            licensePlateEditText.setText(cars.licensePlate.toString())
                        }
                    }

                })
            }

        })

    }

    private fun setOnClickListener(key: String) {
        cancelImageButton.setOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            if(checkTheNumberOfCharacters()){
                val carsName = carNameEditText.text.toString()
                val brand = brandEditText.text.toString()
                val licensePlate = licensePlateEditText.text.toString()

                firebaseManager.editCars(this,this,carsName,licensePlate,brand,key)

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

                    firebaseManager.editCars(this,this,carName,licensePlate,brand,key)
                }else{
                    toastMakeTextMethod.theNumberOfCharactersIsNull(this)
                }
            }
            false
        }
    }

    private fun checkTheNumberOfCharacters(): Boolean{
        return (carNameEditText.length() != 0 && brandEditText.length() != 0 && licensePlateEditText.length() != 0)
    }
}
