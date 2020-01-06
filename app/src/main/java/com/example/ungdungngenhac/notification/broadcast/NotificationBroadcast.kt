package com.example.ungdungngenhac.notification.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ungdungngenhac.interfaces.Constants
import com.example.ungdungngenhac.notification.Notifications

class NotificationBroadcast : BroadcastReceiver() {
    private var notify: Notifications? = null
    override fun onReceive(context: Context?, inten: Intent?) {
//        notify!!.builNotify()
//        notify!!.showNotify(123)
        when(inten!!.getStringExtra(Constants.KEY_INTENT_BROADCAST)){
            Constants.ACTION.ACTION_PREVIOUS -> Log.d("pre","ok")
        }
    }
}