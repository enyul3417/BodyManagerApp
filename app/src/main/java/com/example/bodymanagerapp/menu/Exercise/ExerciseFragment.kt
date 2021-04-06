package com.example.bodymanagerapp.menu.Exercise

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bodymanagerapp.MainActivity
import com.example.bodymanagerapp.R
import java.util.*
import kotlin.concurrent.timer

class ExerciseFragment : Fragment(){
    private var time = 0
    private var isRunning = false
    private var timerTask : Timer? = null

    // 운동 타이머
    lateinit var timer_hour : TextView
    lateinit var timer_minute : TextView
    lateinit var timer_second : TextView
    lateinit var button_start : Button
    lateinit var button_done : Button

    // 만보기
    //lateinit var steps : TextView

    // 운동 추가
    lateinit var button_exercise_add :Button

    fun newInstance() : ExerciseFragment {
        return ExerciseFragment()
    }

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
        //steps = view.findViewById(R.id.steps)
        button_exercise_add = view.findViewById(R.id.button_exercise_add)

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

    // 운동 시작
    private fun start() {
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

}