package com.smartrefrig.flear.adapter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout
import com.smartrefrig.flear.R
import com.smartrefrig.flear.bottomsheet.BottomSheetFragment
import com.smartrefrig.flear.model.Cars
import kotlinx.android.synthetic.main.layout_user_car.view.*


class CarsAdapter (private val cars : ArrayList<Cars>, private val context: Context, private val supportFragmentManager: FragmentManager)
    : androidx.recyclerview.widget.RecyclerView.Adapter<CarsAdapter.ImageViewHolder>() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mRef : DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsAdapter.ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_user_car, parent
                , false))
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent = cars[position]
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        val typeface = ResourcesCompat.getFont(context,R.font.mitr)
        val typefaceBold = ResourcesCompat.getFont(context,R.font.mitr_medium)

        holder.carNameTextView.typeface = typefaceBold
        holder.brandTextView.typeface = typeface
        holder.licensePlateTextView.typeface = typeface

        try {
            holder.carNameTextView.text = uploadCurrent.carName
            holder.brandTextView.text = uploadCurrent.brand
            holder.licensePlateTextView.text = uploadCurrent.licensePlate

            holder.showMenuImageView.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("key", uploadCurrent.key.toString())
                val bottomSheet = BottomSheetFragment()
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager, "BottomSheetFragment")
            }

            holder.carsConstraintLayout.setOnLongClickListener {
                val bundle = Bundle()
                bundle.putString("key", uploadCurrent.key.toString())
                val bottomSheet = BottomSheetFragment()
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager, "BottomSheetFragment")
                return@setOnLongClickListener true
            }

            holder.carsConstraintLayout.setOnClickListener {
                mRef.child("cars").setValue(uploadCurrent.key.toString())
            }

            mRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //...
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(uploadCurrent.key.toString() == dataSnapshot.child("cars").value.toString()){
                        holder.carsRoundKornerRelativeLayout.setBackgroundResource(R.color.blue3)
                    }else{
                        holder.carsRoundKornerRelativeLayout.setBackgroundResource(R.color.white)
                    }
                }

            })
        }catch (e : Exception){

        }

    }

    class ImageViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val carNameTextView : TextView = view.carNameTextView
        val brandTextView : TextView = view.brandTextView
        val licensePlateTextView : TextView = view.licensePlateTextView
        val showMenuImageView : ImageView = view.showMenuImageView
        val carsConstraintLayout : ConstraintLayout = view.carsConstraintLayout
        val carsRoundKornerRelativeLayout : RoundKornerRelativeLayout = view.carsRoundKornerRelativeLayout
    }
}