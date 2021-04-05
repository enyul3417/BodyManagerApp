package com.example.bodymanagerapp.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.MainActivity
import com.example.bodymanagerapp.R
import java.util.*
import kotlin.concurrent.timer

class ExerciseFragment : Fragment() {
    private var time = 0
    private var isRunning = false
    private var timerTask : Timer? = null

    // 타이머/스톱워치
    lateinit var timer_hour : TextView
    lateinit var timer_minute : TextView
    lateinit var timer_second : TextView
    lateinit var button_start : Button
    lateinit var button_done : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view : View = inflater.inflate(R.layout.fragment_exercise, container, false)

        timer_hour = view.findViewById(R.id.timer_hour)
        timer_minute = view.findViewById(R.id.timer_minute)
        timer_second = view.findViewById(R.id.timer_second)
        button_start = view.findViewById(R.id.button_startnpause)
        button_done = view.findViewById(R.id.button_done)

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

        return view
    }

    private fun start() { // 운동 시작
        button_start.setText("운동 중지")
        timerTask = timer(period=1000) { // 1초
            time++

            val hour = time / 3600
            val min = (time % 3600) / 60
            val sec = time % 60

            val minStr = String.format("%02d", min)
            val secStr = String.format("%02d", sec)

            activity?.runOnUiThread {
                timer_hour.setText(hour.toString())
                timer_minute.setText(minStr)
                timer_second.setText(secStr)
            }
        }
    }

    private fun stop() { // 운동 중지
        button_start.text = "운동 시작"
        timerTask?.cancel()
    }

    private fun done() { // 운동 완료
        timerTask?.cancel()
        isRunning = false
        button_start.text = "운동 시작"
        timer_hour.text = "0"
        timer_minute.text = "00"
        timer_second.text = "00"
    }
}