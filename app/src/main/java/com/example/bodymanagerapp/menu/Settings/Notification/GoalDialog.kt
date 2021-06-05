package com.example.bodymanagerapp.menu.Settings.Notification


import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.R
import java.util.*

class GoalDialog(context: Context) {
    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb : SQLiteDatabase

    private val dialog = Dialog(context) // 부모 액티비티의 context가 들어감
    private lateinit var goalET : EditText
    private lateinit var dateTV : TextView
    private lateinit var okButton : Button
    private lateinit var cancelButton: Button

    var goal = ""
    var date = 0

    //private lateinit var listener : GoalDialogOKClickedListener

    fun start(context: Context, id: Int) {
        MyDBHelper = MyDBHelper(context)
        sqldb = MyDBHelper.writableDatabase

        dialog.setContentView(R.layout.goal_dialog) // Dialog에 사용할 xml 파일 불러오기
        dialog.setCancelable(false) // Dialog 밖 클릭 시 화면 사라지지 않음

        goalET = dialog.findViewById(R.id.et_goal)
        dateTV = dialog.findViewById(R.id.tv_goal_date)
        okButton = dialog.findViewById(R.id.btn_goal_ok)
        cancelButton = dialog.findViewById(R.id.btn_goal_cancel)


        if (id > -1) loadGoal(id)

        dateTV.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
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
            goal = goalET.text.toString()
            if (goal != "" && date != 0) {
                if(id != -1) updateGoal(id, goal, date)
                else {
                    saveGoal(goal, date)
                    //setNotification(context, goal, date)
                }
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

    private fun saveGoal(goal: String, date : Int) {
        sqldb = MyDBHelper.writableDatabase

        sqldb.execSQL("INSERT INTO goal_table(goal, date, achievement) VALUES ('$goal', $date, 0);")
        sqldb.close()
    }

    private fun loadGoal(id : Int) {
        sqldb = MyDBHelper.readableDatabase

        var cursor = sqldb.rawQuery("SELECT * FROM goal_table WHERE gId = $id;", null)

        if (cursor.moveToFirst()) {
            do {
                val goal = cursor.getString(cursor.getColumnIndex("goal"))
                date = cursor.getInt(cursor.getColumnIndex("date"))
                val year = date / 10000
                val month = date % 10000 / 100
                val day = date % 10000 % 100
                goalET.setText(goal)
                dateTV.text = "${year}년 ${month}월 ${day}일"
            } while (cursor.moveToNext())
        }
        sqldb.close()
    }

    private fun updateGoal(id : Int, goal: String, date: Int) {
        sqldb = MyDBHelper.writableDatabase
        sqldb.execSQL("UPDATE goal_table SET goal = '$goal', date = $date WHERE gID = $id;")
        sqldb.close()
    }

    /*private fun setNotification(context: Context, goal: String, date: Int) {
        var builder : NotificationCompat.Builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.cat)
                .setContentTitle(goal)
                .setContentText("${date}까지")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)

    }*/

}