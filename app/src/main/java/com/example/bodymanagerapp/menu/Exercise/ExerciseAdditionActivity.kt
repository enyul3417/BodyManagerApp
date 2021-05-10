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

class ExerciseAdditionActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    lateinit var exercise_name: EditText // 운동 이름
    lateinit var set_num: EditText // 세트 수

    lateinit var button_weight_number: Button // 무게, 횟수
    lateinit var button_number: Button // 횟수
    lateinit var button_time: Button // 시간

    lateinit var table_weight_num: TableLayout // 무게, 횟수 테이블
    lateinit var table_num: TableLayout // 횟수 테이블
    lateinit var table_time: TableLayout // 시간 테이블
    lateinit var table_exercise_count: TableLayout // 각각 값 입력 받는 곳

    private var snum: Int = 0 // 세트 수
    private var isLoaded : Boolean = false

    // id 값에 사용
    //private val SET_ID : Int = 100 // 세트
    private val NUM_ID: Int = 200 // 횟수
    private val WEIGHT_ID: Int = 300 // 무게
    //private val TIME_ID: Int = 400 // 시간
    private val HOUR_ID : Int = 500
    private val MIN_ID : Int = 600
    private val SEC_ID : Int = 700

    private lateinit var button_exercise_add_done: Button // 운동 추가 버튼

    var date: Int = 0 // 현재 날짜
    var name: String = "" // 운동 이름

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

        var now = LocalDate.now()
        date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()

        //date = intent.getIntExtra("DATE", 0)
        name = intent.getStringExtra("NAME").toString()

        loadExercise()

        // 무게, 횟수 버튼 클릭 시
        button_weight_number.setOnClickListener {
            snum = try {
                Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            } catch (nfe: NumberFormatException) {
                1;
            }
            setWeightNumMode(snum, null, null)
        }
        // 횟수 버튼 클릭 시
        button_number.setOnClickListener {
            snum = try {
                Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            } catch (nfe: NumberFormatException) {
                1;
            }
            setNumMode(snum, null)
        }
        // 시간 버튼 클릭 시
        button_time.setOnClickListener {
            snum = try {
                Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            } catch (nfe: NumberFormatException) {
                1;
            }
            setTimeMode(snum, null)
        }
        // 운동 추가 완료
        button_exercise_add_done.setOnClickListener {
            snum = try {
                Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            } catch (nfe: NumberFormatException) {
                1;
            }
            addExercise()
            val intent = Intent(this, ExerciseActivity::class.java)
            intent.putExtra("NAME", exercise_name.text.toString())
            setResult(Activity.RESULT_OK, intent)
            Toast.makeText(this, "완료되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 정리 필요함
    private fun addExercise() {
        sqldb = myDBHelper.writableDatabase

        var weightArray : ArrayList<Float> ?= ArrayList() // 무게 배열
        var numArray : ArrayList<Int> ?= ArrayList() // 횟수 배열
        var timeArray : ArrayList<Int> ?= ArrayList() // 시간 배열

        if(findViewById<EditText>(WEIGHT_ID) != null) { // 입력한 무게 값이 있으면
            for(i in 0 until snum) { // 사용자가 입력한 값 가져오기
                var weight: EditText = findViewById(WEIGHT_ID + i)
                weightArray?.add(weight.text.toString().toFloat())
            }
        } else {
            for(i in 0 until snum) {
                weightArray = null
            }
        }

        if(findViewById<EditText>(NUM_ID) != null) { // 입력한 횟수 값이 있으면
            for(i in 0 until snum) { // 사용자가 입력한 값 가져오기
                var num : EditText = findViewById(NUM_ID + i)
                numArray?.add(Integer.parseInt(num.text.toString()))
            }
        } else {
            for(i in 0 until snum) {
                numArray = null
            }
        }

        if(findViewById<EditText>(HOUR_ID) != null || findViewById<EditText>(MIN_ID) != null || findViewById<EditText>(SEC_ID) != null) { // 입력한 시간 값이 있으면
            for(i in 0 until snum) { // 사용자가 입력한 값 가져오기
                var time : Int = 0
                var hour : EditText = findViewById(HOUR_ID + i)
                var min : EditText = findViewById(MIN_ID + i)
                var sec : EditText = findViewById(SEC_ID + i)
                time = (hour.text.toString().toInt() * 3600) + (min.text.toString().toInt() * 60) + (sec.text.toString().toInt())
                timeArray?.add(time)
            }
        } else {
            for(i in 0 until snum) {
                timeArray = null
            }
        }

        // db에 저장하기
        if(isLoaded) { // 불러와진 데이터면 지우기
            sqldb.execSQL("DELETE FROM exercise_counter WHERE date = $date AND exercise_name = '${name}'")
        }
        for(i in 0 until snum) { //데이터 저장하기
            sqldb.execSQL("INSERT INTO exercise_counter VALUES ($date,'${exercise_name.text}', " +
                    "${i+1}, ${weightArray?.get(i)}, ${numArray?.get(i)}, '${timeArray?.get(i)}', 0);")
        }

        sqldb.close()
    }

    private fun loadExercise() {
        sqldb = myDBHelper.readableDatabase
        var cursor = sqldb.rawQuery("SELECT * FROM exercise_counter WHERE date = $date AND exercise_name = '$name';", null)
        if(cursor.moveToFirst()) { // 저장된 글이 있으면
            isLoaded = true
            set_num.setText(cursor.count.toString())
            exercise_name.setText(name)

            var weight = ArrayList<Float>()
            var num = ArrayList<Int>()
            var time = ArrayList<Int>()

            do{
                weight.add(cursor.getFloat(cursor.getColumnIndex("weight")))
                num.add(cursor.getInt(cursor.getColumnIndex("exercise_count")))
                time.add(cursor.getInt(cursor.getColumnIndex("time")))
            } while (cursor.moveToNext())
            sqldb.close()

            if(time[0] == 0) {
                if(weight[0] == 0f) { // 세트와 횟수만
                    setNumMode(set_num.text.toString().toInt(), num)
                }
                else { // 세트, 횟수, 무게
                    setWeightNumMode(set_num.text.toString().toInt(), weight, num)
                }
            } else { // 세트, 시간
                setTimeMode(set_num.text.toString().toInt(), time)
            }
        } else { // 저장된 글이 없으면
            Toast.makeText(this, "저장된 식단이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setWeightNumMode(set : Int, weight : ArrayList<Float>?, num : ArrayList<Int>?) {
        table_weight_num.visibility = View.VISIBLE
        table_num.visibility = View.GONE
        table_time.visibility = View.GONE

        table_exercise_count.removeAllViews() // 기존에 만들어진 것들 다 없애기

        for (i in 0 until set) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            table_exercise_count.addView(tableRow)
            // 세트 수
            val setTV = TextView(this)
            setTV.textSize = 15f // 글자 크기
            setTV.text = "${i+1}"
            setTV.gravity = 17 // 중앙 정렬
            tableRow.addView(setTV)
            // 무게
            val weightET = EditText(this)
            weightET.id = WEIGHT_ID + i // 아이디 값
            weightET.textSize = 15f // 글자 크기
            weightET.gravity = 17 // 중앙 정렬
            weightET.inputType = 4096 // 실수 입력
            if(weight == null)
                weightET.setText("")
            else weightET.setText(weight?.get(i).toString())
            tableRow.addView(weightET)
            // 횟수
            val numET = EditText(this)
            numET.id = NUM_ID + i // 아이디 값
            numET.textSize = 15f // 글자 크기
            numET.gravity = 17 // 중앙 정렬
            numET.inputType = 2 // 숫자 키패트
            if(num == null) numET.setText("")
            else numET.setText(num?.get(i).toString())
            tableRow.addView(numET)

            table_exercise_count.visibility = View.VISIBLE
        }
    }
    private fun setNumMode(set: Int, num: ArrayList<Int>?) {
        table_weight_num.visibility = View.GONE
        table_num.visibility = View.VISIBLE
        table_time.visibility = View.GONE

        table_exercise_count.removeAllViews() // 기존에 만들어진 것들 다 없애기

        for (i in 0 until set) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            table_exercise_count.addView(tableRow)
            // 세트 수
            val setTV = TextView(this)
            setTV.textSize = 15f // 글자 크기
            setTV.text = "${i+1}"
            setTV.gravity = 17 // 중앙 정렬
            tableRow.addView(setTV)
            // 횟수
            val numET = EditText(this)
            numET.id = NUM_ID + i // 아이디 값
            numET.textSize = 15f // 글자 크기
            numET.gravity = 17 // 중앙 정렬
            numET.inputType = 2 // 숫자 키패트
            if(num == null) numET.setText("")
            else numET.setText(num?.get(i).toString())
            tableRow.addView(numET)

            table_exercise_count.visibility = View.VISIBLE
        }
    }
    private fun setTimeMode(set : Int, time : ArrayList<Int>?) {
        table_weight_num.visibility = View.GONE
        table_num.visibility = View.GONE
        table_time.visibility = View.VISIBLE

        table_exercise_count.removeAllViews() // 기존에 만들어진 것들 다 없애기

        for (i in 0 until set) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            table_exercise_count.addView(tableRow)
            // 세트 수
            val setTV = TextView(this)
            setTV.textSize = 15f // 글자 크기
            setTV.text = "${i+1}"
            setTV.gravity = 17 // 중앙 정렬
            tableRow.addView(setTV)

            val linearLayout = LinearLayout(this)
            linearLayout.gravity = 17 // 중앙 정렬

            val hour = time?.get(i)!! / 3600
            val min = (time?.get(i)!! % 3600) / 60
            val sec = (time?.get(i)!! % 3600) % 60

            val hourET = EditText(this)
            hourET.id = HOUR_ID + i
            hourET.textSize = 15f // 글자 크기
            //hourET.gravity = 17 // 중앙 정렬
            hourET.inputType = 2 // 숫자 키패트
            hourET.setText("$hour")
            linearLayout.addView(hourET)
            val tv1 = TextView(this)
            tv1.textSize = 15f
            tv1.text = ":"
            linearLayout.addView(tv1)

            val minET = EditText(this)
            minET.id = MIN_ID + i
            minET.textSize = 15f // 글자 크기
            //hourET.gravity = 17 // 중앙 정렬
            minET.inputType = 2 // 숫자 키패트
            minET.setText("$min")
            linearLayout.addView(minET)
            val tv2 = TextView(this)
            tv2.textSize = 15f
            tv2.text = ":"
            linearLayout.addView(tv2)

            val secET = EditText(this)
            secET.id = SEC_ID + i
            secET.textSize = 15f // 글자 크기
            //hourET.gravity = 17 // 중앙 정렬
            secET.inputType = 2 // 숫자 키패트
            secET.setText("$sec")
            linearLayout.addView(secET)

            tableRow.addView(linearLayout)

            table_exercise_count.visibility = View.VISIBLE
        }
    }
}