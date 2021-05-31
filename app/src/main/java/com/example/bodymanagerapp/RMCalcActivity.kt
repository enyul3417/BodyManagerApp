package com.example.bodymanagerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RMCalcActivity : AppCompatActivity() {
    lateinit var weightET : EditText
    lateinit var countET: EditText
    lateinit var resultTV : TextView
    lateinit var btn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_m_calc)

        weightET = findViewById(R.id.et_calc_weight)
        countET = findViewById(R.id.et_calc_count)
        resultTV = findViewById(R.id.tv_calc_result)
        btn = findViewById(R.id.btn_calc)

        btn.setOnClickListener {
            if(weightET.text != null && countET.text != null) {
                val weight : Float = weightET.text.toString().toFloat()
                val count : Int = countET.text.toString().toInt()
                val result : Float = weight + (weight * count * 0.025f)

                resultTV.text = "$result kg"
            } else {
                Toast.makeText(this, "무게와 횟수를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}