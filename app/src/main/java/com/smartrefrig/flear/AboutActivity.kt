package com.smartrefrig.flear

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.smartrefrig.flear.browser.WebViewClient
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    lateinit var webViewClient: WebViewClient

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //request window to use fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_about)

        init()
        setOnClickListener()

        if (isNetworkAvailable()) {
            val uri = "https://www.google.co.th"
            webview.loadUrl(uri)
            webview.webViewClient = WebViewClient()
            webview.settings.javaScriptEnabled = true

        }else{
            Toast.makeText(
                this,
                "Internet Disconnected",
                Toast.LENGTH_LONG
            ).show()

        }
    }

    private fun init() {
        webViewClient = WebViewClient()
    }

    private fun setOnClickListener() {
        backButton.setOnClickListener {
            finish()
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}
