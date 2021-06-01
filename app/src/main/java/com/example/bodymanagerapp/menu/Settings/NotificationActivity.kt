package com.example.bodymanagerapp.menu.Settings

import android.app.DatePickerDialog
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.MyDBHelper
import java.util.*
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

        btn_goal.setOnClickListener {
            val dialog = GoalDialog(this)
            dialog.start(this, -1)
        }

    }

   /* private fun saveGoal(goal: String, date : Int) {
        sqldb = MyDBHelper.writableDatabase

        sqldb.execSQL("INSERT INTO goal_table(goal, date, achievement) VALUES ('$goal', $date, 0);")
        sqldb.close()
    }*/

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

        return data
    }
}