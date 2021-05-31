package com.example.bodymanagerapp.menu.Settings

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.databinding.ActivityNotificationBinding
import com.example.bodymanagerapp.MyDBHelper

class NotificationActivity : AppCompatActivity() {
    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb : SQLiteDatabase

    lateinit var btn_goal : Button
    lateinit var btn_time : Button
    lateinit var rv_goal : RecyclerView
    lateinit var rv_time : RecyclerView
    lateinit var goalBinding : ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        MyDBHelper = MyDBHelper(this)
        sqldb = MyDBHelper.writableDatabase

        btn_goal = findViewById(R.id.btn_nf_goal)
        btn_time = findViewById(R.id.btn_nf_time)
        rv_goal = findViewById(R.id.rv_nf_goal)
        rv_time = findViewById(R.id.rv_nf_time)

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
            val dateET = dialogView.findViewById<TextView>(R.id.tv_goal_date)
            val okButton = dialogView.findViewById<Button>(R.id.btn_goal_ok)
            val cancelButton = dialogView.findViewById<Button>(R.id.btn_goal_cancel)

            okButton.setOnClickListener {
                val goal = goalET.text.toString()
                val date = dateET.text.toString()

                if (goal != null && date != "목표 날짜를 선택해주세요.")
                    saveGoal(goal, date.toInt())
                else Toast.makeText(this, "목표와 날짜를 설정해주세요.", Toast.LENGTH_SHORT).show()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }

    }

    private fun saveGoal(goal: String, date : Int) {
        sqldb = MyDBHelper.writableDatabase

        sqldb.execSQL("INSERT INTO goal_table(gaol, date) VALUES ('$goal', $date);")
        sqldb.close()
    }
}