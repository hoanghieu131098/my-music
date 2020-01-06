package com.example.ungdungngenhac.notification.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ungdungngenhac.MainActivity
import com.example.ungdungngenhac.R


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage





class MyFirebaseService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // handle a notification payload.
        Log.d("BBB", "running payload: ")
        if (remoteMessage.notification != null) {
            Log.d("AAA", "Message Notification Body: " + remoteMessage.notification!!.body!!)

            sendNotification(remoteMessage.notification!!.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
    }

    private fun sendNotification(messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.ic_logo_souncloud
                )
            )
//            .setContentTitle(getString(R.string.project_id))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.sym_call_missed,
                    "Cancel",
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                )
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.sym_call_outgoing,
                    "OK",
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                )
            )
        //set icon
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.notify(0, notificationBuilder.build())
    }
    private fun getNotificationIcon(notificationBuilder: NotificationCompat.Builder): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = 0x008000
            notificationBuilder.color = color
            return R.drawable.ic_logo_souncloud

        }
        return R.drawable.ic_logo_souncloud
    }
}