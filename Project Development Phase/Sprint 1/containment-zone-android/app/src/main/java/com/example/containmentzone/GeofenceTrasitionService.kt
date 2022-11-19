package com.example.finalgeofence

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.finalgeofence.GeofenceTrasitionService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceTrasitionService : IntentService(TAG) {
    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)

        if (geofencingEvent!!.hasError()) {
            val errorMsg = getErrorString(
                geofencingEvent.errorCode
            )
            Log.e(TAG, errorMsg)
            return
        }
        val geoFenceTransition = geofencingEvent.geofenceTransition

        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val geofenceTransitionDetails =
                getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences)

            // Send notification details as a String
            sendNotification(geofenceTransitionDetails)
        }
    }

    private fun getGeofenceTrasitionDetails(
        geoFenceTransition: Int,
        triggeringGeofences: List<Geofence>?
    ): String {
        // get the ID of each geofence triggered
        val triggeringGeofencesList = ArrayList<String?>()
        for (geofence in triggeringGeofences!!) {
            triggeringGeofencesList.add(geofence.requestId)
        }
        var status: String? = null
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) status =
            "Entering " else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) status =
            "Exiting "
        return status + TextUtils.join(", ", triggeringGeofencesList)
    }

    private fun sendNotification(msg: String) {
        Log.i(TAG, "sendNotification: $msg")

        // Intent to start the main Activity
        val notificationIntent = MainActivity.makeNotificationIntent(
            applicationContext, msg
        )
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(notificationIntent)
        val notificationPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)


        // Creating and sending Notification
        val notificatioMng = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificatioMng.notify(
            GEOFENCE_NOTIFICATION_ID,
            createNotification(msg, notificationPendingIntent)
        )
    }

    // Create notification
    private fun createNotification(
        msg: String,
        notificationPendingIntent: PendingIntent
    ): Notification {
        val notificationBuilder = NotificationCompat.Builder(this)
        notificationBuilder
            .setSmallIcon(R.drawable.ic_action_location)
            .setColor(Color.RED)
            .setContentTitle(msg)
            .setContentText("Geofence Notification!")
            .setContentIntent(notificationPendingIntent)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            .setAutoCancel(true)
        return notificationBuilder.build()
    }

    companion object {
        private val TAG = GeofenceTrasitionService::class.java.simpleName
        const val GEOFENCE_NOTIFICATION_ID = 0
        private fun getErrorString(errorCode: Int): String {
            return when (errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GeoFence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many GeoFences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many pending intents"
                else -> "Unknown error."
            }
        }
    }
}