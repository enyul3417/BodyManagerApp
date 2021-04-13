package com.example.bodymanagerapp.menu.Exercise

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bodymanagerapp.MainActivity
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import kotlin.concurrent.timer

class ExerciseActivity : AppCompatActivity(), SensorEventListener {
    lateinit var bottom_nav_view : BottomNavigationView
    lateinit var toolbar: Toolbar

    // 권한 변수
    private val REQUEST_ACTIVITY_RECOGNITION = 1000

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
    lateinit var button_exercise_add :Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        // 운동 타이머
        timer_hour = findViewById(R.id.timer_hour)
        timer_minute = findViewById(R.id.timer_minute)
        timer_second = findViewById(R.id.timer_second)
        button_start = findViewById(R.id.button_startnpause)
        button_done = findViewById(R.id.button_done)

        // 만보기
        stepsTextView = findViewById(R.id.steps)

        // 운동
        button_exercise_add = findViewById(R.id.button_exercise_add)

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)

        // 만보기 사용을 위한 센서 접근 권한
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
        }

        button_start.setOnClickListener {
            isRunning = !isRunning

            if(isRunning)
                exerciseStart()
            else
                exerciseStop()
        }

        button_done.setOnClickListener {
            exerciseDone()
        }

        button_exercise_add.setOnClickListener {
            val intent : Intent = Intent(this, ExerciseAdditionActivity::class.java)
            startActivity(intent)
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
                        return@OnNavigationItemSelectedListener true
                    }

                    // 상태 메뉴 선택 시
                    R.id.navigation_stats -> {
                        return@OnNavigationItemSelectedListener true
                    }

                    // 펫 선택 시
                    R.id.navigation_pet -> {
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
                MainActivity().replaceFragment(SettingsFragment())
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

    override fun onStop() {
        super.onStop()
        if(sensorManager != null) {
            sensorManager.unregisterListener(this)
        }
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
    }
}