package com.straz.task_7_6_alarm_clock.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.straz.task_7_6_alarm_clock.R
import com.straz.task_7_6_alarm_clock.`object`.APP
import com.straz.task_7_6_alarm_clock.`object`.getDate
import com.straz.task_7_6_alarm_clock.`object`.weeks
import com.straz.task_7_6_alarm_clock.`object`.zero
import com.straz.task_7_6_alarm_clock.databinding.RvItemBinding
import com.straz.task_7_6_alarm_clock.models.AlarmTime

class AdapterHome(val list: ArrayList<AlarmTime>, val listener: ItemSelectListener) :
    RecyclerView.Adapter<AdapterHome.Vh>() {
    inner class Vh(val itemV: RvItemBinding) : RecyclerView.ViewHolder(itemV.root) {
        @SuppressLint("SetTextI18n")
        fun onClick(alarm: AlarmTime, pos: Int) {
            itemV.tvTime.text = "${alarm.hour.zero()}:${alarm.minute.zero()}"
            setColors(alarm)
            if (alarm.date > 0) {
                itemV.tvDays.text = alarm.date.getDate()
            } else {
                var text = ""
                for (it in weeks.indices) {
                    if (alarm.days[it]) {
                        if (it == 0) {
                            text = weeks[it]
                        } else {
                            text = "$text, ${weeks[it]}"
                        }
                    }
                }
                val weekTrues = alarm.days.filter { it }
                if (weekTrues.size == 7) {
                    itemV.tvDays.text = "Har kuni"
                } else if (weekTrues.isEmpty()) {
                    itemV.tvDays.text = ""
                } else {
                    itemV.tvDays.text = "Har: $text"
                }

            }


            itemV.lHome.setOnClickListener { listener.onClick(alarm, pos) }

            itemV.switchAlarm.setOnCheckedChangeListener {
                listener.onCheckedChanged(alarm, pos, it)
            }
            itemV.lHome.setOnLongClickListener {
                listener.onLongClick(alarm, pos)
                true
            }
        }

        private fun setColors(alarm: AlarmTime) {
            if (alarm.soundness) {
                itemV.switchAlarm.setChecked(true)
                itemV.tvTime.setTextColor(APP.resources.getColor(R.color.black, null))
                itemV.tvDays.setTextColor(APP.resources.getColor(R.color.black, null))

            } else {
                itemV.switchAlarm.setChecked(false)
                itemV.tvTime.setTextColor(APP.resources.getColor(R.color.grey, null))
                itemV.tvDays.setTextColor(APP.resources.getColor(R.color.grey, null))

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onClick(list[position], position)
    }

    override fun getItemCount() = list.size

    interface ItemSelectListener {
        fun onClick(alarmTime: AlarmTime, pos: Int)
        fun onLongClick(alarmTime: AlarmTime, pos: Int)
        fun onCheckedChanged(alarmTime: AlarmTime, pos: Int, isChecked: Boolean)

    }
}