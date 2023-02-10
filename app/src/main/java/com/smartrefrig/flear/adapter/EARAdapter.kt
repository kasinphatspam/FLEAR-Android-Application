package com.smartrefrig.flear.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.smartrefrig.flear.R
import com.smartrefrig.flear.model.EAR
import kotlinx.android.synthetic.main.layout_ear.view.*

class EARAdapter (private val cars : ArrayList<EAR>, private val context: Context, private val supportFragmentManager: FragmentManager)
    : RecyclerView.Adapter<EARAdapter.ImageViewHolder>() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mRef : DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_ear, parent, false))
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent = cars[position]

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        val typeface = ResourcesCompat.getFont(context,R.font.mitr)
        val typefaceBold = ResourcesCompat.getFont(context,R.font.mitr_medium)

        holder.notifyTextView.typeface = typefaceBold
        holder.timeTextView.typeface = typeface

        try {
            val time = uploadCurrent.time.toString()
            val date = uploadCurrent.date.toString()

            holder.timeTextView.text = "เวลา $time ในวันที่ $date"
        }catch (e : Exception){

        }

    }

    class ImageViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val timeTextView : TextView = view.timeTextView
        val notifyTextView : TextView = view.notifyTextView
    }
}