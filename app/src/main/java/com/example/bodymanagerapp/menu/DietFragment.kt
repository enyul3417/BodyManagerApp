package com.example.bodymanagerapp.menu

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.R
import java.util.*

class DietFragment : Fragment() {


    lateinit var text_date : TextView // 날짜
    lateinit var button_diet_save : Button // 저장 버튼
    //식단
    lateinit var text_time : TextView // 시간
    lateinit var image_diet : ImageView // 식단 사진
    lateinit var diet_memo : EditText // 메모
    
    lateinit var ct : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view : View = inflater.inflate(R.layout.fragment_diet, container, false)
        ct = container?.context!!

        // 날짜
        text_date = view.findViewById(R.id.date_text)
        button_diet_save = view.findViewById(R.id.button_diet_save)
        // 식단
        text_time = view.findViewById(R.id.text_diet_time)
        image_diet = view.findViewById(R.id.image_diet)
        diet_memo = view.findViewById(R.id.diet_memo)

        // 날짜 텍스트 클릭 시
        text_date.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                text_date.text = "${y}년 ${m+1}월 ${d}일"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        // 시간 텍스트 클릭 시
        text_time.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(ct, TimePickerDialog.OnTimeSetListener { timePicker, h, m ->
                text_time.text = "${h}시 ${m}분"
            }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
        }

        return view
    }
}