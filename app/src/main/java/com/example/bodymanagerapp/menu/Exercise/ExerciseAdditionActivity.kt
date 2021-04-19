package com.example.bodymanagerapp.menu.Exercise


import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.myDBHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min

class ExerciseAdditionActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb : SQLiteDatabase

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
    //private val SET_ID : Int = 100 // 세트
    private val NUM_ID : Int = 200 // 횟수
    private val WEIGHT_ID : Int = 300 // 무게
    private val TIME_ID : Int = 400 // 시

    lateinit var button_exercise_add_done : Button // 운동 추가 버튼

    lateinit var date : String // 현재 날짜

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_addition)

        toolbar = findViewById(R.id.toolbar)
        myDBHelper = myDBHelper(this)

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

//        var now = LocalDate.now() // 현재 날짜 가져오기
//        date = now.format(DateTimeFormatter.ofPattern("yyyy년MM월dd일"))

        date = intent.getStringExtra("DATE").toString()

        // 무게, 횟수 버튼 클릭 시
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
                    //setTV.id = SET_ID+i // 아이디 값
                    setTV.textSize = 15f // 글자 크기
                    setTV.text = "$i"
                    setTV.gravity = 17 // 중앙 정렬
                    tableRow.addView(setTV)
                    // 무게
                    val weightET = EditText(this)
                    weightET.id = WEIGHT_ID+i // 아이디 값
                    weightET.textSize = 15f // 글자 크기
                    weightET.gravity = 17 // 중앙 정렬
                    weightET.inputType = 2 // 숫자 키패트
                    tableRow.addView(weightET)
                    // 횟수
                    val numET = EditText(this)
                    numET.id = NUM_ID+i // 아이디 값
                    numET.textSize = 15f // 글자 크기
                    numET.gravity = 17 // 중앙 정렬
                    numET.inputType = 2 // 숫자 키패트
                    tableRow.addView(numET)

                    table_exercise_count.visibility = View.VISIBLE
                }
        }
        // 횟수 버튼 클릭 시
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
                //setTV.id = SET_ID+i // 아이디 값
                setTV.textSize = 15f // 글자 크기
                setTV.text = "$i"
                setTV.gravity = 17 // 중앙 정렬
                tableRow.addView(setTV)
                // 횟수
                val numET = EditText(this)
                numET.id = NUM_ID+i // 아이디 값
                numET.textSize = 15f // 글자 크기
                numET.gravity = 17 // 중앙 정렬
                numET.inputType = 2 // 숫자 키패트
                tableRow.addView(numET)

                table_exercise_count.visibility = View.VISIBLE
            }
        }
        // 시간 버튼 클릭 시
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
                //setTV.id = SET_ID+i // 아이디 값
                setTV.textSize = 15f // 글자 크기
                setTV.text = "$i"
                setTV.gravity = 17 // 중앙 정렬
                tableRow.addView(setTV)
                // 시간
                val hourET = EditText(this)
                hourET.id = TIME_ID+i // 아이디 값
                hourET.textSize = 15f // 글자 크기
                hourET.gravity = 17 // 중앙 정렬
                hourET.inputType = 4 // 숫자 키패트
                tableRow.addView(hourET)

                table_exercise_count.visibility = View.VISIBLE
            }
        }
        // 운동 추가 완료
        button_exercise_add_done.setOnClickListener {
            addExercise()
            val intent = Intent(this, ExerciseActivity::class.java)
            //intent.putExtra("DATE", date)
            intent.putExtra("NAME", exercise_name.text.toString())
            setResult(Activity.RESULT_OK, intent)
            Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 정리 필요함
    private fun addExercise() {
        sqldb = myDBHelper.writableDatabase

        var weightArray : ArrayList<Int> ?= ArrayList() // 무게 배열
        var numArray : ArrayList<Int> ?= ArrayList() // 횟수 배열
        var timeArray : ArrayList<String> ?= ArrayList() // 시간 배열

        if(findViewById<EditText>(WEIGHT_ID+1) != null) { // 입력한 무게 값이 있으면
            for(i in 1..snum) { // 사용자가 입력한 값 가져오기
                var weight: EditText = findViewById(WEIGHT_ID + i)
                weightArray?.add(Integer.parseInt(weight.text.toString()))
            }
        } else {
            for(i in 1..snum) { // 사용자가 입력한 값 가져오기
                weightArray = null
            }
        }

        if(findViewById<EditText>(NUM_ID+1) != null) { // 입력한 횟수 값이 있으면
            for(i in 1..snum) { // 사용자가 입력한 값 가져오기
                var num : EditText = findViewById(NUM_ID+i)
                numArray?.add(Integer.parseInt(num.text.toString()))
            }
        } else {
            for(i in 1..snum) { // 사용자가 입력한 값 가져오기
                numArray = null
            }
        }

        if(findViewById<EditText>(TIME_ID+1) != null) { // 입력한 시간 값이 있으면
            for(i in 1..snum) { // 사용자가 입력한 값 가져오기
                var time: EditText = findViewById(TIME_ID + i)
                timeArray?.add(time.text.toString())
            }
        } else {
            for(i in 1..snum) { // 사용자가 입력한 값 가져오기
                timeArray = null
            }
        }

        // db에 저장하기
        for(i in 1..snum) {
            sqldb.execSQL("INSERT INTO exercise_counter VALUES ('$date','${exercise_name.text.toString()}', " +
                    "$i, ${weightArray?.get(i-1)}, ${numArray?.get(i-1)}, '${timeArray?.get(i-1)}', 0);")
        }
    }
}