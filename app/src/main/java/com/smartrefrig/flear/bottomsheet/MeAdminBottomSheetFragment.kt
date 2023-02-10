package com.smartrefrig.flear.bottomsheet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.R
import com.smartrefrig.flear.model.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BottomSheetFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BottomSheetFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MeAdminBottomSheetFragment: BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var mBottomSheetListener : BottomSheetListener? = null

    private lateinit var mRef : DatabaseReference
    private lateinit var mRef2 : DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    interface BottomSheetListener {
        fun onOptionClick (text: String){

        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mAuth = FirebaseAuth.getInstance()

        val v = inflater.inflate(R.layout.fragment_me_admin_bottom_sheet, container, false)
        val key = arguments!!.getString("key")

        val deleteConstraintLayout = v.findViewById<ConstraintLayout>(R.id.deleteConstraintLayout)
        val permissionConstraintLayout = v.findViewById<ConstraintLayout>(R.id.permissionConstraintLayout)

        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(key!!)

        setOnClickListener(permissionConstraintLayout,deleteConstraintLayout,key,context!!)

        // Inflate the layout for this fragment

        return v
    }


    private fun setOnClickListener(permissionConstraintLayout: ConstraintLayout, deleteConstraintLayout: ConstraintLayout, key: String, context: Context)
    {
        permissionConstraintLayout.setOnClickListener {
            dismiss()
            mRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //...
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = User()
                    user.getProfile(dataSnapshot)
                    checkAdminCount(user,key,context)
                }

            })
        }

        deleteConstraintLayout.setOnClickListener {
            dismiss()
            mRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //...
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = User()
                    user.getProfile(dataSnapshot)

                    mRef2 = FirebaseDatabase.getInstance().reference.child("family")

                    mRef2.child(user.family.toString()).child("member").child(key).removeValue()
                    mRef2.child(user.family.toString()).child("admin").child(key).removeValue()
                    mRef.child("family").setValue("-")
                }

            })
        }
    }

    private fun checkAdminCount(user: User,key: String,context: Context) {
        mRef2 = FirebaseDatabase.getInstance().reference.child("family")

        mRef2.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child(user.family.toString()).child("admin").childrenCount.toString() == "1"){
                    Toast.makeText(context,"ไม่สามารถลบสิทธิ์ผู้ดูแลได้ เนื่องจากมีผู้ดูแลเพียงคนเดียว",Toast.LENGTH_SHORT).show()
                    Log.i("FamilyPermission","can't remove permission because your family has only one admin")
                }else{
                    mRef2.child(user.family.toString()).child("admin").child(key).removeValue()
                }
            }

        })
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mBottomSheetListener = context as BottomSheetListener?
        }catch (e : Exception){
//            throw ClassCastException(context.toString())
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BottomSheetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}