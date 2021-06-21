package com.example.bodymanagerapp.menu.Exercise.Routine

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
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
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LoadRoutineActivity : AppCompatActivity() {
    private val NUM_ID: Int = 200 // 횟수
    private val WEIGHT_ID: Int = 300 // 무게
    private val HOUR_ID : Int = 500
    private val MIN_ID : Int = 600
    private val SEC_ID : Int = 700

    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase

    lateinit var toolbar: Toolbar

    lateinit var spinner : Spinner
    lateinit var rv : RecyclerView
    lateinit var rvAdapter : RoutineRVAdapter
    lateinit var btn_delete : Button
    lateinit var btn_load : Button

    var nameList = ArrayList<String>()
    var routineData = ArrayList<RoutineData>()

    var date = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_routine)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        MyDBHelper = MyDBHelper(this)

        spinner = findViewById(R.id.spinner_routine_load)
        rv = findViewById(R.id.recycler_routine_load)
        btn_delete = findViewById(R.id.btn_routine_delete)
        btn_load = findViewById(R.id.btn_routine_load)

        var now = LocalDate.now()
        date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()

        loadRoutineName() // 루틴 이름 불러오기
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nameList)
        spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                routineData.clear()
                routineData.addAll(loadRoutine()) // 루틴에 해당하는 운동 불러오기
                if(routineData.size > 0) { // 데이터가 있으면
                    rvAdapter = RoutineRVAdapter(routineData, this@LoadRoutineActivity, rv)
                    rv.adapter = rvAdapter
                    rv.layoutManager = LinearLayoutManager(this@LoadRoutineActivity)
                    rv.visibility = View.VISIBLE
                }
            }
        }

        btn_delete.setOnClickListener {
            var dig = AlertDialog.Builder(this) // 대화상자
            dig.setTitle("삭제 확인") // 제목
            dig.setMessage("삭제하시겠습니까?")
            dig.setPositiveButton("확인") { dialog, which ->
                deleteRoutine()
                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                var intent = Intent(this, LoadRoutineActivity::class.java)
                startActivity(intent)
                finish()
            }
            dig.setNegativeButton("취소", null)
            dig.show()
        }

        btn_load.setOnClickListener { // 불러오기 버튼
            deleteAllExercise()
            addExercise()
            val intent = Intent(this, ExerciseActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            /*val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)*/
            //setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.menu_settings -> {
                //MainActivity().replaceFragment(SettingsFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadRoutineName() {
        nameList.clear()
        sqldb = MyDBHelper.readableDatabase
        var cursor = sqldb.rawQuery("SELECT DISTINCT routine_name FROM routine_info ORDER BY routine_name ASC;", null)

        if(cursor.moveToFirst()) {
            do{
                nameList.add(cursor.getString(cursor.getColumnIndex("routine_name")))
            } while(cursor.moveToNext())
        } else {
            nameList.add("저장된 루틴이 없습니다.")
        }
    }

    private fun loadRoutine() : ArrayList<RoutineData> {
        var data = ArrayList<RoutineData>()
        sqldb = MyDBHelper.readableDatabase

        // 중복 없이 운동명 가져오기
        var nameCursor = sqldb.rawQuery("SELECT DISTINCT exercise_name, tag FROM routine_info WHERE routine_name = '${nameList[spinner.selectedItemPosition]}';", null)

        if(nameCursor.moveToFirst()) { // 저장된 운동이 있으면
            var name : String = ""
            var tag : String = ""

            do{
                name = nameCursor.getString(nameCursor.getColumnIndex("exercise_name")) // 이름 값 받기
                tag = nameCursor.getString(nameCursor.getColumnIndex("tag"))

                // 이름에 해당하는 데이터 검색해서 추가하기
                var cursor = sqldb.rawQuery("SELECT * FROM routine_info WHERE routine_name = '${nameList[spinner.selectedItemPosition]}' AND exercise_name = '$name';", null)
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

                    data.add(RoutineData(name, tag, set, weight, num, time))
                }
            } while (nameCursor.moveToNext())
        }
        sqldb.close()
        return data
    }

    private fun deleteRoutine() {
        sqldb = MyDBHelper.writableDatabase
        sqldb.execSQL("DELETE FROM routine_info WHERE routine_name = '${nameList[spinner.selectedItemPosition]}'")
        sqldb.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addExercise() {
        sqldb = MyDBHelper.writableDatabase

        for(i in 0 until routineData.size) {
            for(j in 0 until routineData[i].set.size) {
                sqldb.execSQL("INSERT INTO exercise_counter VALUES ($date,'${routineData[i].exercise_name}', '${routineData[i].tag}', ${j+1}, " +
                        "${routineData[i].weightList[j]}, ${routineData[i].exercise_count[j]}, ${routineData[i].time[j]}, 0);")
            }
        }
        sqldb.close()
    }

    private fun deleteAllExercise() {
        sqldb = MyDBHelper.writableDatabase
        sqldb.execSQL("DELETE FROM exercise_counter WHERE date = $date")
        sqldb.close()
    }
}