package com.smartrefrig.flear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_alert_sleepy.*

class AlertSleepyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //request window to use fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_alert_sleepy)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        cancelImageButton.setOnClickListener {
            finish()
        }
        confirmButton.setOnClickListener {
            //(Go to main activity and direction)
        }
    }
}
