package com.smartrefrig.flear.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.NoInternetActivity
import com.smartrefrig.flear.R
import com.smartrefrig.flear.SearchActivity
import com.smartrefrig.flear.adapter.EARAdapter
import com.smartrefrig.flear.model.EAR
import com.smartrefrig.flear.model.Family
import com.smartrefrig.flear.model.Sensor
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.fragment_family.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NotifyFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NotifyFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NotifyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef : DatabaseReference

    private lateinit var mContext : Context
    private lateinit var myContext : FragmentActivity

    private lateinit var mUploads : ArrayList<EAR>
    private lateinit var mShowUploads : ArrayList<EAR>
    private lateinit var mAdapter : EARAdapter
    private lateinit var mRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var noneConstraintLayout: ConstraintLayout
    private lateinit var linearLayoutManager : LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mContext = activity!!.applicationContext
        myContext = activity as FragmentActivity

        return inflater.inflate(R.layout.fragment_notify, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            init()
            setOnClickListener()
            setSensorDatabase()
            setNotifyOfEAR()
        }catch (e : Exception){}

        if(!verifyAvailableNetwork(activity)){
            val intent = Intent(mContext, NoInternetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        mRecyclerView = view!!.findViewById(R.id.earRecyclerView)
        noneConstraintLayout = view!!.findViewById(R.id.noneConstraintLayout)

        linearLayoutManager = LinearLayoutManager(mContext)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        mUploads = ArrayList()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = linearLayoutManager
    }

    private fun setNotifyOfEAR() {
        mRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = User()
                user.getProfile(p0)

                val carIsSelected = p0.child("cars").value.toString()

                val mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(user.family.toString())

                mRef2.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                    override fun onDataChange(familySnapshot: DataSnapshot) {
                        val family = Family()
                        family.getDetails(familySnapshot)

                        if(familySnapshot.child("cars").child(carIsSelected).value != null){

                            val sensor = familySnapshot.child("cars").child(carIsSelected).child("sensor").value.toString()
                            val mRef4 = FirebaseDatabase.getInstance().reference.child("sensor").child(sensor)

                            mRef4.child("history").child("camera").addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                    //...
                                }

                                override fun onDataChange(sensorSnapshot: DataSnapshot) {
                                    mUploads.clear()
                                    for (postSnapshot in sensorSnapshot.children) {
                                        val upload = postSnapshot.getValue(EAR::class.java)
                                        mUploads.add(upload!!)
                                    }

                                    mShowUploads = ArrayList(mUploads)
                                    mAdapter = EARAdapter( mShowUploads,mContext,myContext.supportFragmentManager)
                                    mRecyclerView.adapter = mAdapter
                                    mAdapter.notifyDataSetChanged()

                                    if(linearLayoutManager.itemCount == 0){
                                        noneConstraintLayout.visibility = View.VISIBLE
                                        mRecyclerView.visibility = View.INVISIBLE
                                    }else{
                                        noneConstraintLayout.visibility = View.INVISIBLE
                                        mRecyclerView.visibility = View.VISIBLE
                                    }
                                }

                            })
                        }else{
                            noneConstraintLayout.visibility = View.VISIBLE
                            mRecyclerView.visibility = View.INVISIBLE
                        }
                    }

                })
            }

        })
    }

    private fun setOnClickListener() {
        menuImageView.setOnClickListener {
            listener?.onNavigationDrawerListener(true)
        }
        searchConstraintLayout.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext, NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(mContext, SearchActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
        refreshImageView.setOnClickListener {
            if (!verifyAvailableNetwork(activity)) {
                val intent = Intent(mContext, NoInternetActivity::class.java)
                startActivity(intent)
            } else {
                refreshImageView.visibility = View.INVISIBLE
                refreshAvi.visibility = View.VISIBLE

                Handler().postDelayed({
                    setNotifyOfEAR()
                    refreshImageView.visibility = View.VISIBLE
                    refreshAvi.visibility = View.INVISIBLE
                }, 2000)
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
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
        fun onNavigationDrawerListener(boolean: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotifyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotifyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setSensorDatabase() {
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)
        mRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //...
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                val mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(user.family.toString()).child("cars").child(dataSnapshot.child("cars").value.toString())

                mRef2.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                    override fun onDataChange(dataSnapshot2: DataSnapshot) {
                        if(dataSnapshot2.value != null){
                            if(dataSnapshot2.child("sensor").value != null){
                                val mRef3 = FirebaseDatabase.getInstance().reference.child("sensor").child(dataSnapshot2.child("sensor").value.toString())

                                mRef3.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        //...
                                    }

                                    @SuppressLint("SetTextI18n")
                                    override fun onDataChange(dataSnapshot3: DataSnapshot) {
                                        val sensor = Sensor()
                                        sensor.getDetails(dataSnapshot3)

                                        try {
                                            val heartRate : Int = sensor.heartRate!!.toInt()
                                            heartRateTextView.text = "Heart Rate : $heartRate"
                                            earTextView.text = "EAR : " + sensor.ear.toString()
                                            statusTextView.text = sensor.status
                                        }catch (e : Exception){
                                            //...
                                        }
                                    }

                                })
                            }else{
                                //...
                            }
                        }else{
                            //...
                        }
                    }

                })
            }

        })
    }

    private fun verifyAvailableNetwork(activity: Activity):Boolean{
        val connectivityManager= activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo= connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }
}
