package com.anodev.fullwebview

import android.content.Context
import android.widget.Toast

object Utils {
    fun showPopupMessage(c:Context, msg:String, long:Int) {
        /*
        Shows Popup Message
         */
        val length:Int = if (long == 1) {
            Toast.LENGTH_LONG
        } else{
            Toast.LENGTH_SHORT
        }
        Toast.makeText(c, msg, length).show()
    }
}