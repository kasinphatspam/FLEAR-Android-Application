package com.smartrefrig.flear.auth

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.smartrefrig.flear.R
import com.smartrefrig.flear.bottomsheet.BottomSheetFragment
import com.smartrefrig.flear.method.FirebaseManager
import java.io.IOException


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
class UploadProfileBottomSheetFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var mBottomSheetListener: BottomSheetListener? = null

    private lateinit var mRef: DatabaseReference
    private lateinit var mRef2: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    var filePath: Uri? = null
    val REQUEST_TAKE_PHOTO = 1
    val REQUEST_SELECT_IMAGE_IN_ALBUM = 2
    val REQUEST_SELECT_IMAGE_IN_ALBUM_BACKGROUND = 3
    val REQUEST_TAKE_PHOTO_BACKGROUND = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    interface BottomSheetListener {
        fun onOptionClick(text: String) {

        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mAuth = FirebaseAuth.getInstance()

        val v = inflater.inflate(R.layout.fragment_upload_profile_bottom_sheet, container, false)
        val key = arguments!!.getString("key")

        val cameraConstraintLayout = v.findViewById<ConstraintLayout>(R.id.cameraConstraintLayout)
        val galleryConstraintLayout = v.findViewById<ConstraintLayout>(R.id.galleryConstraintLayout)

        mRef = FirebaseDatabase.getInstance().reference
            .child("user")
            .child(key!!)

        // Inflate the layout for this fragment

        cameraConstraintLayout.setOnClickListener {
            askCameraPermission()
        }

        galleryConstraintLayout.setOnClickListener {
            selectImageInAlbum()
        }

        return v
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mBottomSheetListener = context as UploadProfileBottomSheetFragment.BottomSheetListener?
        }catch (e : Exception){
//            throw ClassCastException(context.toString())
        }
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(context!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        filePath = context!!.contentResolver
            .insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    fun askCameraPermission(){
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                    AlertDialog.Builder(context)
                        .setTitle(
                            "Permissions Error!")
                        .setMessage(
                            "Please allow permissions to take photo with camera")
                        .setNegativeButton(
                            android.R.string.cancel
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token?.cancelPermissionRequest()
                        }
                        .setPositiveButton(android.R.string.ok
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token?.continuePermissionRequest()
                        }
                        .setOnDismissListener {
                            token?.cancelPermissionRequest() }
                        .show()
                }


                override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                    if (report.areAllPermissionsGranted()) {
                        //once permissions are granted, launch the camera
                        launchCamera()
                    } else {
                        Toast.makeText(context, "All permissions need to be granted to take photo", Toast.LENGTH_LONG).show()
                    }
                }

            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            try {
                val firebaseManager = FirebaseManager()
                firebaseManager.uploadProfile2(context!!,filePath!!)
                dismiss()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

        if(requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == AppCompatActivity.RESULT_OK) {

            filePath = data?.data!!
            try {
                filePath = data.data
                val firebaseManager = FirebaseManager()
                firebaseManager.uploadProfile2(context!!,filePath!!)
                dismiss()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }else{
            super.onActivityResult(requestCode, resultCode, data)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
            } else {
                Toast.makeText(context, "Not allowed", Toast.LENGTH_SHORT).show()
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}