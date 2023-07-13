package com.example.library.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.library.MainActivity
import com.example.library.utils.Constants.BIRTHDAY_MESSAGE
import com.example.library.utils.Constants.CHANNEL_ID
import com.example.library.utils.Constants.CONTACT_ID
import com.example.library.utils.Constants.TAG

class BirthdayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // val bundleData = intent.extras

        val bundleData = intent.extras
//        findNavController(SplashFragment().view!!.rootView)
//            .navigate(R.id.contactDetailsFragment2, bundleData, null)

        if (bundleData != null) {
            val contactId = bundleData.getString(CONTACT_ID)
            val notificationIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(CONTACT_ID, contactId)
                addFlags(
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
            val notificationPendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                contactId.hashCode(),
                notificationIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.star_big_on)
                .setContentTitle("Не забудь!")
                .setContentText(bundleData.getString(BIRTHDAY_MESSAGE))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // показать на lockscreen
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
            // Запустить отображение нотификации на экране
            NotificationManagerCompat.from(context)
                .notify(TAG, contactId.hashCode(), notificationBuilder.build())
        }
    }
}
