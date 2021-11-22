package com.straz.task_7_6_alarm_clock.models

import java.io.Serializable
import kotlin.collections.ArrayList

data class AlarmTime(
    var id: Int,
    var date: Long=0,
    var hour: Int=6,
    var minute: Int=0,
    var days: ArrayList<Boolean> = arrayListOf(false,false, false, false, false, false, false),
    var name: String = "",
    var url: String = "",
    var soundName: String= "",
    var interval: Int = 5,
    var repeat: Int = 3,
    var soundness: Boolean = true,
    var vibration: Boolean = false,
    var pause: Boolean = false,

    ) : Serializable {
    override fun equals(other: Any?): Boolean {
        return (other as AlarmTime).id == id
    }
}