package com.smartrefrig.flear

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        showSoftKeyboard(searchEditText)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        setOnClickListener()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //...
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //...
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

    }

    private fun setOnClickListener() {
        backImageView.setOnClickListener {
            finish()
        }
        gasStationTextView.setOnClickListener {
            val output = Intent()
            output.putExtra("type", "gas_station")
            setResult(Activity.RESULT_OK, output)
            finish()
        }
        localParkingTextView.setOnClickListener {
            val output = Intent()
            output.putExtra("type", "parking")
            setResult(Activity.RESULT_OK, output)
            finish()
        }
        storeTextView.setOnClickListener {
            val output = Intent()
            output.putExtra("type", "store")
            setResult(Activity.RESULT_OK, output)
            finish()
        }
        restaurantTextView.setOnClickListener {
            val output = Intent()
            output.putExtra("type", "restaurant")
            setResult(Activity.RESULT_OK, output)
            finish()
        }
        cafeTextView.setOnClickListener {
            val output = Intent()
            output.putExtra("type", "cafe")
            setResult(Activity.RESULT_OK, output)
            finish()
        }
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
