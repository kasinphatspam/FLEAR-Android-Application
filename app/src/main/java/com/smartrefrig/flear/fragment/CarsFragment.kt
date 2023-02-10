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
import com.smartrefrig.flear.adapter.CarsAdapter
import com.smartrefrig.flear.family.AddNewCarsActivity
import com.smartrefrig.flear.model.Cars
import com.smartrefrig.flear.model.Sensor
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.fragment_cars.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CarsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CarsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CarsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mContext : Context
    private lateinit var myContext : FragmentActivity

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef : DatabaseReference
    private lateinit var mListener : ValueEventListener

    private lateinit var mUploads : ArrayList<Cars>
    private lateinit var mShowUploads : ArrayList<Cars>
    private lateinit var mAdapter : CarsAdapter
    private lateinit var mRecyclerView : androidx.recyclerview.widget.RecyclerView
    private lateinit var linearLayoutManager : LinearLayoutManager
    private lateinit var noneConstraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mContext = activity!!.applicationContext
        myContext = activity as FragmentActivity

        return inflater.inflate(R.layout.fragment_cars, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            init()
            setOnClickListener()
            setSensorDatabase()
            setCarsListInFamily()
        }catch (e : Exception){}

        if(!verifyAvailableNetwork(activity)){
            val intent = Intent(mContext, NoInternetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        mRecyclerView = view!!.findViewById(R.id.myCarsRecyclerView)
        noneConstraintLayout = view!!.findViewById(R.id.noneConstraintLayout)

        linearLayoutManager = LinearLayoutManager(mContext)

        mUploads = ArrayList()

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = linearLayoutManager
    }

    private fun setOnClickListener() {
        refreshImageView.setOnClickListener {

            if(!verifyAvailableNetwork(activity)) {
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else{
                refreshImageView.visibility = View.INVISIBLE
                refreshAvi.visibility = View.VISIBLE

                Handler().postDelayed({
                    setCarsListInFamily()
                    refreshImageView.visibility = View.VISIBLE
                    refreshAvi.visibility = View.INVISIBLE
                }, 2000)
            }
        }
        menuImageView.setOnClickListener {
            listener?.onNavigationDrawerListener(true)
        }
        searchConstraintLayout.setOnClickListener {
            if(!verifyAvailableNetwork(activity)) {
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(mContext, SearchActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
        addMyCarsFloatingActionButton.setOnClickListener {
            if(!verifyAvailableNetwork(activity)) {
                val intent = Intent(mContext,NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(mContext, AddNewCarsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setCarsListInFamily() {
        mRef.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                val mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(user.family.toString())

                mRef2.child("cars").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot2: DataSnapshot) {
                        mUploads.clear()
                        for (postSnapshot in dataSnapshot2.children) {
                            val upload = postSnapshot.getValue(Cars::class.java)
                            mUploads.add(upload!!)
                        }
                        mShowUploads = ArrayList(mUploads)
                        mAdapter = CarsAdapter( mShowUploads,mContext,myContext.supportFragmentManager)
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
                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }
                })
            }
            override fun onCancelled(p0: DatabaseError) {
                //...
            }
        })
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
         * @return A new instance of fragment CarsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CarsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setSensorDatabase() {
        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)
        mListener = mRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                val mRef2 = FirebaseDatabase.getInstance().reference.child("family").child(user.family.toString()).child("cars").child(dataSnapshot.child("cars").value.toString())

                mRef2.addValueEventListener(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot2: DataSnapshot) {
                        if(dataSnapshot2.value != null){
                            if(dataSnapshot2.child("sensor").value != null){
                                val mRef3 = FirebaseDatabase.getInstance().reference.child("sensor").child(dataSnapshot2.child("sensor").value.toString())

                                mRef3.addValueEventListener(object : ValueEventListener{

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
                                    override fun onCancelled(p0: DatabaseError) {
                                        //...
                                    }
                                })
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        //...
                    }

                })
            }

            override fun onCancelled(p0: DatabaseError) {
                //...
            }

        })
    }

    private fun verifyAvailableNetwork(activity: Activity):Boolean{
        val connectivityManager= activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo= connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

    override fun onDestroy() {
        super.onDestroy()
        mRef.onDisconnect()
        mRef.removeEventListener(mListener)
    }
}
