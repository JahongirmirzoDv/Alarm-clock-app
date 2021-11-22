package com.straz.task_7_6_alarm_clock.`object`

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.straz.task_7_6_alarm_clock.MainActivity
import com.straz.task_7_6_alarm_clock.R
import com.straz.task_7_6_alarm_clock.models.AlarmTime
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import kotlin.collections.ArrayList


lateinit var APP: MainActivity


private const val MODE = Context.MODE_PRIVATE
private const val NAME = "MySharePref"
private lateinit var preferences: SharedPreferences

var isPaused = true
var listAlarms = ArrayList<AlarmTime>()
val alarmSounds = listOf(R.raw.alarm1, R.raw.alarm2, R.raw.alarm3)
val weeks = listOf("Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya")

fun initApp(ac: MainActivity) {
    APP = ac
    initPref(ac)
    initAlarms()

}

fun initPref(context: Context) {
    preferences = context.getSharedPreferences(NAME, MODE)
}

private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = edit()
    operation(editor)
    editor.apply()
}

fun addAlarm(alarm: AlarmTime) {
    val gson = Gson()
    listAlarms.add(alarm)
    listAlarms.sortBy { it.hour }
    ALARMS_PREF = gson.toJson(listAlarms)
}

fun updateAlarm(alarm: AlarmTime) {
    val gson = Gson()
    val index = listAlarms.indexOf(alarm)
    listAlarms.set(index, alarm)
    listAlarms.sortBy { it.hour }
    ALARMS_PREF = gson.toJson(listAlarms)
}

fun removeAlarm(alarm: AlarmTime) {
    val gson = Gson()
    if (!listAlarms.contains(alarm)) {
        listAlarms.remove(alarm)
        listAlarms.sortBy { it.hour }
        ALARMS_PREF = gson.toJson(listAlarms)
    }
}
var CURRENT_ALARM_PREF: String?
    get() = preferences.getString("current_alarm", "")
    set(value) = preferences.edit {
        if (value != null) {
            it.putString("current_alarm", value)
        }
    }
var ALARMS_PREF: String?
    get() = preferences.getString("alarms", "")
    set(value) = preferences.edit {
        if (value != null) {
            it.putString("alarms", value)
        }
    }
var ALARM_URL_PREF: String?
    get() = preferences.getString("alarm_sound", "")
    set(value) = preferences.edit {
        it.putString("alarm_sound", value)
    }
var ALARM_NAME_PREF: String?
    get() = preferences.getString("alarm_name", "")
    set(value) = preferences.edit {
        it.putString("alarm_name", value)
    }
var ALARM_SOUNDNESS: Boolean
    get() = preferences.getBoolean("alarm_soundness", true)
    set(value) = preferences.edit {
        it.putBoolean("alarm_soundness", value)
    }
var ALARM_VIBRATE: Boolean
    get() = preferences.getBoolean("alarm_vibrate", false)
    set(value) = preferences.edit {
        it.putBoolean("alarm_vibrate", value)
    }

fun initAlarms() {
    val gson = Gson()
    val strPref = ALARMS_PREF ?: ""
    if (strPref != "") {
        val type = object : TypeToken<ArrayList<AlarmTime>>() {}.type
        listAlarms = gson.fromJson<ArrayList<AlarmTime>>(ALARMS_PREF, type)
        listAlarms.sortBy { it.hour }

    }
}

fun String.toToast() {
    Toast.makeText(APP, this, Toast.LENGTH_SHORT).show()
}

fun hideKeyBoard() {
    val view = APP.currentFocus
    if (view != null) {
        val inm: InputMethodManager =
            APP.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun View.showKeyboard(force: Boolean = false) {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (force) {
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.getDate() = SimpleDateFormat("EE, dd MMM, HH:mm").format(Date(this))
fun Long.toTime(): String {
    var text = ""
    val time = this / 60_000
    if (time % 1440 == 0L) {
        text = "${time / 1440} kun"
    } else if (time > 1440) {
        text = "${time / 1440} kun, ${(time % 1440)/60} soat"
    } else if (time < 60) {
        text = "${time} min"
    } else if (time % 60 == 0L) {
        text = "${time / 60} soat"
    } else {
        text = "${time / 60} soat, ${time % 60} min"
    }
    return text
}

fun Int.getDayOfWeek() = when (this) {
    0 -> MONDAY
    1 -> TUESDAY
    2 -> WEDNESDAY
    3 -> THURSDAY
    4 -> FRIDAY
    5 -> SATURDAY
    else -> SUNDAY
}

fun Int.zero() = if (this < 10) "0$this" else this.toString()

var mediaPlayer: MediaPlayer? = null
var vibrator: Vibrator? = null

@SuppressLint("ServiceCast")
fun playSound(ctx: Context, url: String) {
    Log.d("main", "playSound  ")

    if (mediaPlayer != null) {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        mediaPlayer = null
        vibrator?.cancel()
    }

    val resId = ctx.getResources()?.getIdentifier(
        //R.raw.send_message.toString(),
        alarmSounds[0].toString(),
        "raw", ctx.packageName
    )
    if (url == "") {
        mediaPlayer = MediaPlayer.create(ctx, resId!!)

    } else {
        mediaPlayer = MediaPlayer.create(ctx, url.toUri())
    }

    val audioDuration = (mediaPlayer?.duration ?: 0).toLong()
    if (ALARM_VIBRATE) {
        vibrate(audioDuration, ctx)
    }

    //mediaPlayer?.setAudioAttributes(AudioAttributes.Builder(). .build())
    mediaPlayer?.start()
}

fun changeVolume(progress: Int) {
    val audioManager = APP.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

}

@SuppressLint("ServiceCast")
fun vibrate(audioDuration: Long, ctx: Context) {
    vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator?.vibrate(
            VibrationEffect.createOneShot(
                audioDuration,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        vibrator?.vibrate(audioDuration)
    }

}