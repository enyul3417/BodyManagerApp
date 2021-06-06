package com.example.bodymanagerapp.menu.Settings.Notification

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.view.WindowManager
import android.widget.*
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.R
import java.util.*

class TimeDialog(context : Context) {
    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb : SQLiteDatabase

    private val  dialog = Dialog(context)
    private lateinit var timeTV : TextView
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

    fun start(context: Context, id : Int) {
        MyDBHelper = MyDBHelper(context)
        sqldb = MyDBHelper.writableDatabase

        dialog.setContentView(R.layout.time_dialog) // Dialog에 사용할 xml 파일 불러오기
        dialog.setCancelable(false) // Dialog 밖 클릭 시 화면 사라지지 않음
        var params : WindowManager.LayoutParams = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT

        timeTV = dialog.findViewById(R.id.tv_time)
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


        if(id > -1) loadTime(id)

        timeTV.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { timePicker, h, m ->
                time = h * 60 + m
                timeTV.text = "${h}시 ${m}분"
            }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
        }

        okButton.setOnClickListener {
            memo = memoET.text.toString()
            if (time != -1) {
                if(id != -1) updateTime(id, time, memo)
                else saveTime(time, memo)
                dialog.dismiss()

                val intent = Intent(context, NotificationActivity::class.java)
                context.startActivity(intent)
                Activity().finish()
            }
            else Toast.makeText(context, "목표와 날짜를 설정해주세요.", Toast.LENGTH_SHORT).show()
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
                timeTV.text = "${hour}시 ${min}분"
                memoET.setText(memo)
                setDays(days)
            } while (cursor.moveToNext())
        }
        sqldb.close()
    }

    private fun updateTime(id : Int, time : Int, memo : String) {
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
        var intent : Intent = Intent(context, AlarmReceiver::class.java)
        //intent.putExtra("index", alarmIdx)
    }
}