package com.straz.task_7_6_alarm_clock.`object`

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.straz.task_7_6_alarm_clock.models.AlarmTime
import com.straz.task_7_6_alarm_clock.receiver.MyReceiver
import java.util.*

fun setNextAlarm(ctx: Context) {
    val cal = Calendar.getInstance()
    val now = System.currentTimeMillis()

    val filter1 = listAlarms.filter {
        it.soundness && (it.days.filter { it }.isNotEmpty() || it.date > now)
    }
    if (filter1.isNotEmpty()) {
        val map = HashMap<Long, AlarmTime>()
        filter1.forEach { alar ->
            if (alar.date > now) {
                map.put(alar.date, alar)
            }
            if (alar.days.filter { it }.isNotEmpty()) {
                for (i in weeks.indices) {
                    if (alar.days[i]) {
                        cal[Calendar.DAY_OF_WEEK] = i.getDayOfWeek()
                        cal[Calendar.HOUR] = alar.hour
                        cal[Calendar.MINUTE] = alar.minute
                        if (cal.timeInMillis > now)
                            map.put(cal.timeInMillis, alar)
                    }
                }
            }
        }
        try {
            val nextAlarmTime = map.keys.minOf { it }
            val alarmTime = map[nextAlarmTime]!!
            val gson = Gson()
            CURRENT_ALARM_PREF = gson.toJson(alarmTime)
            val receiver = ComponentName(ctx, MyReceiver::class.java)
            ctx.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            val intent = Intent(ctx, MyReceiver::class.java)

            if (alarmTime.pause) {
                val interval = alarmTime.interval * 60_000
                for (i in 0 until alarmTime.repeat) {
                    val pendingIntent =
                        PendingIntent.getBroadcast(ctx, "${alarmTime.id}$i".toInt(), intent, 0)
                    val alarmManager =
                        ctx.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

                    setAlarm(alarmManager, nextAlarmTime + i * interval, pendingIntent)
                }
            } else {
                val pendingIntent = PendingIntent.getBroadcast(ctx, alarmTime.id, intent, 0)
                val alarmManager =
                    ctx.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

                setAlarm(alarmManager, nextAlarmTime, pendingIntent)
            }
        } catch (e: Exception) {
        }


    }
}

fun cancelAlarm(ctx: Context, alarmTime: AlarmTime) {

    val receiver = ComponentName(ctx, MyReceiver::class.java)
    ctx.packageManager.setComponentEnabledSetting(
        receiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
    val intent = Intent(ctx, MyReceiver::class.java)
    if (alarmTime.pause) {
        for (i in 0 until alarmTime.repeat) {
            val pendingIntent =
                PendingIntent.getBroadcast(ctx, "${alarmTime.id}$i".toInt(), intent, 0)
            val alarm = ctx.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            alarm.cancel(pendingIntent)
        }
    } else {
        val pendingIntent = PendingIntent.getBroadcast(ctx, alarmTime.id, intent, 0)
        val alarm = ctx.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pendingIntent)
    }

}

private fun setAlarm(
    alarmManager: AlarmManager,
    time: Long,
    pendingIntent: PendingIntent?
) {

    alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        time,
        pendingIntent
    )
}

