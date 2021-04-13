package com.example.bodymanagerapp.menu.Exercise


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bodymanagerapp.R

class ExerciseAdditionActivity : AppCompatActivity() {
    lateinit var exercise_name : EditText // 운동 이름
    lateinit var set_num : EditText // 세트 수

    lateinit var button_weight_number : Button // 무게, 횟수
    lateinit var button_number : Button // 횟수
    lateinit var button_time : Button // 시간

    lateinit var table_weight_num : TableLayout // 무게, 횟수 테이블
    lateinit var table_num : TableLayout // 횟수 테이블
    lateinit var table_time : TableLayout // 시간 테이블
    lateinit var table_exercise_count : TableLayout // 각각 값 입력 받는 곳

    private var snum : Int = 0 // 세트 수
    // id 값에 사용
    private val SET_ID : Int = 100 // 세트
    private val NUM_ID : Int = 200 // 횟수
    private val WEIGHT_ID : Int = 300 // 무게
    private val HOUR_ID : Int = 400 // 시
    private val MIN_ID : Int = 500 // 분
    private val SEC_ID : Int = 600 // 초

    lateinit var button_exercise_add_done : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_addition)

        exercise_name = findViewById(R.id.exercise_name)
        set_num = findViewById(R.id.set_num)

        button_weight_number = findViewById(R.id.button_weight_number)
        button_number = findViewById(R.id.button_number)
        button_time = findViewById(R.id.button_time)

        table_weight_num = findViewById(R.id.table_weight_num)
        table_num = findViewById(R.id.table_num)
        table_time = findViewById(R.id.table_time)
        table_exercise_count = findViewById(R.id.table_exercise_count)

        button_exercise_add_done = findViewById(R.id.button_exercise_add_done)

        button_weight_number.setOnClickListener{
            table_weight_num.visibility = View.VISIBLE
            table_num.visibility = View.GONE
            table_time.visibility = View.GONE

            table_exercise_count.removeAllViews() // 기존에 만들어진 것들 다 없애기

            try {
                snum = Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            }
            catch (nfe : NumberFormatException) {
                snum = 1;
            }
                for(i in 1..snum) {
                    val tableRow = TableRow(this)
                   tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    table_exercise_count.addView(tableRow)
                    // 세트 수
                    val setTV = TextView(this)
                    setTV.id = SET_ID+i // 아이디 값
                    setTV.textSize = 15f // 글자 크기
                    setTV.text = "$i"
                    setTV.gravity = 17 // 중앙 정렬
                    tableRow.addView(setTV)
                    // 무게
                    val weightET = EditText(this)
                    weightET.id = WEIGHT_ID+i // 아이디 값
                    weightET.textSize = 15f // 글자 크기
                    weightET.gravity = 17 // 중앙 정렬
                    tableRow.addView(weightET)
                    // 횟수
                    val numET = EditText(this)
                    numET.id = NUM_ID+i // 아이디 값
                    numET.textSize = 15f // 글자 크기
                    numET.gravity = 17 // 중앙 정렬
                    tableRow.addView(numET)

                    table_exercise_count.visibility = View.VISIBLE
                }
        }

        button_number.setOnClickListener {
            table_weight_num.visibility = View.GONE
            table_num.visibility = View.VISIBLE
            table_time.visibility = View.GONE

            table_exercise_count.removeAllViews() // 기존에 만들어진 것들 다 없애기

            try {
                snum = Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            }
            catch (nfe : NumberFormatException) {
                snum = 1;
            }
            for(i in 1..snum) {
                val tableRow = TableRow(this)
                tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                table_exercise_count.addView(tableRow)
                // 세트 수
                val setTV = TextView(this)
                setTV.id = SET_ID+i // 아이디 값
                setTV.textSize = 15f // 글자 크기
                setTV.text = "$i"
                setTV.gravity = 17 // 중앙 정렬
                tableRow.addView(setTV)
                // 횟수
                val numET = EditText(this)
                numET.id = NUM_ID+i // 아이디 값
                numET.textSize = 15f // 글자 크기
                numET.gravity = 17 // 중앙 정렬
                tableRow.addView(numET)

                table_exercise_count.visibility = View.VISIBLE
            }
        }

        button_time.setOnClickListener {
            table_weight_num.visibility = View.GONE
            table_num.visibility = View.GONE
            table_time.visibility = View.VISIBLE

            table_exercise_count.removeAllViews() // 기존에 만들어진 것들 다 없애기

            try {
                snum = Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            }
            catch (nfe : NumberFormatException) {
                snum = 1;
            }
            for(i in 1..snum) {
                val tableRow = TableRow(this)
                tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                table_exercise_count.addView(tableRow)
                // 세트 수
                val setTV = TextView(this)
                setTV.id = SET_ID+i // 아이디 값
                setTV.textSize = 15f // 글자 크기
                setTV.text = "$i"
                setTV.gravity = 17 // 중앙 정렬
                tableRow.addView(setTV)
                // 시
                val hourET = EditText(this)
                hourET.id = HOUR_ID+i // 아이디 값
                hourET.textSize = 15f // 글자 크기
                hourET.gravity = 17 // 중앙 정렬
                tableRow.addView(hourET)
                // 분
                val minET = EditText(this)
                minET.id = MIN_ID+i // 아이디 값
                minET.textSize = 15f // 글자 크기
                minET.gravity = 17 // 중앙 정렬
                tableRow.addView(minET)
                // 초
                val secET = EditText(this)
                secET.id = SEC_ID+i // 아이디 값
                secET.textSize = 15f // 글자 크기
                secET.gravity = 17 // 중앙 정렬
                tableRow.addView(secET)

                table_exercise_count.visibility = View.VISIBLE
            }
        }

        button_exercise_add_done.setOnClickListener {
            saveExercise()
        }
    }

    fun saveExercise() {

    }
}