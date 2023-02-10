package com.smartrefrig.flear.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.smartrefrig.flear.NoInternetActivity
import com.smartrefrig.flear.R
import com.smartrefrig.flear.auth.EditAccountActivity
import com.smartrefrig.flear.auth.EditIntroductoryActivity
import com.smartrefrig.flear.auth.UploadBackgroundBottomSheetFragment
import com.smartrefrig.flear.auth.UploadProfileBottomSheetFragment
import com.smartrefrig.flear.method.CrashLogManager
import com.smartrefrig.flear.model.User
import kotlinx.android.synthetic.main.fragment_account.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AccountFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mContext : Context
    private lateinit var myContext : FragmentActivity

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mRef : DatabaseReference
    private lateinit var mListener: ValueEventListener

    private lateinit var logManager : CrashLogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContext = activity!!.applicationContext
        myContext = activity as FragmentActivity
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            init()
            setOnClickListener()
        }catch (e : Exception){}

        if(!verifyAvailableNetwork(activity)){
            val intent = Intent(mContext, NoInternetActivity::class.java)
            startActivity(intent)
        }

        mRef = FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser!!.uid)

        mListener = mRef.addValueEventListener(object : ValueEventListener{

            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = User()
                user.getProfile(dataSnapshot)

                try {
                    usernameTextView.text = user.name
                    emailTextView.text = user.email
                    telephoneTextView.text = user.telephone

                    if(user.introductory == "-"){
                        introductoryTextView.text = "Click to set introductory text"
                    }else{
                        introductoryTextView.text = user.introductory
                    }

                    Glide.with(mContext)
                        .load(user.userUrl)
                        .centerCrop()
                        .placeholder(R.color.gray_slide_bar)
                        .into(userCircleImageView)

                    Glide.with(mContext)
                        .load(user.background)
                        .centerCrop()
                        .placeholder(R.color.gray_slide_bar)
                        .into(backgroundImageView)


                }catch (e : java.lang.Exception){
                    logManager.piacssoError()
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                //...
            }

        })
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        logManager = CrashLogManager()
    }

    private fun setOnClickListener() {
        editProfileFloatingActionButton.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext, NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(mContext,EditAccountActivity::class.java)
                startActivity(intent)
            }
        }
        backgroundImageView.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext, NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                val bundle = Bundle()
                bundle.putString("key", mAuth.currentUser!!.uid)
                val bottomSheet = UploadBackgroundBottomSheetFragment()
                bottomSheet.arguments = bundle
                bottomSheet.show(
                    myContext.supportFragmentManager,
                    "BottomSheetFragment"
                )
            }
        }
        userCircleImageView.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext, NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                val bundle = Bundle()
                bundle.putString("key", mAuth.currentUser!!.uid)
                val bottomSheet = UploadProfileBottomSheetFragment()
                bottomSheet.arguments = bundle
                bottomSheet.show(
                    myContext.supportFragmentManager,
                    "BottomSheetFragment"
                )
            }
        }
        introductoryTextView.setOnClickListener {
            if(!verifyAvailableNetwork(activity)){
                val intent = Intent(mContext, NoInternetActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(mContext, EditIntroductoryActivity::class.java)
                startActivity(intent)
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
