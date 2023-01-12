package com.example.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.app.di.app.*
import com.example.library.di.AppContainer
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.ANDROID_RESOURCE
import com.example.library.utils.Constants.CHANNEL_ID
import com.example.library.utils.Constants.notificationSound

class Application : Application(), HasAppComponent {
    private val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
     override fun onCreate() {
         super.onCreate()
         // Звук нотификации, привязка к ресурсу
         notificationSound = Uri.parse(
             ANDROID_RESOURCE + packageName +
                     "/" + com.android.car.ui.R.raw.car_ui_keep
         )
         // Создание канала нотификаций
         createNotificationChannel()
     }

     // Создание и регистрация канала нотификаций, без него нотификации не сработают на Android ver.8
     // и выше
     private fun createNotificationChannel() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val name = "Message"
             val descriptionText = "Birthday Reminder"
             val importance = NotificationManager.IMPORTANCE_HIGH
             val audioAttributes = AudioAttributes.Builder()
                 .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                 .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                 .build()
             val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                 setImportance(importance)
                 description = descriptionText
                 lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                 setSound(notificationSound, audioAttributes)
                 enableVibration(true)
                 setShowBadge(true)
             }
             // Регистрация канала в системе
             val notificationManager: NotificationManager =
                 getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
             notificationManager.createNotificationChannel(channel)
         }
     }
    override fun getAppComponent(): AppContainer = appComponent
}