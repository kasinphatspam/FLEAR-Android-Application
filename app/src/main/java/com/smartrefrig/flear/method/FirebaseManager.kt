package com.smartrefrig.flear.method

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.smartrefrig.flear.MainActivity
import com.smartrefrig.flear.dialog.LoadingDialog
import com.smartrefrig.flear.family.CreateOrJoinFamilyActivity
import com.smartrefrig.flear.model.User
import java.util.*
import kotlin.collections.HashMap


class FirebaseManager {

    lateinit var mRef : DatabaseReference
    lateinit var mRef2 : DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var mStorageReference: StorageReference
    lateinit var mFireStorage : FirebaseStorage
    lateinit var toastMakeTextMethod: ToastMakeTextMethod
    lateinit var loadingDialog: LoadingDialog

    fun editCars(activity: Activity ,context: Context, carName: String, licensePlate: String, brand: String,key: String) {
        init(context)
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(user.family.toString())

                mRef2.child("cars").child(key).child("carName").setValue(carName)
                mRef2.child("cars").child(key).child("licensePlate").setValue(licensePlate)
                mRef2.child("cars").child(key).child("brand").setValue(brand)
                mRef.child("cars").setValue(key)
                toastMakeTextMethod.editCarsComplete(context)

                activity.finish()
            }

        })
    }

    fun createNewCars(activity: Activity ,context: Context, carName: String, licensePlate: String, brand: String) {
        init(context)
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(user.family.toString())
                val key = mRef2.push().key.toString()

                val hashMap = HashMap<String,String>()
                hashMap["carName"] = carName
                hashMap["brand"] = brand
                hashMap["licensePlate"] = licensePlate
                hashMap["key"] = key

                mRef2.child("cars").child(key).setValue(hashMap)
                mRef.child("cars").setValue(key)
                toastMakeTextMethod.createNewCarComplete(context)

                activity.finish()
            }

        })
    }

    fun joinFamily(context: Context,passcode: String){
        init(context)
        loadingDialog.show()
        mRef = FirebaseDatabase.getInstance().reference.child("family")
        mRef2 = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        mRef.child(passcode).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value != null){
                    mRef.child(passcode).child("member").child(mAuth.currentUser!!.uid).setValue(mAuth.currentUser!!.uid)
                    mRef2.child("family").setValue(passcode)

                    loadingDialog.cancel()
                    toastMakeTextMethod.joinFamilyComplete(context)
                    val intent = Intent(context,MainActivity::class.java)
                    context.startActivity(intent)
                }else{
                    loadingDialog.cancel()
                    toastMakeTextMethod.cannotJoinFamily(context)
                }
            }

        })
    }

    fun createNewFamily(context: Context, familyName: String, telephone: String, address: String, location: String) {
        init(context)
        loadingDialog.show()
        mRef = FirebaseDatabase.getInstance().reference.child("family")
        mRef2 = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)
        val key = getRandomString(5)

        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(key).value == null){
                    addValueToDatabase()
                }else{
                    loadingDialog.cancel()
                    createNewFamily(context,familyName,telephone,address,location)
                }
            }

            private fun addValueToDatabase() {
                val hashMap = HashMap<String,String>()
                hashMap["familyName"] = familyName
                hashMap["telephone"] = telephone
                hashMap["address"] = address
                hashMap["location"] = location
                hashMap["key"] = key

                mRef.child(key).setValue(hashMap)
                //Add family member
                mRef.child(key).child("admin").child(mAuth.currentUser!!.uid).setValue(mAuth.currentUser!!.uid)
                mRef.child(key).child("member").child(mAuth.currentUser!!.uid).setValue(mAuth.currentUser!!.uid)
                mRef2.child("family").setValue(key)

                loadingDialog.cancel()
                toastMakeTextMethod.createFamilyComplete(context)
                val intent = Intent(context,MainActivity::class.java)
                context.startActivity(intent)
            }

        })
    }

    fun checkIfYouHaveFamily(activity: Activity, context: Context){
        init(context)
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)
                if(user.family == "-"){
                    activity.finish()
                    val intent = Intent(context,CreateOrJoinFamilyActivity::class.java)
                    context.startActivity(intent)
                }
            }

        })
    }

    fun createUserAccount(email : String , name : String , telephone : String , userUid : String , userUrl : String , introductory : String , background : String , token : String , family : String,context: Context){
        init(context)
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        val hashMap = HashMap<String,String>()
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["telephone"] = telephone
        hashMap["userUid"] = userUid
        hashMap["userUrl"] = userUrl
        hashMap["introductory"] = introductory
        hashMap["background"] = background
        hashMap["token"] = token
        hashMap["family"] = family
        mRef.setValue(hashMap)
    }

    fun uploadProfile(context: Context,imageUri: Uri){
        init(context)
        mRef = FirebaseDatabase.getInstance()
            .reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("userUrl")
        mFireStorage = FirebaseStorage.getInstance()
        mStorageReference = mFireStorage.reference

        loadingDialog.show()

        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value.toString() == "-") {
                    val uuid = UUID.randomUUID().toString()
                    val ref = mStorageReference.child("profile/$uuid")
                    ref.putFile(imageUri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            mRef.setValue(imageUrl)

                            val intent = Intent(context,CreateOrJoinFamilyActivity::class.java)
                            context.startActivity(intent)
                            loadingDialog.cancel()
                        }
                    }
                } else {
                    try {
                        val storageReference =
                            FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.value.toString())
                        storageReference.delete().addOnSuccessListener {}.addOnFailureListener {}
                    } catch (e: Exception) {
                        Log.d("FirebaseManager", "has error and can't remove images")
                    }

                    val uuid = UUID.randomUUID().toString()
                    val ref = mStorageReference.child("profile/$uuid")
                    ref.putFile(imageUri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            mRef.setValue(imageUrl)

                            val intent = Intent(context,CreateOrJoinFamilyActivity::class.java)
                            context.startActivity(intent)
                            loadingDialog.cancel()
                        }
                    }
                }
            }
        })
    }

    fun uploadProfile2(context: Context,imageUri: Uri){
        init(context)
        mRef = FirebaseDatabase.getInstance()
            .reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("userUrl")
        mFireStorage = FirebaseStorage.getInstance()
        mStorageReference = mFireStorage.reference

        loadingDialog.show()

        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value.toString() == "-") {
                    val uuid = UUID.randomUUID().toString()
                    val ref = mStorageReference.child("profile/$uuid")
                    ref.putFile(imageUri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            mRef.setValue(imageUrl)

                            loadingDialog.cancel()
                        }
                    }
                } else {
                    try {
                        val storageReference =
                            FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.value.toString())
                        storageReference.delete().addOnSuccessListener {}.addOnFailureListener {}
                    } catch (e: Exception) {
                        Log.d("FirebaseManager", "has error and can't remove images")
                    }

                    val uuid = UUID.randomUUID().toString()
                    val ref = mStorageReference.child("profile/$uuid")
                    ref.putFile(imageUri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            mRef.setValue(imageUrl)

                            loadingDialog.cancel()
                        }
                    }
                }
            }
        })
    }

    fun uploadBackground(context: Context,imageUri: Uri){
        init(context)
        mRef = FirebaseDatabase.getInstance()
            .reference
            .child("user")
            .child(mAuth.currentUser!!.uid)
            .child("background")
        mFireStorage = FirebaseStorage.getInstance()
        mStorageReference = mFireStorage.reference

        loadingDialog.show()

        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value.toString() == "-") {
                    val uuid = UUID.randomUUID().toString()
                    val ref = mStorageReference.child("background/$uuid")
                    ref.putFile(imageUri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            mRef.setValue(imageUrl)

                            loadingDialog.cancel()
                        }
                    }
                } else {
                    try {
                        val storageReference =
                            FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.value.toString())
                        storageReference.delete().addOnSuccessListener {}.addOnFailureListener {}
                    } catch (e: Exception) {
                        Log.d("FirebaseManager", "has error and can't remove images")
                    }

                    val uuid = UUID.randomUUID().toString()
                    val ref = mStorageReference.child("background/$uuid")
                    ref.putFile(imageUri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            mRef.setValue(imageUrl)

                            loadingDialog.cancel()
                        }
                    }
                }
            }
        })
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXTZ"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun init(context : Context) {
        mAuth = FirebaseAuth.getInstance()
        toastMakeTextMethod = ToastMakeTextMethod()
        loadingDialog = LoadingDialog(context)
    }
}