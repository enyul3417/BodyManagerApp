package com.example.bodymanagerapp.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.R

class ExerciseFragment : Fragment() {
    lateinit var timer_hour : EditText
    lateinit var timer_minute : EditText
    lateinit var timer_second : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view : View = inflater.inflate(R.layout.fragment_exercise, container, false)
        timer_hour = view.findViewById(R.id.timer_hour)
        timer_minute = view.findViewById(R.id.timer_minute)
        timer_second = view.findViewById(R.id.timer_second)

        return view
    }
}