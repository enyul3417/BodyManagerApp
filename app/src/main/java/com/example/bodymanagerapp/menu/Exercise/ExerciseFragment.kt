package com.example.bodymanagerapp.menu.Exercise

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Exercise.Routine.LoadRoutineActivity
import com.example.bodymanagerapp.menu.Exercise.Routine.SavedRoutineActivity
import com.example.bodymanagerapp.MyDBHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class ExerciseFragment : Fragment()/*, SensorEventListener*/ {
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
    lateinit var stepsTextView: TextView
    //lateinit var steps: Steps
    lateinit var sensorManager : SensorManager
    var stepCountSensor : Sensor? = null
    private var steps : Int = 0 // 현재 발걸음 수
    private var counterSteps : Int = 0 // 리스너 등록 후의 발걸음 수

    // 운동
    lateinit var button_exercise_add : Button
    private var date_format : String = ""
    private var name : String = ""

    // 나만의 루틴
    lateinit var button_load_routine : Button
    lateinit var button_save_routine : Button

    lateinit var ct : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)
        ct = container!!.context

        MyDBHelper = MyDBHelper(ct)
        rv = view.findViewById(R.id.recycler_exercise)

        // 운동 타이머
        timer_hour = view.findViewById(R.id.timer_hour)
        timer_minute = view.findViewById(R.id.timer_minute)
        timer_second = view.findViewById(R.id.timer_second)
        button_start = view.findViewById(R.id.button_startnpause)
        button_done = view.findViewById(R.id.button_done)

        // 만보기
        stepsTextView = view.findViewById(R.id.steps)

        // 운동
        button_exercise_add = view.findViewById(R.id.button_exercise_add)

        // 나만의 루틴
        button_load_routine = view.findViewById(R.id.button_load_routine)
        button_save_routine = view.findViewById(R.id.button_save_routine)

        var now = LocalDate.now()
        date_format = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        /*// 만보기 사용을 위한 센서 접근 권한
        var sensorPermission = ContextCompat.checkSelfPermission(ct, Manifest.permission.ACTIVITY_RECOGNITION)
        when {
            sensorPermission == PackageManager.PERMISSION_GRANTED -> {
                // 만보기
                sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                try {
                    stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                    //sensorManager.registerListener(this, stepCountSensor, )
                }
                catch (ise : IllegalStateException){
                    Toast.makeText(ct, "걸음 센서가 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
                var dig = AlertDialog.Builder(ct)
                dig.setTitle("권한이 필요한 이유")
                dig.setMessage("만보기 사용을 위해 센서 사용 권한이 필수로 필요합니다.")
                dig.setPositiveButton("동의") { dialog, which ->
                    requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
                }
                dig.setNegativeButton("취소", null)
                dig.show()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
            }
        }*/

        // 오늘 입력해둔 운동 불러오기
        exerciseData.clear()
        exerciseData.addAll(loadExercise())
        if (exerciseData.size > 0) {
            rvAdapter = ExerciseRecyclerViewAdapter(exerciseData, ct, rv)
            rv.adapter = rvAdapter
            rv.layoutManager = LinearLayoutManager(ct)
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
            val intent : Intent = Intent(ct, SavedRoutineActivity::class.java)
            startActivity(intent)
        }

        // 루틴 불러오기 버튼 클릭 시
        button_load_routine.setOnClickListener {
            val intent : Intent = Intent(ct, LoadRoutineActivity::class.java)
            //startActivity(intent)
            startActivityForResult(intent, REQUEST_CODE_LOAD_ROUTINE)
        }

        // 운동 추가 버튼 클릭 시
        button_exercise_add.setOnClickListener {
            val intent : Intent = Intent(ct, ExerciseAdditionActivity::class.java)
            intent.putExtra("DATE", date_format)
            startActivityForResult(intent, REQUEST_CODE_ADD_EXERCISE)

        }

        return view
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

            activity?.runOnUiThread {
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
/*
    override fun onStart() {
        super.onStart()
        if(stepCountSensor != null) {
            // 센서 속도 결정
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            // TYPE_STEP_COUNTER는 앱이 꺼지더라도 초기화 되지 않음
            // 초기값 필요
            if(counterSteps < 1) {
                counterSteps = event.values[0].toInt()
            }
            // 리셋 안된 값 + 현재 값 - 리셋 안된 값
            steps = event.values[0].toInt() - counterSteps
            stepsTextView.text = steps.toString()
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REQUEST_CODE_ADD_EXERCISE -> {
                    /*name = data?.getStringExtra("NAME").toString()
                    exerciseData.addAll(addExercise())
                    rvAdapter = ExerciseRecyclerViewAdapter(exerciseData, ct, rv)
                    rv.adapter = rvAdapter
                    rv.layoutManager = LinearLayoutManager(ct)
                    rv.visibility = View.VISIBLE*/
                    exerciseData.clear()
                    exerciseData.addAll(loadExercise())
                    if (exerciseData.size > 0) {
                        rvAdapter = ExerciseRecyclerViewAdapter(exerciseData, ct, rv)
                        rv.adapter = rvAdapter
                        rv.layoutManager = LinearLayoutManager(ct)
                        rv.visibility = View.VISIBLE
                    }
                }
                REQUEST_CODE_LOAD_ROUTINE -> {
                    exerciseData.clear()
                    exerciseData.addAll(loadExercise())
                    if (exerciseData.size > 0) {
                        rvAdapter = ExerciseRecyclerViewAdapter(exerciseData, ct, rv)
                        rv.adapter = rvAdapter
                        rv.layoutManager = LinearLayoutManager(ct)
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

                    do {
                        set.add(cursor.getInt(cursor.getColumnIndex("set_num")))
                        weight.add(cursor.getFloat(cursor.getColumnIndex("weight")))
                        num.add(cursor.getInt(cursor.getColumnIndex("exercise_count")))
                        time.add(cursor.getInt(cursor.getColumnIndex("time")))
                    } while (cursor.moveToNext())
                    data.add(ExerciseData(date_format.toInt(), name, set, num, weight, time))
                }
            } while (nameCursor.moveToNext())
        }
        sqldb.close()
        return data
    }

    private fun addExercise() : ArrayList<ExerciseData> {
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
    }

    private fun saveExercise() {
        sqldb = MyDBHelper.writableDatabase
        var hour = timer_hour.text.toString().toInt() * 3600
        var min = timer_minute.text.toString().toInt() * 60
        var time = hour + min + (timer_second.text.toString().toInt())

        sqldb.execSQL("INSERT INTO exercise_record(date, total_time) VALUES (${date_format.toInt()}, $time);")
        sqldb.close()
    }

    companion object {

    }
}