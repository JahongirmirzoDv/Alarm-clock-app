package com.straz.task_7_6_alarm_clock.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.straz.task_7_6_alarm_clock.`object`.CURRENT_ALARM_PREF
import com.straz.task_7_6_alarm_clock.`object`.cancelAlarm
import com.straz.task_7_6_alarm_clock.`object`.mediaPlayer
import com.straz.task_7_6_alarm_clock.databinding.FragmentAlarmBinding
import com.straz.task_7_6_alarm_clock.models.AlarmTime

class FragmentAlarm : Fragment() {
    private var _binding: FragmentAlarmBinding? = null
    private val b get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(layoutInflater)
        b.btnStop.setOnClickListener {
            mediaPlayer?.release()
            mediaPlayer = null
            val gson = Gson()
            val alarm = gson.fromJson(CURRENT_ALARM_PREF, AlarmTime::class.java)
            cancelAlarm(requireContext(), alarm)
            findNavController().popBackStack()
        }
        return b.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}