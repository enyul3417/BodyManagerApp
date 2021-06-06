package com.example.bodymanagerapp.menu.Settings.Notification

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.MyDBHelper
import java.sql.Time
import kotlin.collections.ArrayList

class NotificationActivity : AppCompatActivity() {
    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb : SQLiteDatabase

    // 레이아웃
    lateinit var btn_goal : Button
    lateinit var btn_time : Button
    lateinit var rv_goal : RecyclerView
    lateinit var rv_time : RecyclerView

    // 목표와 시간 데이터를 받아올 ArrayList
    private var goalData = ArrayList<NotificationData>()
    private var timeData = ArrayList<NotificationData>()

    // View
    lateinit var goalAdapter : GoalRecyclerViewAdapter
    lateinit var timeAdapter: TimeRecyclerViewAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        MyDBHelper = MyDBHelper(this)
        sqldb = MyDBHelper.writableDatabase

        btn_goal = findViewById(R.id.btn_nf_goal)
        btn_time = findViewById(R.id.btn_nf_time)
        rv_goal = findViewById(R.id.rv_nf_goal)
        rv_time = findViewById(R.id.rv_nf_time)

        goalData.clear()
        goalData.addAll(loadGoals())
        if (goalData.size > 0) {
            goalAdapter = GoalRecyclerViewAdapter(goalData, this, rv_goal) {
                data ->
                val dialog = GoalDialog(this)
                dialog.start(this, data.id)
            }
            Log.d("어댑터", "$goalAdapter")
            rv_goal.adapter = goalAdapter
            rv_goal.layoutManager = LinearLayoutManager(this)
            rv_goal.visibility = View.VISIBLE
        }

        timeData.clear()
        timeData.addAll(loadTime())
        if(timeData.size > 0) {
            timeAdapter = TimeRecyclerViewAdapter(timeData, this, rv_time) {
                data ->
                val dialog = TimeDialog(this)
                dialog.start(this, data.id)
            }
            rv_time.adapter = timeAdapter
            rv_time.layoutManager = LinearLayoutManager(this)
            rv_time.visibility = View.VISIBLE

        }

        btn_goal.setOnClickListener {
            val dialog = GoalDialog(this)
            dialog.start(this, -1)
        }

        btn_time.setOnClickListener {
            val dialog = TimeDialog(this)
            dialog.start(this, -1)
        }
    }

    private fun loadGoals() : ArrayList<NotificationData> {
        var data = ArrayList<NotificationData>()

        sqldb = MyDBHelper.readableDatabase

        var cursor = sqldb.rawQuery("SELECT * FROM goal_table;", null)

        if(cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("gId"))
                val goal = cursor.getString(cursor.getColumnIndex("goal"))
                val date = cursor.getInt(cursor.getColumnIndex("date"))
                val isChecked = cursor.getInt(cursor.getColumnIndex("achievement"))
                var check = false
                if (isChecked != 0) check = true
                data.add(NotificationData(id, date, goal, check))
            } while (cursor.moveToNext())
        }
        sqldb.close()
        return data
    }

    private fun loadTime() : ArrayList<NotificationData> {
        var data = ArrayList<NotificationData>()

        sqldb = MyDBHelper.readableDatabase

        var cursor = sqldb.rawQuery("SELECT * FROM time_table;", null)

        if(cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("tId"))
                val memo = cursor.getString(cursor.getColumnIndex("memo"))
                val time = cursor.getInt(cursor.getColumnIndex("time"))
                val days = cursor.getString(cursor.getColumnIndex("days"))
                val isOn = cursor.getInt(cursor.getColumnIndex("isOn"))
                var on = true
                if(isOn != 1) on = false
                data.add(NotificationData(id, time, memo, on, days))
            } while (cursor.moveToNext())
        }
        sqldb.close()
        return data
    }
}