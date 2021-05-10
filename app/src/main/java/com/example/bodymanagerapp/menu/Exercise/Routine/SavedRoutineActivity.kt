package com.example.bodymanagerapp.menu.Exercise.Routine

import android.app.AlertDialog
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.MainActivity
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.SettingsFragment
import com.example.bodymanagerapp.myDBHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SavedRoutineActivity : AppCompatActivity() {
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    lateinit var toolbar: Toolbar

    lateinit var spinner : Spinner
    lateinit var et_routine : EditText
    lateinit var rv : RecyclerView
    lateinit var rvAdapter : RoutineRVAdapter
    lateinit var btn_save : Button

    var nameList = ArrayList<String>()
    var routineData = ArrayList<RoutineData>()
    var date_format : String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_routine)

        var now = LocalDate.now()
        date_format = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        myDBHelper = myDBHelper(this)

        spinner = findViewById(R.id.spinner_routine_save)
        et_routine = findViewById(R.id.et_routine_name)
        rv = findViewById(R.id.recycler_routine_save)
        btn_save = findViewById(R.id.btn_routine_save)

        loadRoutineName() // 루틴 이름 불러오기
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nameList)
        spinner.adapter = spinnerAdapter

        routineData.clear()
        routineData.addAll(loadExercise()) // 오늘 운동 불러오기
        if(routineData.size > 0) { // 데이터가 있으면
            rvAdapter = RoutineRVAdapter(routineData, this, rv)
            rv.adapter = rvAdapter
            rv.layoutManager = LinearLayoutManager(this)
            rv.visibility = View.VISIBLE
        }

        spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(nameList[spinner.selectedItemPosition] == "직접 입력") {
                    et_routine.visibility = View.VISIBLE
                } else {
                    et_routine.visibility = View.GONE
                }
            }
        }

        btn_save.setOnClickListener {
            var str = ""
            if(nameList[spinner.selectedItemPosition] == "직접 입력") str = et_routine.text.toString()
            else str = nameList[spinner.selectedItemPosition]

            var dig = AlertDialog.Builder(this) // 대화상자
            dig.setTitle("저장하기") // 제목
            dig.setMessage("'$str'에 저장하시겠습니까?")
            dig.setPositiveButton("확인") { dialog, which ->
                saveRoutine()
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            dig.setNegativeButton("취소", null)
            dig.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.menu_settings -> {
                MainActivity().replaceFragment(SettingsFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadRoutineName() {
        nameList.clear()
        nameList.add("직접 입력")
        sqldb = myDBHelper.readableDatabase
        var cursor = sqldb.rawQuery("SELECT DISTINCT routine_name FROM routine_info ORDER BY routine_name ASC;", null)

        if(cursor.moveToFirst()) {
            do{
                nameList.add(cursor.getString(cursor.getColumnIndex("routine_name")))
            } while(cursor.moveToNext())
        }

    }

    private fun loadExercise() : ArrayList<RoutineData> {
        var data = ArrayList<RoutineData>()
        sqldb = myDBHelper.readableDatabase

        // 중복 없이 운동명 가져오기
        var nameCursor = sqldb.rawQuery("SELECT DISTINCT exercise_name FROM exercise_counter WHERE date = ${date_format.toInt()};", null)

        if(nameCursor.moveToFirst()) { // 저장된 운동이 있으면
            var name : String = ""

            do{
                name = nameCursor.getString(nameCursor.getColumnIndex("exercise_name")) // 이름 값 받기

                // 이름에 해당하는 데이터 검색해서 추가하기
                var cursor = sqldb.rawQuery("SELECT * FROM exercise_counter WHERE date = ${date_format.toInt()} AND exercise_name = '$name';", null)
                if (cursor.moveToFirst()) {
                    var set = ArrayList<Int>()
                    var weight = ArrayList<Float>()
                    var num = ArrayList<Int>()
                    var time = ArrayList<Int>()

                    do {
                        set.add(cursor.getInt(cursor.getColumnIndex("set_num")))
                        weight.add(cursor.getFloat(cursor.getColumnIndex("weight")))
                        num.add(cursor.getInt(cursor.getColumnIndex("exercise_count")))
                        time.add(cursor.getInt(cursor.getColumnIndex("time")))
                    } while (cursor.moveToNext())

                    data.add(RoutineData(name, set, weight, num, time))
                }
            } while (nameCursor.moveToNext())
        }
        sqldb.close()
        return data
    }

    private fun saveRoutine() {
        sqldb = myDBHelper.writableDatabase

        if(nameList[spinner.selectedItemPosition] != "직접 입력") {
            sqldb.execSQL("DELETE FROM routine_info WHERE routine_name = '${nameList[spinner.selectedItemPosition]}'")
            for(i in 0 until routineData.size) {
                for(j in 0 until routineData[i].set.size) {
                    sqldb.execSQL("INSERT INTO routine_info VALUES ('${nameList[spinner.selectedItemPosition]}', '${routineData[i].exercise_name}', " +
                            "${routineData[i].set[j]}, ${routineData[i].weightList[j]}, ${routineData[i].exercise_count[j]}, ${routineData[i].time[j]})")
                }
            }
        } else {
            for(i in 0 until routineData.size) {
                for(j in 0 until routineData[i].set.size) {
                    sqldb.execSQL("INSERT INTO routine_info VALUES ('${et_routine.text}', '${routineData[i].exercise_name}', " +
                            "${routineData[i].set[j]}, ${routineData[i].weightList[j]}, ${routineData[i].exercise_count[j]}, ${routineData[i].time[j]})")
                }
            }
        }
        sqldb.close()
    }
}