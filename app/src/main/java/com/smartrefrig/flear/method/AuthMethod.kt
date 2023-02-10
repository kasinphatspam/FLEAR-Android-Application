package com.smartrefrig.flear.method

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.EditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.smartrefrig.flear.MainActivity
import com.smartrefrig.flear.auth.CameraAPIActivity
import com.smartrefrig.flear.dialog.LoadingDialog
import com.smartrefrig.flear.model.User

class AuthMethod {

    lateinit var toastMakeTextMethod: ToastMakeTextMethod
    lateinit var keyboardManager: KeyboardManager
    lateinit var firebaseManger: FirebaseManager
    lateinit var user: User
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference
    lateinit var mTokenRef: DatabaseReference
    lateinit var loadingDialog: LoadingDialog

    fun createUserAccount(activity: Activity
                          , context: Context
                          , emailEditText: EditText
                          , passwordEditText: EditText
                          , nameEditText: EditText
                          , telephoneNumberEditText: EditText)
    {
        init(context)
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        loadingDialog.show()

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                val name =  nameEditText.text.toString()
                val telephone = telephoneNumberEditText.text.toString()
                val userUid = mAuth.currentUser!!.uid
                val userUrl = "-"
                val introductory = "-"
                val background = "-"
                val family = "-"

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        // Get new Instance ID token
                        val token = task.result!!.token
                        //Add user data to firebase
                        firebaseManger.createUserAccount(email,name,telephone,userUid,userUrl,introductory,background,token,family,context)

                        val intent = Intent(context, CameraAPIActivity::class.java)
                        //Pass value to model user and pass value to next activity (Object)
                        user.email = email
                        user.name = name
                        user.telephone = telephone
                        user.userUid = userUid
                        user.userUrl = userUrl
                        user.introductory = introductory
                        user.background = background
                        user.token = token
                        user.family  = family
                        intent.putExtra("user",user)
                        context.startActivity(intent)

                        toastMakeTextMethod.createAccountComplete(context)
                        mTokenRef.child(mAuth.currentUser!!.uid).child("token").setValue(token)

                        loadingDialog.cancel()
                    })
            }else{
                //Login fails , close keyboard and show text
                toastMakeTextMethod.cannotCreateYourAccount(context)

                loadingDialog.cancel()
            }
        }
    }

    fun loginWithEmailAndPassword(activity: Activity , context: Context , emailEditText: EditText , passwordEditText: EditText){
        init(context)
        val email = emailEditText.text.toString()
        val password=  passwordEditText.text.toString()

        loadingDialog.show()

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                //Login successful and app will switch activity to main activity
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        // Get new Instance ID token
                        val token = task.result!!.token

                        activity.finish()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        toastMakeTextMethod.loginAccountComplete(context)
                        mTokenRef.child(mAuth.currentUser!!.uid).child("token").setValue(token)

                        loadingDialog.cancel()
                    })
            }else{
                //Login fails , close keyboard and show text
                keyboardManager.hideKeyboard(activity)
                toastMakeTextMethod.cannotLoginAccount(context)

                loadingDialog.cancel()
            }
        }
    }

    private fun init(context: Context){
        toastMakeTextMethod = ToastMakeTextMethod()
        keyboardManager = KeyboardManager()
        user = User()
        mAuth = FirebaseAuth.getInstance()
        firebaseManger = FirebaseManager()
        mTokenRef = FirebaseDatabase.getInstance().reference.child("user")
        loadingDialog = LoadingDialog(context)
    }

}