package com.example.ungdungngenhac.notification.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ungdungngenhac.notification.CHANNEL_ID
import com.example.ungdungngenhac.MainActivity
import com.example.ungdungngenhac.R
import com.example.ungdungngenhac.interfaces.Constants
import com.example.ungdungngenhac.models.BaiHat
import com.example.ungdungngenhac.notification.Notifications
import com.example.ungdungngenhac.notification.broadcast.NotificationBroadcast
import java.util.*
import java.util.zip.Inflater

class NotificationService : Service() {
    private var notify: Notifications? = null
    private var intentBroadcast = Intent().setAction(Constants.ACTION.ACTION_BROADCAST)

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent!!.action) {
            Constants.ACTION.ACTION_SHOW_NOTIFICATION -> {
                showNotification(intent)

            }
            Constants.ACTION.ACTION_PREVIOUS -> {
                intentBroadcast.putExtra(Constants.KEY_INTENT_BROADCAST, Constants.ACTION.ACTION_PREVIOUS)
            }
            Constants.ACTION.ACTION_PLAY -> {
                intentBroadcast.putExtra(Constants.KEY_INTENT_BROADCAST, Constants.ACTION.ACTION_PLAY)
            }
            Constants.ACTION.ACTION_NEXT -> {
                intentBroadcast.putExtra(Constants.KEY_INTENT_BROADCAST, Constants.ACTION.ACTION_NEXT)
            }
        }
        //LocalBroadcastManager dùng để gửi data nội bộ trong app
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast)
        return START_REDELIVER_INTENT
    }

    private fun showNotification(intent:Intent) {
        notify=Notifications.getInstance(this)
        val baihat:BaiHat=intent.getSerializableExtra("Song") as BaiHat
        Log.d("Song",baihat.tenBaiHat)
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notify!!.builNotify().build())
        notify!!.setBaiHat(baihat)
        notify!!.setPlaying(true)

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}