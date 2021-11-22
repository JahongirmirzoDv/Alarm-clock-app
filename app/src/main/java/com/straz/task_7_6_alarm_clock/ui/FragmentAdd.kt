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
import java.util.*
import kotlin.random.Random

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FragmentAdd : Fragment() {
    private var _binding: FragmentAddAlarmBinding? = null
    private val b get() = _binding!!

    private lateinit var weekViews: List<TextView>

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    var weeksBool = arrayListOf(false, false, false, false, false, false, false)
    private var selectedYear = -1
    private var selectedMonth = -1
    private var selectedDay = -1
    private var hour = 6
    private var minute = 0
    private var selectedDate = ""

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAlarmBinding.inflate(layoutInflater)
        weekViews = listOf(
            b.tvMonday,
            b.tvTuesday,
            b.tvThurthday,
            b.tvWensday,
            b.tvFriday,
            b.tvSaturday,
            b.tvSunday
        )

        b.timePicker.setIs24HourView(true)
        b.timePicker.hour = 6
        b.timePicker.minute = 0

        b.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            b.hour1.text = "$hourOfDay hour $minute minute"
        }
        b.tvSave.setOnClickListener {
            saveAlarm()
        }
        b.tvExit.setOnClickListener {
            findNavController().popBackStack()
        }

        b.lAlarmSound.setOnClickListener {
            findNavController().navigate(R.id.fragmentListAlarms)
        }

        b.switchVibrate.setOnCheckedChangeListener {
            ALARM_VIBRATE = it
        }

//        b.switchSound.setOnCheckedChangeListener { btn, isChecked ->
//            soundness = isChecked
//            ALARM_SOUNDNESS = isChecked
//            setAlarmName(isChecked)
//        }
//        b.lPause.setOnClickListener {
//            findNavController().navigate(R.id.action_fragmentAdd_to_fragmentPause)
//        }
//        b.switchPause.setOnCheckedChangeListener { btn, isChecked ->
//            isPaused = isChecked
//            setInterval(isChecked)
//
//        }
        b.timePicker.setOnTimeChangedListener { view, hourOfDay, minute1 ->
            hour = hourOfDay
            minute = minute1
        }
        setWeekListeners()
        return b.root
    }

    private fun setAlarmName(isChecked: Boolean) {
        if (!isChecked)
            b.tvSoundAlarm.text = "ovozsiz"
        else {
            if (ALARM_URL_PREF == "" || ALARM_URL_PREF == null) {
                val t = resources.getText(alarmSounds[0])
                b.tvSoundAlarm.text = t.substring(t.lastIndexOf("/") + 1, t.length)
            } else {
                b.tvSoundAlarm.text = ALARM_NAME_PREF
            }

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
//        b.switchSound.isChecked = true
//        b.switchPause.isChecked = false
//        setInterval(isPaused)
//        setAlarmName(ALARM_SOUNDNESS)
//    }

    fun saveAlarm() {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        Log.d("main", "saveAlarm : hour=$hour, minute=$minute")
        val alarmTime = AlarmTime(
            id = Random.nextInt(1, 200_000_000),
            hour = hour,
            minute = minute,
            days = weeksBool,
            name = "",
            url = ALARM_URL_PREF ?: "",
            soundName = ALARM_NAME_PREF ?: "",
            interval = interval,
            repeat = repeatAmount,
            soundness = soundness,
            vibration = ALARM_VIBRATE,
            pause = isPaused
        )

        if (selectedDay != -1) {
            calendar[Calendar.YEAR] = selectedYear
            calendar[Calendar.MONTH] = selectedMonth
            calendar[Calendar.DAY_OF_MONTH] = selectedDay
            alarmTime.date = calendar.timeInMillis
            alarmTime.days.clear()
        } else if (weeksBool.filter { it }.isEmpty() || isPaused == false) {
            if (calendar.timeInMillis > now) {
                alarmTime.date = calendar.timeInMillis
            } else {
                alarmTime.date = calendar.timeInMillis + 86_400_000
            }
        } else {
            alarmTime.date = 0
        }
        Log.d("alarm", "alarmTime: $alarmTime")

        val filter = listAlarms.filter {
            it.date.getDate() == alarmTime.date.getDate() &&
                    checkList(it) &&
                    it.hour == alarmTime.hour &&
                    it.minute == alarmTime.minute
        }
        Log.d("main", "add weeksBool : $weeksBool")
        Log.d("main", "add alarmEdit.days : ${alarmTime.days}")
        if (filter.isEmpty()) {
            addAlarm(alarmTime)
        }
        setNextAlarm(requireContext())
        findNavController().popBackStack()
    }

    private fun checkList(it: AlarmTime): Boolean {
        var b = true
        for (i in weeksBool.indices) {
            if (it.days[i] != weeksBool[i]) {
                b = false
            }
        }
        return b
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
            b.days.text = "Evry day"
        } else if (weekTrues.isEmpty()) {
            b.days.text = ""
        } else {
            b.days.text = text
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
            selectedDate = "${dayOfMonth.zero()}/${(month2 + 1).zero()}/$year2"
            val date = c.time
            val parseDate = dateFormat.parse(selectedDate)
            if (date.after(parseDate)) {
                "Bu sanaga budilnik o'rnata olmaysiz".toToast()
            } else {
                selectedYear = year2
                selectedMonth = month2
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