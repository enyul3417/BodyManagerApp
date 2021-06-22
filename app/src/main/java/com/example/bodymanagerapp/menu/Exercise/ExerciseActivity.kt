package com.example.bodymanagerapp.menu.Exercise

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Body.BodyActivity
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.Exercise.Routine.LoadRoutineActivity
import com.example.bodymanagerapp.menu.Exercise.Routine.SavedRoutineActivity
import com.example.bodymanagerapp.menu.Pet.PetActivity
import com.example.bodymanagerapp.menu.Stats.StatsActivity
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.menu.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

// 운동 수정 후 새로 고침이 안됨

class ExerciseActivity : AppCompatActivity(), SensorEventListener {
    lateinit var bottom_nav_view : BottomNavigationView
    lateinit var toolbar: Toolbar

    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    var exerciseData = ArrayList<ExerciseData>()

    // View
    lateinit var rv : RecyclerView
    lateinit var rvAdapter: ExerciseRecyclerViewAdapter

    // 권한 변수
    private val REQUEST_ACTIVITY_RECOGNITION = 1000
    private val REQUEST_CODE_ADD_EXERCISE = 100
    private val REQUEST_CODE_LOAD_ROUTINE = 200
    private val REQUEST_CODE_SAVE_ROUTINE = 300

    private var time = 0 // 총 시간
    private var isRunning = false
    private var timerTask : Timer? = null

    // 운동 타이머
    lateinit var timer_hour : TextView // 시
    lateinit var timer_minute : TextView // 분
    lateinit var timer_second : TextView // 초
    lateinit var button_start : Button // 시작 및 중지
    lateinit var button_done : Button // 운동 끝

    // 만보기
    lateinit var stepsLinear : LinearLayout
    lateinit var stepsTextView: TextView
    //lateinit var steps: Steps
    lateinit var sensorManager : SensorManager
    var stepCountSensor : Sensor? = null
    private var steps : Int = 0 // 현재 발걸음 수
    private var counterSteps : Int = 0 // 리스너 등록 후의 발걸음 수

    // 운동
    lateinit var button_exercise_add :Button
    private var date_format : String = ""
    private var name : String = ""

    // 나만의 루틴
    lateinit var button_load_routine : Button
    lateinit var button_save_routine : Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        MyDBHelper = MyDBHelper(this)
        rv = findViewById(R.id.recycler_exercise)

        // 운동 타이머
        timer_hour = findViewById(R.id.timer_hour)
        timer_minute = findViewById(R.id.timer_minute)
        timer_second = findViewById(R.id.timer_second)
        button_start = findViewById(R.id.button_startnpause)
        button_done = findViewById(R.id.button_done)

        // 만보기
        stepsTextView = findViewById(R.id.steps)
        stepsLinear = findViewById(R.id.ll_steps)

        // 운동
        button_exercise_add = findViewById(R.id.button_exercise_add)

        // 나만의 루틴
        button_load_routine = findViewById(R.id.button_load_routine)
        button_save_routine = findViewById(R.id.button_save_routine)

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        bottom_nav_view.menu.findItem(R.id.navigation_exercise).isChecked = true
        setSupportActionBar(toolbar)

        var now = LocalDate.now()
        date_format = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        /*// 만보기 사용을 위한 센서 접근 권한
        var sensorPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)

        if(sensorPermission != PackageManager.PERMISSION_GRANTED) { // 권한이 허용되지 않은 경우
            // 권한이 허용되지 않음
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                // 이전에 이미 권한이 거부되었을 때 설명
                var dig = AlertDialog.Builder(this)
                dig.setTitle("권한이 필요한 이유")
                dig.setMessage("만보기 사용을 위해 센서 사용 권한이 필수로 필요합니다.")
                dig.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
                }
                dig.setNegativeButton("취소", null)
                dig.show()
            } else {
                // 처음 권한 요청
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
        }

        // 만보기
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        try {
            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            //sensorManager.registerListener(this, stepCountSensor, )
        }
        catch (ise : IllegalStateException){
            Toast.makeText(this, "걸음 센서가 없습니다", Toast.LENGTH_SHORT).show()
        }*/
        // 만보기 권한 확인
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
        }

        // 만보기
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // 디바이스에 걸음 센서 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(this, "걸음 센서가 없습니다.", Toast.LENGTH_SHORT).show()
            stepsLinear.visibility = GONE
        }

        // 오늘 입력해둔 운동 불러오기
        exerciseData.clear()
        exerciseData.addAll(loadExercise())
        if (exerciseData.size > 0) {
            rvAdapter = ExerciseRecyclerViewAdapter(exerciseData, this, rv)
            rv.adapter = rvAdapter
            rv.layoutManager = LinearLayoutManager(this)
            rv.visibility = View.VISIBLE
        }

        // 운동 시작 버튼 클릭 시
        button_start.setOnClickListener {
            isRunning = !isRunning

            if(isRunning)
                exerciseStart()
            else
                exerciseStop()
        }

        // 운동 완료 버튼 클릭 시
        button_done.setOnClickListener {
            saveExercise()
            exerciseDone()
        }

        // 루틴 저장 버튼 클릭 시
        button_save_routine.setOnClickListener {
            val intent : Intent = Intent(this, SavedRoutineActivity::class.java)
            startActivity(intent)
        }

        // 루틴 불러오기 버튼 클릭 시
        button_load_routine.setOnClickListener {
            val intent : Intent = Intent(this, LoadRoutineActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_LOAD_ROUTINE)
        }

        // 운동 추가 버튼 클릭 시
        button_exercise_add.setOnClickListener {
            val intent : Intent = Intent(this, ExerciseAdditionActivity::class.java)
            //intent.putExtra("DATE", date_format)
            startActivityForResult(intent, REQUEST_CODE_ADD_EXERCISE)
        }
    }

    // 하단 메뉴 선택 시 작동
    private val bottomNavItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    // 운동 메뉴 선택 시
                    R.id.navigation_exercise -> {
                        var intent: Intent = Intent(this, ExerciseActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 식단 메뉴 선택 시
                    R.id.navigation_diet -> {
                        var intent: Intent = Intent(this, DietActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 신체 메뉴 선택 시
                    R.id.navigation_body -> {
                        var intent: Intent = Intent(this, BodyActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 통계 메뉴 선택 시
                    R.id.navigation_stats -> {
                        var intent : Intent = Intent(this, StatsActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 펫 선택 시
                    R.id.navigation_pet -> {
                        var intent : Intent = Intent(this, PetActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 그 외
                    else -> false
                }
            }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.menu_settings -> {
                var intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 운동 시작
    private fun exerciseStart() {
        button_start.text = "운동 중지"
        timerTask = timer(period=1000) { // 1초
            time++

            val hour = time / 3600
            val min = (time % 3600) / 60
            val sec = time % 60

            val minStr = String.format("%02d", min)
            val secStr = String.format("%02d", sec)

            runOnUiThread {
                timer_hour.text = hour.toString()
                timer_minute.text = minStr
                timer_second.text = secStr
            }
        }
    }

    // 운동 중지
    private fun exerciseStop() {
        button_start.text = "운동 시작"
        timerTask?.cancel()
    }

    // 운동 완료
    private fun exerciseDone() {
        timerTask?.cancel()
        isRunning = false
        button_start.text = "운동 시작"
        timer_hour.text = "0"
        timer_minute.text = "00"
        timer_second.text = "00"
    }

    override fun onStart() {
        super.onStart()
        if(stepCountSensor != null) {
            // 센서 속도 결정
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    /*override fun onStop() {
        super.onStop()
        if(sensorManager != null) {
            sensorManager.unregisterListener(this)
        }
    }*/

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            // TYPE_STEP_COUNTER는 앱이 꺼지더라도 초기화 되지 않음
            // 초기값 필요
            /*if(counterSteps < 1) {
                counterSteps = event.values[0].toInt()
            }
            // 리셋 안된 값 + 현재 값 - 리셋 안된 값
            steps = event.values[0].toInt() - counterSteps
            stepsTextView.text = steps.toString()*/

            if(event!!.values[0] == 1.0f) {
                counterSteps++
                stepsTextView.text = counterSteps.toString()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REQUEST_CODE_ADD_EXERCISE, REQUEST_CODE_LOAD_ROUTINE -> {
                    /*name = data?.getStringExtra("NAME").toString()
                    exerciseData.addAll(addExercise())
                    rvAdapter = ExerciseRecyclerViewAdapter(exerciseData, this, rv)
                    rv.adapter = rvAdapter
                    rv.layoutManager = LinearLayoutManager(this)
                    rv.visibility = View.VISIBLE*/
                    exerciseData.clear()
                    exerciseData.addAll(loadExercise())
                    if (exerciseData.size > 0) {
                        rvAdapter = ExerciseRecyclerViewAdapter(exerciseData, this, rv)
                        rv.adapter = rvAdapter
                        rv.layoutManager = LinearLayoutManager(this)
                        rv.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun loadExercise() : ArrayList<ExerciseData>{
        Log.d("exercise", "신호 수신")

        var data = ArrayList<ExerciseData>()
        sqldb = MyDBHelper.readableDatabase

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
                    var complete = ArrayList<Int>()

                    do {
                        set.add(cursor.getInt(cursor.getColumnIndex("set_num")))
                        weight.add(cursor.getFloat(cursor.getColumnIndex("weight")))
                        num.add(cursor.getInt(cursor.getColumnIndex("exercise_count")))
                        time.add(cursor.getInt(cursor.getColumnIndex("time")))
                        complete.add(cursor.getInt(cursor.getColumnIndex("is_complete")))
                    } while (cursor.moveToNext())
                    data.add(ExerciseData(date_format.toInt(), name, set, num, weight, time, complete))
                }
            } while (nameCursor.moveToNext())
        }
        sqldb.close()
        return data
    }

   /* private fun addExercise() : ArrayList<ExerciseData> {
        Log.d("exercise", "신호 수신")

        var data = ArrayList<ExerciseData>()
        sqldb = MyDBHelper.readableDatabase

        var cursor = sqldb.rawQuery("SELECT * FROM exercise_counter WHERE date = ${date_format.toInt()} AND exercise_name = '${name}';", null)

        if(cursor.moveToFirst()) {
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

            data.add(ExerciseData(date_format.toInt(), name, set, num, weight, time))
            sqldb.close()
        }

        return data
    }*/

    private fun saveExercise() {
        sqldb = MyDBHelper.writableDatabase
        var hour = timer_hour.text.toString().toInt() * 3600
        var min = timer_minute.text.toString().toInt() * 60
        var time = hour + min + (timer_second.text.toString().toInt())

        sqldb.execSQL("INSERT INTO exercise_record(date, total_time) VALUES (${date_format.toInt()}, $time);")
        sqldb.close()
    }
}