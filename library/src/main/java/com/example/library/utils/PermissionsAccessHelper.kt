package com.example.library.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.example.library.utils.Constants.TAG

object PermissionsAccessHelper {

    private const val PERMISSION_REQUEST_CODE = 123
    private const val DIALOG_TAG = "permiss"
    private var requiredPermissions = arrayOf<String>()
    private val introDialog = IntroDialogFragment()
    private val epilogueDialog = EpilogueDialogFragment()

    // Функция проверяет доступность заданных в permissions разрешений (но не запрашивает их)
    private fun isPermissionsAvailable(context: Context, permissions: Array<String>): Boolean {
        var availability = true
        for (permission in permissions) {
            val checkPermission = ActivityCompat.checkSelfPermission(context, permission)
            Log.d(TAG, "$permission: $checkPermission")
            if (checkPermission != PackageManager.PERMISSION_GRANTED) availability = false
        }
        return availability
    }

    // Функция запрашивает заданные в permissions разрешения у пользователя.
    // showIntro == true перед запросом разрешений выводит на экран предупреждение (диалог),
    // оповещающее пользователя о последующем запросе.
    fun startPermissionsRequest(
        activity: AppCompatActivity,
        permissions: Array<String>,
        showIntro: Boolean
    ) {
        requiredPermissions = permissions
        Log.d(TAG, "Начинаю запрос разрешений у пользователя...")
        if (showIntro) { // Показать предупреждение пользователю перед запросом разрешений...
            if (!isPermissionsAvailable(activity.applicationContext, permissions)) {
                introDialog.isCancelable = false
                introDialog.show(activity.supportFragmentManager, DIALOG_TAG)
            } else {
                activity.requestPermissions(requiredPermissions, PERMISSION_REQUEST_CODE)
            }
        }
        // иначе запросить разрешения без предупреждения
        else {
            activity.requestPermissions(requiredPermissions, PERMISSION_REQUEST_CODE)
        }
    }

    // Функция предназначена для вызова в колбеке onRequestPermissionsResult, для разбора
    // результатов запроса разрешений у пользователя, запущенного функцией startPermissionsRequest.
    // Здесь showEpilogue == true при отказе пользователя от дачи разрешений выводит на экран
    // диалог, в котором можно повторно запустить процесс выдачи разрешений, либо выйти
    // из приложения.
    // При showEpilogue == false диалог не выводится, функция завершает работу. При этом
    // если разрешения были даны пользователем, функция возвращает значение true, иначе - false.
    fun isPermissionsRequestSuccessful(
        activity: AppCompatActivity,
        showEpilogue: Boolean,
    ): Boolean {
        var success = true
        Log.d(TAG, "Запрос разрешений окончен. Результат:")
        if (!isPermissionsAvailable(activity.applicationContext, requiredPermissions)) {
            success = false
            if (showEpilogue) {
                epilogueDialog.isCancelable = false
                epilogueDialog.show(activity.supportFragmentManager, DIALOG_TAG)
            }
        } else {
            Log.d(TAG, "Все разрешения получены.")
        }
        return success
    }

    // Класс для создания диалога-предупреждения пользователя о запуске запроса разрешений
    class IntroDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return requireActivity().let {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Permissions required")
                    .setMessage(
                        "Now you will be asked for permissions, " +
                            "without which the application will not be able to work. " +
                            "Please confirm all requested permissions."
                    )
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requireActivity().requestPermissions(
                            requiredPermissions,
                            PERMISSION_REQUEST_CODE
                        )
                    }
                builder.create()
            }
        }
    }

    // Класс для создания диалога повторного запроса разрешений у пользователя после того,
    // как он отказался их дать с первого раза
    class EpilogueDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return requireActivity().let {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Permissions required")
                    .setMessage("Please accept all requested permissions. Without them, the application can not work.")
                    .setPositiveButton("Grant permissions") { _, _ ->
                        Log.d(TAG, "Повторный запрос разрешений.")
                        requireActivity().requestPermissions(
                            requiredPermissions,
                            PERMISSION_REQUEST_CODE
                        )
                    }
                    .setNegativeButton("No, exit the app") { _, _ ->
                        Log.d(TAG, "Отказ в выдаче разрешений! Работа приложения завершена.")
                        requireActivity().finish()
                    }
                builder.create()
            }
        }
    }
}
