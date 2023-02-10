package com.smartrefrig.flear

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.multidex.MultiDex
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.auth.EditAccountActivity
import com.smartrefrig.flear.auth.LoginActivity
import com.smartrefrig.flear.bottomsheet.BottomSheetFragment
import com.smartrefrig.flear.bottomsheet.UserBottomSheetFragment
import com.smartrefrig.flear.fragment.*
import com.smartrefrig.flear.method.CrashLogManager
import com.smartrefrig.flear.method.FirebaseManager
import com.smartrefrig.flear.model.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_progress.*


class MainActivity : AppCompatActivity() ,
    HomeFragment.OnFragmentInteractionListener,
    FamilyFragment.OnFragmentInteractionListener,
    NotifyFragment.OnFragmentInteractionListener,
    AccountFragment.OnFragmentInteractionListener,
    CarsFragment.OnFragmentInteractionListener,
    BottomSheetFragment.OnFragmentInteractionListener,
    UserBottomSheetFragment.OnFragmentInteractionListener{

    private lateinit var mDrawerLayout: DrawerLayout
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef : DatabaseReference
    lateinit var crashLogManager : CrashLogManager

    lateinit var mRef2 : DatabaseReference

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        setUpDefaultFragment()
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setUpNavigationView()
        setOnClickListener()

        //Check family is non-null
        val firebaseManager = FirebaseManager()
        firebaseManager.checkIfYouHaveFamily(this,this)

        //set status bar color
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
    }

    private fun setOnClickListener() {
        cancelButton.setOnClickListener {
            mainConstraintLayout.visibility = View.VISIBLE
            progressConstraintLayout.visibility = View.INVISIBLE
        }
    }

    private fun setUpDefaultFragment() {
        //Setup default fragment when this activity is open
        val fragment = HomeFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.homeFrameLayout, fragment,"Home")
        fragmentTransaction.commit()
    }

    private fun init() {
        crashLogManager = CrashLogManager()
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)
    }

    override fun onBackPressed() {
        //if drawer is open system will close the drawer before
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)

        } else {
            super.onBackPressed()
        }
    }

    private fun setUpNavigationView() {
        //Setup menu in navigation view
        val navigationView: NavigationView = findViewById(R.id.navigationView)

        val hView = navigationView.getHeaderView(0)
        val navUserTextView = hView.findViewById(R.id.usernameTextView) as TextView
        val navEditProfileTextView = hView.findViewById(R.id.editProfileTextView) as TextView
        val navUserCircleImageView = hView.findViewById(R.id.userCircleImageView) as CircleImageView

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val fragment = AccountFragment()
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.homeFrameLayout, fragment,"Profile")
                    fragmentTransaction.commit()

                    mDrawerLayout.closeDrawer(GravityCompat.START)
                    bottomNavigationView.selectedItemId = R.id.nav_account
                }
                R.id.nav_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                }
                R.id.nav_setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                }
                R.id.nav_signout -> {
                    mAuth.signOut()
                    finish()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }


        mRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val user = User()
                    user.getProfile(dataSnapshot)
                    navUserTextView.text = user.name.toString()

                    navEditProfileTextView.setOnClickListener {
                        val intent = Intent(this@MainActivity,EditAccountActivity::class.java)
                        startActivity(intent)
                    }

                    Glide.with(this@MainActivity)
                        .load(user.userUrl)
                        .centerCrop()
                        .placeholder(R.color.gray_slide_bar)
                        .into(navUserCircleImageView)

                }catch (e : java.lang.Exception){
                    crashLogManager.piacssoError()
                }
            }

        })

    }

    override fun onFragmentInteraction(uri: Uri) {
        //...
    }

    override fun onNavigationDrawerListener(boolean: Boolean) {
        if(boolean){
            //Open drawer in main screen
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onDialogLoadingListener(boolean: Boolean) {
        if(boolean){
            progressConstraintLayout.visibility = View.VISIBLE
            mainConstraintLayout.visibility = View.INVISIBLE
        }else{
            progressConstraintLayout.visibility = View.INVISIBLE
            mainConstraintLayout.visibility = View.VISIBLE
        }
    }

    override fun onFragmentListener(boolean: Boolean) {
        if(boolean){
            val fragment = AccountFragment()
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.homeFrameLayout, fragment,"Profile")
            fragmentTransaction.commit()

            bottomNavigationView.selectedItemId = R.id.nav_account

        }
    }

    //Setup bottom navigation view
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                val fragment = HomeFragment()
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.homeFrameLayout, fragment,"Home")
                fragmentTransaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_cars -> {
                val fragment = CarsFragment()
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.homeFrameLayout, fragment,"Home")
                fragmentTransaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_family -> {
                val fragment = FamilyFragment()
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.homeFrameLayout, fragment , "Family")
                fragmentTransaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notify -> {
                val fragment = NotifyFragment()
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.homeFrameLayout, fragment , "Notify")
                fragmentTransaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_account -> {
                val fragment = AccountFragment()
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.homeFrameLayout, fragment,"Profile")
                fragmentTransaction.commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(baseContext)
    }
}