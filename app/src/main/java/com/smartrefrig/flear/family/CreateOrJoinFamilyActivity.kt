package com.smartrefrig.flear.family

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.smartrefrig.flear.R
import kotlinx.android.synthetic.main.activity_create_or_join_family.*

class CreateOrJoinFamilyActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_or_join_family)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)


        setOnClickListener()
    }

    private fun setOnClickListener() {
        joinFamilyCardView.setOnClickListener {
            val intent = Intent(this, JoinFamilyActivity::class.java)
            startActivity(intent)
        }

        createNewFamilyCardView.setOnClickListener {
            val intent = Intent(this, CreateNewFamilyActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        //Block onBackPressed
    }
}
