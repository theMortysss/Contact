package com.example.library.repository.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.java.entities.Contact
import com.example.library.utils.Constants.TAG
import com.example.java.interfaces.IBirthdayRepository
import com.example.library.receiver.BirthdayReceiver
import com.example.library.utils.Constants.BIRTHDAY_MESSAGE
import com.example.library.utils.Constants.CONTACT_ID
import java.util.*
import javax.inject.Inject

private const val LEAP_YEAR_MILLISECONDS =
    1000 * 60 * 60 * 24 * 366L // кол-во миллисекунд в високосном году
private const val NORMAL_YEAR_MILLISECONDS =
    1000 * 60 * 60 * 24 * 365L // кол-во миллисекунд в обычном году
private const val LEAP_YEAR_PERIOD = 4

class BirthdayRepository @Inject constructor(
    private val appContext: Context
) : IBirthdayRepository {

    override fun isBirthdayAlarmOn(curContact: Contact) = PendingIntent.getBroadcast(
        appContext,
        curContact.id.hashCode(),
        Intent(appContext, BirthdayReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    ) != null

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun setBirthdayAlarm(curContact: Contact, alarmStartMoment: Calendar) {
        if (curContact.birthday != null) {
            val birthday = curContact.birthday as Calendar
            val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmPendingIntent = Intent(appContext, BirthdayReceiver::class.java)
                .let { intent ->
                    intent.putExtra(
                        BIRTHDAY_MESSAGE,
                        "Сегодня день рождения " + curContact.name
                    )
                    intent.putExtra(CONTACT_ID, curContact.id)
                    PendingIntent.getBroadcast(
                        appContext,
                        curContact.id.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartMoment.timeInMillis,
                if (isLeapYear(birthday)) LEAP_YEAR_MILLISECONDS else NORMAL_YEAR_MILLISECONDS,
                alarmPendingIntent
            )
            Log.d(TAG, "Аларм установлен на $alarmStartMoment")
            Toast.makeText(
                appContext,
                "Напоминание установлено",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun cancelBirthdayAlarm(curContact: Contact) {
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmPendingIntent = PendingIntent.getBroadcast(
            appContext,
            curContact.id.hashCode(),
            Intent(appContext, BirthdayReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(alarmPendingIntent)
        alarmPendingIntent.cancel()
        Toast.makeText(
            appContext,
            "Напоминание отменено",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun isLeapYear(date: Calendar) = date[Calendar.YEAR] % LEAP_YEAR_PERIOD == 0

}