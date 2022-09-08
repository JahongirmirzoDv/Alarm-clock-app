package com.straz.task_7_6_alarm_clock.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.straz.task_7_6_alarm_clock.R
import com.straz.task_7_6_alarm_clock.`object`.*
import com.straz.task_7_6_alarm_clock.databinding.FragmentAddAlarmBinding
import com.straz.task_7_6_alarm_clock.models.AlarmTime
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FragmentEdit : Fragment() {
    private var _binding: FragmentAddAlarmBinding? = null
    private var alarmEdit: AlarmTime? = null
    private lateinit var weekViews: List<TextView>

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val b get() = _binding!!
    private var weeksBool = arrayListOf(false, false, false, false, false, false, false)
    private var weekDays = TreeSet<Int>()
    private var hour = 6
    private var minute = 0
    private var selectedDate = ""
    private var selectedYear = -1
    private var selectedMonth = -1
    private var selectedDay = -1

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAlarmBinding.inflate(layoutInflater)

        arguments?.let {
            alarmEdit = it.get("alarmEdit") as AlarmTime
        }
        weekViews = listOf(
            b.tvMonday,
            b.tvTuesday,
            b.tvThurthday,
            b.tvWensday,
            b.tvFriday,
            b.tvSaturday,
            b.tvSunday
        )
        if (alarmEdit != null) {
            interval = alarmEdit!!.interval
            repeatAmount = alarmEdit!!.repeat
            hour = alarmEdit!!.hour
            minute = alarmEdit!!.minute
            soundness = alarmEdit!!.soundness
            Log.d("main", "weeksBool : $weeksBool")
            Log.d("main", "alarmEdit.days : ${alarmEdit!!.days}")

            weeksBool = alarmEdit!!.days
//            b.etNameAlarm.setText(alarmEdit!!.name)
            b.timePicker.setIs24HourView(true)
            b.timePicker.hour = alarmEdit!!.hour
            b.timePicker.minute = alarmEdit!!.minute

//            b.tvPause.text = "$interval min, $repeatAmount marta"
//            b.tvVibration.text = "yoniq"

            b.tvSave.setOnClickListener {
                soundness = true
                ALARM_SOUNDNESS = true
                saveAlarm()
            }
            b.tvExit.setOnClickListener {
                soundness = false
                ALARM_SOUNDNESS = false
                findNavController().popBackStack()
            }
//            b.ivCalendar.setOnClickListener {
//                showCalendar()
//            }

            b.lAlarmSound.setOnClickListener {
                findNavController().navigate(R.id.fragmentListAlarms)
            }

            b.switchVibrate.setOnCheckedChangeListener {
                ALARM_VIBRATE = it
            }

//            b.switchSound.setOnCheckedChangeListener { btn, isChecked ->
//                soundness = isChecked
//                ALARM_SOUNDNESS = isChecked
//                setAlarmName(isChecked)
//            }
//            b.lPause.setOnClickListener {
//                findNavController().navigate(R.id.action_fragmentEdit_to_fragmentPause)
//            }
//            b.switchPause.setOnCheckedChangeListener { btn, isChecked ->
//                isPaused = isChecked
//                setInterval(isChecked)
//
//            }
            b.timePicker.setOnTimeChangedListener { view, hourOfDay, minute1 ->
                val rightNow = Calendar.getInstance()
                val now = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.now()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                val currentHourIn24Format: Int =
                    rightNow.get(Calendar.HOUR_OF_DAY) // return the hour in 24 hrs format (ranging from 0-23)
                val currentHourIn12Format: Int = rightNow.get(Calendar.HOUR)
                val currentMinute: Int = rightNow.get(Calendar.MINUTE)
                val timeH = hourOfDay - currentHourIn24Format
                val timeM = minute1 - currentMinute
//            if (timeH<0){
//                val dayOfWeek = now.dayOfWeek.value
//                weeksBool[dayOfWeek] = true
//                initViews()
//            }
                if (timeH == 0)
                    hour = hourOfDay
                minute = minute1
                b.hour1.text = " ${timeH.toString().trim('-')} hour ${
                    timeM.toString().trim('-')
                } minute"
            }
        }
        setWeekListeners()
        setAlarmName()
        return b.root
    }

    private fun setAlarmName() {

        if (ALARM_URL_PREF == "" || ALARM_URL_PREF == null) {
            val t = resources.getText(alarmSounds[0])
            b.tvSoundAlarm.text = t.substring(t.lastIndexOf("/") + 1, t.length)
        } else {
            b.tvSoundAlarm.text = ALARM_NAME_PREF
        }

    }

//    private fun setInterval(isChecked: Boolean) {
//        if (!isChecked)
//            b.tvPause.text = "o'chiq"
//        else
//            b.tvPause.text = "$interval min, $repeatAmount marta"
//    }

//    override fun onResume() {
//        super.onResume()
//        b.switchSound.isChecked = ALARM_SOUNDNESS
//        b.switchPause.isChecked = isPaused
//        setInterval(isPaused)
//        setAlarmName(ALARM_SOUNDNESS)
//        initViews()
//    }

    fun saveAlarm() {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0


        alarmEdit!!.hour = hour
        alarmEdit!!.minute = minute
        alarmEdit!!.days = weeksBool
        alarmEdit!!.name = ""
        alarmEdit!!.url = ALARM_URL_PREF ?: ""
        alarmEdit!!.soundName = ALARM_NAME_PREF ?: ""
        alarmEdit!!.interval = interval
        alarmEdit!!.repeat = repeatAmount
        alarmEdit!!.soundness = soundness
        alarmEdit!!.vibration = ALARM_VIBRATE
        alarmEdit!!.pause = isPaused

        if (selectedDay != -1) {
            calendar[Calendar.YEAR] = selectedYear
            calendar[Calendar.MONTH] = selectedMonth
            calendar[Calendar.DAY_OF_MONTH] = selectedDay
            alarmEdit!!.date = calendar.timeInMillis
            alarmEdit!!.days.clear()
        } else if (weeksBool.filter { it }.isEmpty()) {
            if (calendar.timeInMillis > now) {
                alarmEdit!!.date = calendar.timeInMillis
            } else {
                alarmEdit!!.date = calendar.timeInMillis + 86_400_000
            }
        } else {
            alarmEdit!!.date = 0
        }



        updateAlarm(alarmEdit!!)
        setNextAlarm(requireContext())
        findNavController().popBackStack()
    }

    private fun setWeekListeners() {
        b.tvMonday.setOnClickListener { weeksBool[0] = !weeksBool[0]; initViews() }
        b.tvTuesday.setOnClickListener { weeksBool[1] = !weeksBool[1]; initViews() }
        b.tvThurthday.setOnClickListener { weeksBool[2] = !weeksBool[2]; initViews() }
        b.tvWensday.setOnClickListener { weeksBool[3] = !weeksBool[3]; initViews() }
        b.tvFriday.setOnClickListener { weeksBool[4] = !weeksBool[4]; initViews() }
        b.tvSaturday.setOnClickListener { weeksBool[5] = !weeksBool[5]; initViews() }
        b.tvSunday.setOnClickListener { weeksBool[6] = !weeksBool[6]; initViews() }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        var text = ""
        for (it in weeksBool.indices) {

            if (weeksBool[it]) {
                weekViews[it].setBackgroundResource(R.drawable.back_week_days)
                weekViews[it].setTextColor(resources.getColor(R.color.blue, null))
                if (it == 0) {
                    text = weeks[it]
                } else {
                    text = "$text, ${weeks[it]}"
                }

            } else {
                weekViews[it].setBackgroundResource(R.drawable.back_week_days_off)
                if (it == 6) {
                    weekViews[it].setTextColor(resources.getColor(R.color.red, null))
                } else {
                    weekViews[it].setTextColor(resources.getColor(R.color.black, null))
                }
            }
        }
        val weekTrues = weeksBool.filter { it }
        if (weekTrues.size == 7) {
            b.tvAlarmDaysTitle.text = "Har kuni"
        } else if (weekTrues.isEmpty()) {
            b.tvAlarmDaysTitle.text = ""
        } else {
            b.tvAlarmDaysTitle.text = "Har: $text"
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val calendar = DatePickerDialog(
            APP, { view, year1, monthOfYear, dayOfMonth ->
            },
            year,
            month,
            day
        )
        calendar.setOnDateSetListener { view, year2, month2, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month2 + 1}/$year2"
            val date = c.time
            val parseDate = dateFormat.parse(selectedDate)
            if (date.after(parseDate)) {
                "Bu sanaga budilnik o'rnata olmaysiz".toToast()
            } else {
                selectedYear = year2
                selectedMonth = month2 + 1
                selectedDay = dayOfMonth
                b.tvAlarmDaysTitle.text = selectedDate
            }
        }
        calendar.show()

    }

    companion object {
        var interval = 5
        var repeatAmount = 3
        var soundness = true
    }
}