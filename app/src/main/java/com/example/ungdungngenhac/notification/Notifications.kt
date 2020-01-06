package com.example.ungdungngenhac.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.ungdungngenhac.MainActivity
import com.example.ungdungngenhac.R
import com.example.ungdungngenhac.interfaces.Constants
import com.example.ungdungngenhac.models.BaiHat
import com.example.ungdungngenhac.notification.service.NotificationService
import java.util.*

import androidx.core.app.TaskStackBuilder


class Notifications(var context: Context) {
    private var notificationIntent: Intent = Intent(context, MainActivity::class.java)
    private var contentview = RemoteViews(context.packageName, R.layout.custom_notification)

    private lateinit var pendingIntent: PendingIntent
    companion object {
        private var notify: Notifications? = null

        fun getInstance(context: Context): Notifications {
            if (notify == null) {
                notify = Notifications(context)
            }
            return notify!!
        }
    }

    private var notificationbuilder: NotificationCompat.Builder? =null
    private var baihat: BaiHat? = null
    private val mNotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun setBaiHat(baihat: BaiHat) {
        this.baihat = baihat
        Log.d("ASong",baihat.tenBaiHat)
        contentview!!.setTextViewText(R.id.notification_title, baihat!!.tenBaiHat)
        contentview!!.setTextViewText(R.id.notification_content, baihat!!.tenCaSi)
        notificationIntent.putExtra(Constants.KEY_INTENT_SONG_MAIN,baihat)
        pendingIntent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)!!;
        notifyManager()
    }

    fun setPlaying(status: Boolean) {
        if (status) {
            notificationbuilder!!.contentView.setImageViewResource(
                R.id.notification_ic_pause_play,
                R.drawable.ic_pause_circle_outline
            )
        } else {
            notificationbuilder!!.contentView.setImageViewResource(
                R.id.notification_ic_pause_play,
                R.drawable.ic_play_circle_outline
            )
        }
        notifyManager()

    }
    private fun notifyManager(){

        mNotificationManager.notify(
            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
            notificationbuilder!!.build()
        )
    }
    fun builNotify(): NotificationCompat.Builder {
        //TODO: Action chuyển qua main chính khi click vào noti
        notificationIntent.setAction(Constants.ACTION.ACTION_MAIN)
        notificationIntent.putExtra(Constants.KEY_INTENT_SONG_MAIN,baihat)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        pendingIntent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)!!;
        //TODO:Action click vào previous noti
        val previousIntent: Intent = Intent(context, NotificationService::class.java)
        previousIntent.setAction(Constants.ACTION.ACTION_PREVIOUS)
        val ppreviousIntent: PendingIntent = PendingIntent.getService(context, 0, previousIntent, 0)

        //TODO:Action click vào play noti
        val playIntent: Intent = Intent(context, NotificationService::class.java)
        playIntent.setAction(Constants.ACTION.ACTION_PLAY)
        val pplayIntent: PendingIntent = PendingIntent.getService(context, 0, playIntent, 0)

        //TODO:Action click vào next noti
        val nextIntent: Intent = Intent(context, NotificationService::class.java)
        nextIntent.setAction(Constants.ACTION.ACTION_NEXT)
        val pnextIntent: PendingIntent = PendingIntent.getService(context, 0, nextIntent, 0)



        //TODO: set event cho view noti
        contentview!!.setOnClickPendingIntent(R.id.notification_ic_previous,ppreviousIntent)
        contentview!!.setOnClickPendingIntent(R.id.notification_ic_pause_play,pplayIntent)
        contentview!!.setOnClickPendingIntent(R.id.notification_ic_next,pnextIntent)

        notificationbuilder = NotificationCompat.Builder(
            context,
            CHANNEL_ID.ServiceChannel.toString()
        )
            .setSmallIcon(R.drawable.ic_logo_souncloud)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(contentview)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        return notificationbuilder!!
    }



}