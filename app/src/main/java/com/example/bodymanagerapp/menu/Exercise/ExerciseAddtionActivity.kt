package com.example.bodymanagerapp.menu.Exercise

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bodymanagerapp.R

class ExerciseAddtionActivity : AppCompatActivity() {
    lateinit var exercise_name : EditText
    lateinit var set_num : EditText

    lateinit var button_weight_number : Button
    lateinit var button_number : Button
    lateinit var button_time : Button

    lateinit var table : LinearLayout
    lateinit var table_weight_num : TableLayout
    lateinit var table_num : TableLayout
    lateinit var table_time : TableLayout

    lateinit var button_exercise_add_done : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_addtion)

        exercise_name = findViewById(R.id.exercise_name)
        set_num = findViewById(R.id.set_num)

        button_weight_number = findViewById(R.id.button_weight_number)
        button_number = findViewById(R.id.button_number)
        button_time = findViewById(R.id.button_time)

        table = findViewById(R.id.table_layout)
        table_weight_num = findViewById(R.id.table_weight_num)
        table_num = findViewById(R.id.table_num)
        table_time = findViewById(R.id.table_time)

        button_exercise_add_done = findViewById(R.id.button_exercise_add_done)

        button_weight_number.setOnClickListener{
            table_weight_num.visibility = View.VISIBLE
            table_num.visibility = View.GONE
            table_time.visibility = View.GONE

            /*table.removeAllViews()
            val tableLayout = TableLayout(this)
            tableLayout.layoutParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            table.addView(tableLayout)

            val tableRow = TableRow(this)
            tableLayout.addView(tableRow)
            val setText = TextView(this)
            setText.text = "세트"
            tableRow.addView(setText)
            val weighText = TextView(this)
            setText.text = "무게"
            tableRow.addView(weighText)
            val numText = TextView(this)
            setText.text = "횟수"
            tableRow.addView(numText)*/

           // table_weight_num.removeAllViews()

            try {
                var num = Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요

                for(i in 1..num) {
                    val tableRow = TableRow(this)
                   // tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            //ViewGroup.LayoutParams.WRAP_CONTENT)
                    table_weight_num.addView(tableRow)
                    for(j in 1..3) {
                        val editText = EditText(this)
                        editText.id = i*j
                        editText.textSize = 15f
                        tableRow.addView(editText)
                    }
                }
            }
            catch (nfe : NumberFormatException) {
                Toast.makeText(this, "세트 수를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            
        }

        button_number.setOnClickListener {
            table_weight_num.visibility = View.GONE
            table_num.visibility = View.VISIBLE
            table_time.visibility = View.GONE

            try {
                var num = Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요

                for(i in 1..num) {
                    val tableRow = TableRow(this)
                    // tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    //ViewGroup.LayoutParams.WRAP_CONTENT)
                    table_num.addView(tableRow)
                    for(j in 1..2) {
                        val editText = EditText(this)
                        editText.id = i*j
                        editText.textSize = 15f
                        tableRow.addView(editText)
                    }
                }
            }
            catch (nfe : NumberFormatException) {
                Toast.makeText(this, "세트 수를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        button_time.setOnClickListener {
            table_weight_num.visibility = View.GONE
            table_num.visibility = View.GONE
            table_time.visibility = View.VISIBLE

            try {
                var num = Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요

                for(i in 1..num) {
                    val tableRow = TableRow(this)
                    // tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    //ViewGroup.LayoutParams.WRAP_CONTENT)
                    table_time.addView(tableRow)
                    for(j in 1..4) {
                        val editText = EditText(this)
                        editText.id = i*j
                        editText.textSize = 15f
                        tableRow.addView(editText)
                    }
                }
            }
            catch (nfe : NumberFormatException) {
                Toast.makeText(this, "세트 수를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        button_exercise_add_done.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_exercise,
                    ExerciseFragment().apply {
                        arguments = Bundle().apply {
                            putString("exercise_name", "${exercise_name.text.toString()}")
                            putString("set_num", "${set_num.text.toString()}")
                            //putString()
                        }
                    }
            ).commit()
        }
    }
}