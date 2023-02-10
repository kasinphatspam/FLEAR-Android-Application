package com.smartrefrig.flear.method

import android.content.Context
import android.widget.Toast

class ToastMakeTextMethod {

    fun cannotSetHardware(context: Context){
        Toast.makeText(context,"ไม่สามารถเพิ่มเซนเซอร์ได้ โปรดตรวจสอบอีกครั้ง",Toast.LENGTH_SHORT).show()
    }
    fun editCarsComplete(context: Context){
        Toast.makeText(context,"แก้ไขรถยนต์ของคุณแล้ว",Toast.LENGTH_SHORT).show()
    }
    fun createNewCarComplete(context: Context){
        Toast.makeText(context,"บันทึกรถยนต์ของคุณแล้ว",Toast.LENGTH_SHORT).show()
    }
    fun copyClipBoardPasscode(context: Context){
        Toast.makeText(context,"คัดลอกพาสโค้ดแล้ว",Toast.LENGTH_SHORT).show()
    }
    fun joinFamilyComplete(context: Context){
        Toast.makeText(context,"เข้าร่วมครอบครัวนี้แล้ว",Toast.LENGTH_SHORT).show()
    }
    fun createFamilyComplete(context: Context){
        Toast.makeText(context,"สร้างครอบครัวใหม่แล้ว",Toast.LENGTH_SHORT).show()
    }
    fun cannotJoinFamily(context: Context){
        Toast.makeText(context,"รหัสเข้าร่วมผิดพลาด",Toast.LENGTH_SHORT).show()
    }
    //Create user account
    fun cannotCreateYourAccount(context: Context){
        Toast.makeText(context,"ไม่สามารถสร้างบัญชีได้ กรุณาลองใหม่อีกครั้ง",Toast.LENGTH_SHORT).show()
    }

    fun createAccountComplete(context: Context){
        Toast.makeText(context,"สร้างบัญชีเรียบร้อยแล้ว",Toast.LENGTH_SHORT).show()
    }

    fun cannotLoginAccount(context: Context){
        Toast.makeText(context,"รหัสผิดพลาด ลองใหม่อีกครั้ง",Toast.LENGTH_SHORT).show()
    }

    fun loginAccountComplete(context: Context){
        Toast.makeText(context,"เข้าสู่ระบบเรียบร้อยแล้ว",Toast.LENGTH_SHORT).show()
    }

    fun editAccountComplete(context: Context){
        Toast.makeText(context,"แก้ไขเรียบร้อยแล้ว",Toast.LENGTH_SHORT).show()
    }

    fun theNumberOfCharactersIsNull(context: Context){
        Toast.makeText(context,"โปรดใส่ข้อมูลให้ครบก่อน",Toast.LENGTH_SHORT).show()
    }
}