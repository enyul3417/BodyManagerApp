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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bodymanagerapp.MainActivity
import com.example.bodymanagerapp.R
import java.security.AccessController.getContext
import java.util.*
import kotlin.concurrent.timer

class ExerciseFragment : Fragment(), SensorEventListener{
    // 권한 변수
    private val REQUEST_ACTIVITY_RECOGNITION = 1000

    private var time = 0 // 총 시간
    private var isRunning = false
    private var timerTask : Timer? = null

    // 운동 타이머
    lateinit var timer_hour : TextView
    lateinit var timer_minute : TextView
    lateinit var timer_second : TextView
    lateinit var button_start : Button
    lateinit var button_done : Button

    // 만보기
    lateinit var stepsTextView: TextView
    //lateinit var steps: Steps
    lateinit var sensorManager : SensorManager
    lateinit var stepCountSensor : Sensor
    private var steps : Int = 0 // 현재 발걸음 수
    private var counterSteps : Int = 0 // 리스너 등록 후의 발걸음 수


    // 운동 추가
    lateinit var button_exercise_add :Button

    lateinit var ct : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 만보기 사용을 위한 센서 접근 권한
        var sensorPermission = ContextCompat.checkSelfPermission(ct, Manifest.permission.ACTIVITY_RECOGNITION)
        if(sensorPermission != PackageManager.PERMISSION_GRANTED) { // 권한이 허용되지 않은 경우

        } else {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view : View = inflater.inflate(R.layout.fragment_exercise, container, false)

        //ct = container?.context!!

        timer_hour = view.findViewById(R.id.timer_hour)
        timer_minute = view.findViewById(R.id.timer_minute)
        timer_second = view.findViewById(R.id.timer_second)
        button_start = view.findViewById(R.id.button_startnpause)
        button_done = view.findViewById(R.id.button_done)

        stepsTextView = view.findViewById(R.id.steps)

        button_exercise_add = view.findViewById(R.id.button_exercise_add)

        // 만보기
        sensorManager = ct.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        try {
            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            //sensorManager.registerListener(this, stepCountSensor, )
        }
        catch (ise : IllegalStateException){
            Toast.makeText(ct, "걸음 센서가 없습니다", Toast.LENGTH_SHORT).show()
        }

        /*if(stepCountSensor == null) {
            Toast.makeText(ct, "걸음 센서가 없습니다", Toast.LENGTH_SHORT).show()
        }*/

        button_start.setOnClickListener {
            isRunning = !isRunning

            if(isRunning)
                start()
            else
                stop()
        }

        button_done.setOnClickListener {
            done()
        }

        button_exercise_add.setOnClickListener {
            val intent : Intent = Intent(activity, ExerciseAddtionActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity)
            ct = context
    }

    /*companion object {
        const val KEY = "Key"
        fun newInstance(data : String) = ExerciseFragment().apply {
            arguments = Bundle().apply {
                putString(KEY, data)
            }
        }
    }

    val receiveData by lazy { requireArguments().getString(KEY) }*/

    // 운동 시작
    private fun start() {
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
    private fun stop() {
        button_start.text = "운동 시작"
        timerTask?.cancel()
    }

    // 운동 완료
    private fun done() {
        timerTask?.cancel()
        isRunning = false
        button_start.text = "운동 시작"
        timer_hour.text = "0"
        timer_minute.text = "00"
        timer_second.text = "00"
    }

    fun walkStart() {
        super.onStart()
        if(stepCountSensor != null) {
            // 센서 속도 결정
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun walkStop() {
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