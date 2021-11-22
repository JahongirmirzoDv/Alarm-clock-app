package com.straz.task_7_6_alarm_clock.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.straz.task_7_6_alarm_clock.R
import com.straz.task_7_6_alarm_clock.`object`.*
import com.straz.task_7_6_alarm_clock.adapters.AdapterHome
import com.straz.task_7_6_alarm_clock.databinding.FragmentHomeBinding
import com.straz.task_7_6_alarm_clock.models.AlarmTime
import java.util.*
import java.util.Calendar.*
import kotlin.collections.HashMap


class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var alarm: AlarmTime? = null
    private val b get() = _binding!!
    private lateinit var adapter: AdapterHome
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        return b.root
    }

    override fun onStart() {
        super.onStart()
        b.add.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentAdd)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                findNavController().navigate(R.id.action_fragmentHome_to_fragmentAlarm)
            }
        } else {

            adapter = AdapterHome(listAlarms, object : AdapterHome.ItemSelectListener {
                override fun onClick(alarmTime: AlarmTime, pos: Int) {
                    findNavController().navigate(
                        R.id.action_fragmentHome_to_fragmentEdit,
                        bundleOf("alarmEdit" to alarmTime)
                    )
                }

                override fun onLongClick(alarmTime: AlarmTime, pos: Int) {
                    showDialog(alarmTime, pos)
                }

                override fun onCheckedChanged(alarmTime: AlarmTime, pos: Int, isChecked: Boolean) {
                    alarmTime.soundness = isChecked
                    setChangedItem(pos, alarmTime)

                }
            })
            b.recyclerView.adapter = adapter
            setTitles()
            initAlarmAdd()
        }
    }

    private fun initAlarmAdd() {
        if (listAlarms.isEmpty()) {
            b.add.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentHome_to_fragmentAdd)
            }
        }
    }

    private fun setChangedItem(
        pos: Int,
        alarmTime: AlarmTime
    ) {
        listAlarms.set(pos, alarmTime)
        //adapter.notifyItemChanged(pos)
        updateAlarm(alarmTime)
        setNextAlarm(requireContext())
        setTitles()
    }

    private fun showDialog(
        alarmTime: AlarmTime,
        pos: Int
    ) {
        val builder = AlertDialog.Builder(APP)
        builder.setCancelable(true)
        builder.setTitle("O'chirish")
        builder.setMessage("O'chirishga ishonchingiz komilmi?")
        builder.setNegativeButton("Bekor"
        ) { dialog, which -> dialog?.cancel() }
        builder.setPositiveButton("Ha"
        ) { dialog, which ->
            listAlarms.remove(alarmTime)
            adapter.notifyItemRemoved(pos)
            adapter.notifyItemRangeChanged(pos, listAlarms.size)
            removeAlarm(alarmTime)
            cancelAlarm(requireContext(), alarmTime)
            setTitles()
            initAlarmAdd()
            dialog?.cancel()
        }
        builder.create().show()
    }

    @SuppressLint("SetTextI18n")
    private fun setTitles() {
        val cal = Calendar.getInstance()
        val now = System.currentTimeMillis()

        val filter1 = listAlarms.filter {
            it.soundness && (it.days.filter { it }.isNotEmpty() || it.date > now)
        }
        if (filter1.isNotEmpty()) {
            val map = HashMap<Long, AlarmTime>()
            filter1.forEach { alar ->
                if (alar.date > 0) {
                    map.put(alar.date, alar)
                } else if (alar.days.filter { it }.isNotEmpty()) {
                    for (i in weeks.indices) {
                        if (alar.days[i]) {
                            cal[DAY_OF_WEEK] = i.getDayOfWeek()
                            cal[HOUR] = alar.hour
                            cal[MINUTE] = alar.minute
                            if (cal.timeInMillis > now)
                                map.put(cal.timeInMillis, alar)
                        }
                    }
                }
            }
        }
        initAlarmAdd()
    }
}