package com.straz.task_7_6_alarm_clock.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.straz.task_7_6_alarm_clock.R
import com.straz.task_7_6_alarm_clock.`object`.isPaused
import com.straz.task_7_6_alarm_clock.databinding.FragmentPauseBinding
import com.straz.task_7_6_alarm_clock.ui.FragmentAdd.Companion.interval
import com.straz.task_7_6_alarm_clock.ui.FragmentAdd.Companion.repeatAmount

class FragmentPause : Fragment() {
    private var _binding: FragmentPauseBinding? = null
    private val b get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPauseBinding.inflate(layoutInflater)

        b.ivBack.setOnClickListener { findNavController().popBackStack() }

        b.switchPause.setOnCheckedChangeListener { buttonView, isChecked ->
            isPaused = isChecked
            setSoundMode(isChecked)
        }
        b.rgInterval.setOnCheckedChangeListener { group, checkedId ->

            if (b.rb5.id == checkedId) {
                interval = 5
            } else if (b.rb10.id == checkedId) {
                interval = 10
            } else if (b.rb15.id == checkedId) {
                interval = 15
            } else if (b.rb30.id == checkedId) {
                interval = 30
            }
        }
        b.rgRepeat.setOnCheckedChangeListener { group, checkedId ->

            if (b.rbRepeat3.id == checkedId) {
                repeatAmount = 3
            } else if (b.rbRepeat5.id == checkedId) {
                repeatAmount = 5
            } else if (b.rbRepeatEndless.id == checkedId) {
                repeatAmount = 1000
            }
        }
        b.switchPause.isChecked = isPaused
        setSoundMode(isPaused)

        return b.root
    }


    private fun setSoundMode(isChecked: Boolean) {
        if (isChecked) {
            b.lOffOn.setBackgroundResource(R.drawable.back_blue_30)
            b.tvOnOff.text = "Yoniq"
            b.tvOnOff.setTextColor(Color.WHITE)
            b.rlMask.visibility = View.GONE

        } else {
            b.lOffOn.setBackgroundResource(R.drawable.back_white_30)
            b.tvOnOff.text = "O'chiq"
            b.tvOnOff.setTextColor(resources.getColor(R.color.grey, null))
            b.rlMask.visibility = View.VISIBLE

        }
    }


}