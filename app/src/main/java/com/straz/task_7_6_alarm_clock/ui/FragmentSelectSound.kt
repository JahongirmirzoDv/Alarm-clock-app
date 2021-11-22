package com.straz.task_7_6_alarm_clock.ui

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.straz.task_7_6_alarm_clock.R
import com.straz.task_7_6_alarm_clock.`object`.*
import com.straz.task_7_6_alarm_clock.databinding.FragmentSelectSoundBinding

class FragmentSelectSound : Fragment() {
    private var _binding: FragmentSelectSoundBinding? = null
    private val b get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectSoundBinding.inflate(layoutInflater)
        if (ALARM_NAME_PREF == "" || ALARM_NAME_PREF == null) {
            val t = resources.getText(alarmSounds[0])
            b.tvSoundName.text = t.substring(t.lastIndexOf("/") + 1, t.length)
        } else {
            b.tvSoundName.text = ALARM_NAME_PREF
        }
        b.ivBack.setOnClickListener { findNavController().popBackStack() }
        b.lSound.setOnClickListener { findNavController().navigate(R.id.action_fragmentSelectSound_to_fragmentListAlarms) }
        b.switchSound.setOnCheckedChangeListener { buttonView, isChecked ->
            ALARM_SOUNDNESS = isChecked
            setSoundMode(isChecked)
        }
        b.switchSound.isChecked = ALARM_SOUNDNESS
        setSoundMode(ALARM_SOUNDNESS)
        val audioManager = APP.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        b.seekSelect.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        b.seekSelect.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return b.root
    }

    private fun setSeekListener() {
        b.seekSelect.isEnabled=true
        b.seekSelect.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer == null) {
                    ALARM_URL_PREF?.let { playSound(requireContext(), it) }

                }
                changeVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

    }

    private fun setSoundMode(isChecked: Boolean) {
        if (isChecked) {
            b.lOffOn.setBackgroundResource(R.drawable.back_blue_30)
            b.tvOnOff.text = "Yoniq"
            b.tvOnOff.setTextColor(Color.WHITE)
            b.rlMask.visibility = View.GONE
            b.lSound.setOnClickListener { findNavController().navigate(R.id.action_fragmentSelectSound_to_fragmentListAlarms) }
            setSeekListener()
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            b.lOffOn.setBackgroundResource(R.drawable.back_white_30)
            b.tvOnOff.text = "O'chiq"
            b.tvOnOff.setTextColor(resources.getColor(R.color.grey, null))
            b.rlMask.visibility = View.VISIBLE
            b.lSound.setOnClickListener(null)
            b.seekSelect.setOnSeekBarChangeListener(null)
            b.seekSelect.isEnabled=false
        }
    }

    override fun onResume() {
        super.onResume()
        b.tvSoundName.text = ALARM_NAME_PREF
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}