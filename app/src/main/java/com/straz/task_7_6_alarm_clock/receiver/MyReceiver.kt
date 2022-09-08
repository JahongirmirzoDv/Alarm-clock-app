package com.straz.task_7_6_alarm_clock.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.straz.task_7_6_alarm_clock.MainActivity
import com.straz.task_7_6_alarm_clock.`object`.*

class MyReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("TAG", "onReceive: ")
        initPref(context)
        playSound(context, ALARM_URL_PREF ?: "")
        setNextAlarm(context)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val i=Intent(context, MainActivity::class.java)
            i.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(i)
        }
    }
}