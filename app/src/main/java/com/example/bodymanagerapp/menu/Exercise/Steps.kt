package com.example.bodymanagerapp.menu.Exercise

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService

class Steps : SensorEventListener {
    private var steps : Int = 0 // 현재 발걸음 수
    private var counterSteps : Int = 0 // 리스너 등록 후의 발걸음 수
    lateinit var ct : Context

    // 센서 연결을 위한 변수
    lateinit var sensorManager : SensorManager
    lateinit var stepCountSensor : Sensor

    fun countSteps() {
        // 센서 연결 -> 걸음수 센서를 이용한 흔들림 감지
        //sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            // TYPE_STEP_COUNTER는 앱이 꺼지더라도 초기화 되지 않음
            // 초기값 필요
            if(counterSteps < 1) {
                //counterSteps = (int) event.values[0]
            }
        }
    }
}