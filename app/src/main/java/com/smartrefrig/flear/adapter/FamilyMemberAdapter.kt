package com.smartrefrig.flear.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.R
import com.smartrefrig.flear.auth.ProfileActivity
import com.smartrefrig.flear.bottomsheet.AdminBottomSheetFragment
import com.smartrefrig.flear.bottomsheet.MeAdminBottomSheetFragment
import com.smartrefrig.flear.bottomsheet.MeBottomSheetFragment
import com.smartrefrig.flear.bottomsheet.UserBottomSheetFragment
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.layout_family_member.view.*

class FamilyMemberAdapter (private val user : ArrayList<User>, private val context: Context, private val supportFragmentManager: FragmentManager, private val listener : OnItemClick)
    : androidx.recyclerview.widget.RecyclerView.Adapter<FamilyMemberAdapter.ImageViewHolder>() {

    private lateinit var mRef : DatabaseReference
    private lateinit var mRef2 : DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyMemberAdapter.ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_family_member, parent
                , false))
    }

    override fun getItemCount(): Int {
        return user.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent = user[position]
        mAuth = FirebaseAuth.getInstance()

        val typeface = ResourcesCompat.getFont(context,R.font.mitr)
        val typefaceBold = ResourcesCompat.getFont(context,R.font.mitr_medium)

        holder.nameTextView.typeface = typefaceBold
        holder.introductoryTextView.typeface = typeface

        try {
            mRef = FirebaseDatabase.getInstance().reference.child("user").child(uploadCurrent.userUid.toString())

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //...
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userModel = User()
                    userModel.getProfile(dataSnapshot)

                    mRef2 = FirebaseDatabase.getInstance().reference
                        .child("family")
                        .child(userModel.family.toString())

                    mRef2.addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            //...
                        }

                        override fun onDataChange(dataSnapshot2: DataSnapshot) {

                            if(uploadCurrent.userUid.toString() != mAuth.currentUser!!.uid) {
                                if (dataSnapshot2.child("admin").child(userModel.userUid.toString()).value != null) {
                                    holder.introductoryTextView.text = "ผู้ดูแล"

                                } else {
                                    holder.introductoryTextView.text = "สมาชิก"
                                }

                                if(dataSnapshot2.child("admin").child(mAuth.currentUser!!.uid).value != null){
                                    holder.moreImageButton.setOnClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = AdminBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                    }
                                    holder.userConstraintLayout.setOnLongClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = AdminBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                        true
                                    }
                                }else{
                                    holder.moreImageButton.setOnClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = UserBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                    }
                                    holder.userConstraintLayout.setOnLongClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = UserBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                        true
                                    }
                                }
                            } else {
                                if (dataSnapshot2.child("admin").child(userModel.userUid.toString()).value != null) {
                                    holder.introductoryTextView.text = "ผู้ดูแล"
                                } else {
                                    holder.introductoryTextView.text = "สมาชิก"
                                }

                                if (dataSnapshot2.child("admin").child(mAuth.currentUser!!.uid).value != null) {
                                    holder.moreImageButton.setOnClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = MeAdminBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                    }
                                    holder.userConstraintLayout.setOnLongClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = MeAdminBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                        true
                                    }
                                }else{
                                    holder.moreImageButton.setOnClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = MeBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                    }
                                    holder.userConstraintLayout.setOnLongClickListener {
                                        val bundle = Bundle()
                                        bundle.putString("key", uploadCurrent.userUid.toString())
                                        val bottomSheet = MeBottomSheetFragment()
                                        bottomSheet.arguments = bundle
                                        bottomSheet.show(
                                            supportFragmentManager,
                                            "BottomSheetFragment"
                                        )
                                        true
                                    }
                                }
                            }
                        }

                    })

                    holder.nameTextView.text = userModel.name.toString()

                    holder.userConstraintLayout.setOnClickListener {
                        if(userModel.userUid != mAuth.currentUser!!.uid) {
                            val intent = Intent(context, ProfileActivity::class.java)
                            intent.putExtra("userUid", userModel.userUid)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }else{
                            listener.onClick(true)
                        }
                    }

                    Glide.with(context)
                        .load(userModel.userUrl)
                        .centerCrop()
                        .placeholder(R.color.gray_slide_bar)
                        .into(holder.userCircleImageView)
                }

            })
        }catch (e : Exception){

        }

    }

    class ImageViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val nameTextView : TextView = view.usernameTextView
        val userCircleImageView : ImageView = view.userImageView
        val userConstraintLayout : ConstraintLayout = view.userConstraintLayout
        val introductoryTextView : TextView = view.introductoryTextView
        val moreImageButton : ImageButton = view.moreImageButton
    }
    interface OnItemClick {
        fun onClick(boolean: Boolean)
    }
}