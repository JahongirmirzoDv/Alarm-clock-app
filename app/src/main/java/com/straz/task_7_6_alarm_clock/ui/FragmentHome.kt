package com.straz.task_7_6_alarm_clock.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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

                }

                override fun onCheckedChanged(alarmTime: AlarmTime, pos: Int, isChecked: Boolean) {
                    alarmTime.soundness = isChecked
                    setChangedItem(pos, alarmTime)
                }
            }, requireContext())
            b.recyclerView.adapter = adapter

            var touch = object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    var swipe = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipe)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.delete(viewHolder.adapterPosition)
                }

            }
            var itemTouchHelper = ItemTouchHelper(touch)
            itemTouchHelper.attachToRecyclerView(b.recyclerView)
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
                    map[alar.date] = alar
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