package com.straz.task_7_6_alarm_clock.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kevalpatel.ringtonepicker.RingtonePickerDialog
import com.kevalpatel.ringtonepicker.RingtonePickerListener
import com.straz.task_7_6_alarm_clock.R
import com.straz.task_7_6_alarm_clock.`object`.ALARM_NAME_PREF
import com.straz.task_7_6_alarm_clock.`object`.ALARM_URL_PREF
import com.straz.task_7_6_alarm_clock.`object`.APP
import com.straz.task_7_6_alarm_clock.databinding.RvBinding


class FragmentListAlarms : Fragment() {
    private var _binding: RvBinding? = null
    private val b get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.fragmentHome,true)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RvBinding.inflate(layoutInflater)
        val ringtonePickerBuilder = RingtonePickerDialog.Builder(
            requireContext(),
            requireActivity().getSupportFragmentManager()
        )
            .setTitle("Tanlash") //set the currently selected uri, to mark that ringtone as checked by default.
            .setCurrentRingtoneUri(null) //Set true to allow allow user to select default ringtone set in phone settings.
            .displayDefaultRingtone(true) //Set true to allow user to select silent (i.e. No ringtone.).
            .displaySilentRingtone(true) //set the text to display of the positive (ok) button.
            .setPositiveButtonText("Tanlash") //set text to display as negative button.
            .setCancelButtonText("Bekor   ") //Set flag true if you want to play the sample of the clicked tone.
            .setPlaySampleWhileSelection(true) //Set the callback listener.
            .setListener(object : RingtonePickerListener {
                override fun OnRingtoneSelected(ringtoneName: String, ringtoneUri: Uri?) {
                    ALARM_NAME_PREF = ringtoneName
                    ALARM_URL_PREF = ringtoneUri.toString()
                    findNavController().popBackStack()

                }

            })

//        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC)
//        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION)
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE)
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM)
        ringtonePickerBuilder.show()

        return b.root
    }
}

