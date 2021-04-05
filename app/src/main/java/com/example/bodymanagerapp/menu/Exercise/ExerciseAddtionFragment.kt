package com.example.bodymanagerapp.menu.Exercise

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import com.example.bodymanagerapp.R

class ExerciseAddtionFragment : Fragment() {
    lateinit var ct : Context

    lateinit var search_exercise : SearchView

    lateinit var btn_weight_number : Button
    lateinit var btn_number : Button
    lateinit var btn_time : Button
    lateinit var contentFrame : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_exercise_addtion, container, false)
        ct = container!!.context

        search_exercise = view.findViewById(R.id.search_exercise)

        btn_weight_number = view.findViewById(R.id.button_weight_number)
        btn_number = view.findViewById(R.id.button_number)
        btn_number = view.findViewById(R.id.button_time)
        contentFrame = view.findViewById(R.id.content_frame)

        search_exercise.setOnQueryTextListener(object  : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })

        btn_weight_number.setOnClickListener {
            var inflater : LayoutInflater = ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.exercise_weight_number, contentFrame, true)
        }



        return view
    }


}