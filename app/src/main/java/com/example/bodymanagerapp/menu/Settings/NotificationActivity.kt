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
import com.example.bodymanagerapp.databinding.ActivityNotificationBinding
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
    lateinit var goalBinding : ActivityNotificationBinding

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
            goalAdapter = GoalRecyclerViewAdapter(goalData, this, rv_goal) /*{
                data, num ->
                val dialogView = LayoutInflater.from(this).inflate(R.layout.goal_dialog, null)
                val builder = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setTitle("목표 추가")
                val dialog = builder.show()
            }*/
            Log.d("어댑터", "$goalAdapter")
            rv_goal.adapter = goalAdapter
            rv_goal.layoutManager = LinearLayoutManager(this)
            rv_goal.visibility = View.VISIBLE
        }

        // 뷰 바인딩 -> 알림 설정을 위해
        goalBinding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(goalBinding.root)

        goalBinding.btnNfGoal.setOnClickListener {
            // Dialog 만들기
            val dialogView = LayoutInflater.from(this).inflate(R.layout.goal_dialog, null)
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("목표 추가")
            val dialog = builder.show()

            val goalET = dialogView.findViewById<EditText>(R.id.et_goal)
            val dateTV = dialogView.findViewById<TextView>(R.id.tv_goal_date)
            val okButton = dialogView.findViewById<Button>(R.id.btn_goal_ok)
            val cancelButton = dialogView.findViewById<Button>(R.id.btn_goal_cancel)
            var date = 0

            dateTV.setOnClickListener {
                val cal = Calendar.getInstance()
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    var ymd = "$y"
                    ymd += if(m + 1 < 10)
                        "0${m+1}"
                    else "${m+1}"
                    ymd += if(d < 10)
                        "0$d"
                    else "$d"
                    date = ymd.toInt()
                    dateTV.text = "${y}년 ${m+1}월 ${d}일까지"
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
            }

            okButton.setOnClickListener {
                val goal = goalET.text.toString()

                if (goal != null && date != 0) {
                    saveGoal(goal, date.toInt())
                    dialog.dismiss()
                }
                else Toast.makeText(this, "목표와 날짜를 설정해주세요.", Toast.LENGTH_SHORT).show()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        goalBinding.btnNfTime.setOnClickListener {
            // Dialog 만들기
            val dialogView = LayoutInflater.from(this).inflate(R.layout.goal_dialog, null)
            val builder = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setTitle("목표 추가")
            val dialog = builder.show()

            val goalET = dialogView.findViewById<EditText>(R.id.et_goal)
            val dateTV = dialogView.findViewById<TextView>(R.id.tv_goal_date)
            val okButton = dialogView.findViewById<Button>(R.id.btn_goal_ok)
            val cancelButton = dialogView.findViewById<Button>(R.id.btn_goal_cancel)
            var date = 0

            dateTV.setOnClickListener {
                val cal = Calendar.getInstance()
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    var ymd = "$y"
                    ymd += if(m + 1 < 10)
                        "0${m+1}"
                    else "${m+1}"
                    ymd += if(d < 10)
                        "0$d"
                    else "$d"
                    date = ymd.toInt()
                    dateTV.text = "${y}년 ${m+1}월 ${d}일까지"
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
            }

            okButton.setOnClickListener {
                val goal = goalET.text.toString()

                if (goal != null && date != 0) {
                    saveGoal(goal, date.toInt())
                    dialog.dismiss()
                }
                else Toast.makeText(this, "목표와 날짜를 설정해주세요.", Toast.LENGTH_SHORT).show()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun saveGoal(goal: String, date : Int) {
        sqldb = MyDBHelper.writableDatabase

        sqldb.execSQL("INSERT INTO goal_table(goal, date, achievement) VALUES ('$goal', $date, 0);")
        sqldb.close()
    }

    private fun loadGoals() : ArrayList<NotificationData> {
        var data = ArrayList<NotificationData>()

        sqldb = MyDBHelper.readableDatabase

        var cursor = sqldb.rawQuery("SELECT * FROM goal_table;", null)

        if(cursor.moveToFirst()) {
            do {
                val goal = cursor.getString(cursor.getColumnIndex("goal"))
                val date = cursor.getInt(cursor.getColumnIndex("date"))
                val isChecked = cursor.getInt(cursor.getColumnIndex("achievement"))
                var check = false
                if (isChecked != 0) check = true
                data.add(NotificationData(date, goal, check))
            } while (cursor.moveToNext())
        }

        return data
    }
}