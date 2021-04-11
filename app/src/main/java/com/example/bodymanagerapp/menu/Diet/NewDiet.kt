package com.example.bodymanagerapp.menu.Diet

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.example.bodymanagerapp.R

class NewDiet constructor(
        context: Context, attrs : AttributeSet? = null, defStyle : Int = 0
) : LinearLayout(context, attrs, defStyle){
    init {
        //val linearLayout : LinearLayout = findViewById(R.id.new_diet)
        val inflater : LayoutInflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.new_diet, this, true)
        //addView(view)

        val button_diet_save : Button = view.findViewById(R.id.button_diet_save) // 저장 버튼
        val button_diet_delete : Button = view.findViewById(R.id.button_diet_delete) // 삭제 버튼
        val text_time : TextView = view.findViewById(R.id.text_diet_time) // 시간
        val image_diet : ImageView = view.findViewById(R.id.image_diet) // 식단 사진
        val diet_memo : EditText = view.findViewById(R.id.diet_memo) // 식단 메모
    }



}