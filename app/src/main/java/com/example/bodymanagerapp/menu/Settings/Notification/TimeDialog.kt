package com.example.bodymanagerapp.menu.Settings.Notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.R
import java.util.*

class TimeDialog(context : Context) {
    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb : SQLiteDatabase

    private val  dialog = Dialog(context)
    /*private lateinit var timeTV : TextView*/
    private lateinit var timePicker : TimePicker
    private lateinit var memoET : EditText
    private lateinit var okButton : Button
    private lateinit var cancelButton: Button
    private lateinit var sunCB : CheckBox
    private lateinit var monCB : CheckBox
    private lateinit var tueCB : CheckBox
    private lateinit var wedCB : CheckBox
    private lateinit var thuCB : CheckBox
    private lateinit var friCB : CheckBox
    private lateinit var satCB : CheckBox

    var time = -1
    private var memo = ""
    private var days = ""

    private lateinit var alarmManager : AlarmManager
    private var hour = -1
    private var minute = -1

    @RequiresApi(Build.VERSION_CODES.M)
    fun start(context: Context, id : Int) {
        MyDBHelper = MyDBHelper(context)
        sqldb = MyDBHelper.writableDatabase

        dialog.setContentView(R.layout.time_dialog) // Dialog에 사용할 xml 파일 불러오기
        dialog.setCancelable(false) // Dialog 밖 클릭 시 화면 사라지지 않음
        var params : WindowManager.LayoutParams = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT

        timePicker = dialog.findViewById(R.id.tp_time)
        memoET = dialog.findViewById(R.id.et_time_memo)
        okButton = dialog.findViewById(R.id.btn_time_ok)
        cancelButton = dialog.findViewById(R.id.btn_time_cancel)
        sunCB = dialog.findViewById(R.id.cb_sun)
        monCB = dialog.findViewById(R.id.cb_mon)
        tueCB = dialog.findViewById(R.id.cb_tue)
        wedCB = dialog.findViewById(R.id.cb_wed)
        thuCB = dialog.findViewById(R.id.cb_thu)
        friCB = dialog.findViewById(R.id.cb_fri)
        satCB = dialog.findViewById(R.id.cb_sat)

        alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

       if(id > -1) loadTime(id)

        /*timeTV.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { timePicker, h, m ->
                time = h * 60 + m
                timeTV.text = "${h}시 ${m}분"
            }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
        }*/

        okButton.setOnClickListener {
            memo = memoET.text.toString()

            if(id != -1) updateTime(context, id, time, memo)
            else {
                if (getDays() != "") {
                    hour = timePicker.hour
                    minute = timePicker.minute
                    time = hour * 60 + minute
                    saveTime(time, memo)
                    setAlarm(context)
                } else {
                    Toast.makeText(context, "요일을 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()

            val intent = Intent(context, NotificationActivity::class.java)
            context.startActivity(intent)
            Activity().finish()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveTime(time : Int, memo : String) {
        sqldb = MyDBHelper.writableDatabase
        days = getDays()
        sqldb.execSQL("INSERT INTO time_table(time, memo, days, isOn) VALUES ($time, '$memo', '$days', 1);")
        sqldb.close()
    }

    private fun loadTime(id : Int) {
        sqldb = MyDBHelper.readableDatabase

        var cursor = sqldb.rawQuery("SELECT * FROM time_table WHERE tId = $id;", null)

        if (cursor.moveToFirst()) {
            do {
                time = cursor.getInt(cursor.getColumnIndex("time"))
                memo = cursor.getString(cursor.getColumnIndex("memo"))
                days = cursor.getString(cursor.getColumnIndex("days"))
                val hour = time / 60
                val min = time % 60
                /*timeTV.text = "${hour}시 ${min}분"*/
                memoET.setText(memo)
                setDays(days)
            } while (cursor.moveToNext())
        }
        sqldb.close()
    }

    private fun updateTime(context: Context, id : Int, time : Int, memo : String) {
        cancelAlarm(context)
        setAlarm(context)
        sqldb = MyDBHelper.writableDatabase
        days = getDays()
        sqldb.execSQL("UPDATE time_table SET time = $time, memo = '$memo', days = '$days' WHERE tID = $id;")
        sqldb.close()
    }

    private fun getDays() : String {
        var days = ""
        if (sunCB.isChecked) days += "일 "
        if (monCB.isChecked) days += "월 "
        if (tueCB.isChecked) days += "화 "
        if (wedCB.isChecked) days += "수 "
        if (thuCB.isChecked) days += "목 "
        if (friCB.isChecked) days += "금 "
        if (satCB.isChecked) days += "토 "

        return days
    }

    @JvmName("setDays1")
    private fun setDays(days : String) {
        sunCB.isChecked = false
        monCB.isChecked = false
        tueCB.isChecked = false
        wedCB.isChecked = false
        thuCB.isChecked = false
        friCB.isChecked = false
        satCB.isChecked = false

        for(i in days.indices) {
            if(days[i] != ' ') {
                when (days[i].toString()) {
                    "${sunCB.text}" -> {
                        sunCB.isChecked = true
                    }
                    "${monCB.text}" -> {
                        monCB.isChecked = true
                    }
                    "${tueCB.text}" -> {
                        tueCB.isChecked = true
                    }
                    "${wedCB.text}" -> {
                        wedCB.isChecked = true
                    }
                    "${thuCB.text}" -> {
                        thuCB.isChecked = true
                    }
                    "${friCB.text}" -> {
                        friCB.isChecked = true
                    }
                    "${satCB.text}" -> {
                        satCB.isChecked = true
                    }
                }
            }
        }
    }

    fun setAlarm(context: Context) {
        var week = booleanArrayOf(false, sunCB.isChecked, monCB.isChecked, tueCB.isChecked,
                wedCB.isChecked, thuCB.isChecked, friCB.isChecked, satCB.isChecked)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("weekday", week)
            intent.putExtra("memo", memo)
            var pIntent : PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            var calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            //var today = Date()
            var intervalDay : Long = 24 * 60 * 60 * 1000 // 24시간
            var selectTime = calendar.timeInMillis
            var currentTime = System.currentTimeMillis()

            // 설정한 시간이 현재시간 보다 작으면 다음날 울리게 하기
            if (currentTime > selectTime) {
                selectTime += intervalDay
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectTime, intervalDay, pIntent)
        } else {
            Toast.makeText(context, "버전을 확인해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelAlarm(context: Context) {
        var intent = Intent(context, AlarmReceiver::class.java)
        var pIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.cancel(pIntent)
    }
}