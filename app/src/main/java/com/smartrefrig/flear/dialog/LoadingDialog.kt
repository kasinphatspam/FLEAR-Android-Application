package com.smartrefrig.flear.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.smartrefrig.flear.R

class LoadingDialog(context: Context) {

    private var mDialog : Dialog = Dialog(context)

    init {
        mDialog.apply {
            setContentView(R.layout.dialog_loading)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable())
        }
    }

    fun show(){
        mDialog.show()
    }

    fun cancel(){
        mDialog.cancel()
    }
}