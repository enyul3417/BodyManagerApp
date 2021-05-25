package com.example.bodymanagerapp.menu.Exercise


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.myDBHelper
import com.google.firebase.database.*
import com.mancj.materialsearchbar.MaterialSearchBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.round


class ExerciseAdditionActivity : AppCompatActivity() {
    // id 값에 사용
    //private val SET_ID : Int = 100 // 세트
    private val NUM_ID: Int = 200 // 횟수
    private val WEIGHT_ID: Int = 300 // 무게
    //private val TIME_ID: Int = 400 // 시간
    private val HOUR_ID : Int = 500
    private val MIN_ID : Int = 600
    private val SEC_ID : Int = 700

    lateinit var toolbar: Toolbar

    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    lateinit var searchBar : MaterialSearchBar
    lateinit var list_view : ListView
    lateinit var exercise_name: EditText // 운동 이름
    lateinit var set_num: EditText // 세트 수

    // 운동 부위 체크 박스
    lateinit var cb_chest : CheckBox
    lateinit var cb_shoulder : CheckBox
    lateinit var cb_back : CheckBox
    lateinit var cb_abs : CheckBox
    lateinit var cb_arms : CheckBox
    lateinit var cb_lower_body : CheckBox
    lateinit var cb_hip : CheckBox
    lateinit var cb_whole_body : CheckBox
    lateinit var cb_aerobic : CheckBox

    lateinit var button_weight_number: Button // 무게, 횟수
    lateinit var button_number: Button // 횟수
    lateinit var button_time: Button // 시간

    lateinit var table_weight_num: TableLayout // 무게, 횟수 테이블
    lateinit var table_num: TableLayout // 횟수 테이블
    lateinit var table_time: TableLayout // 시간 테이블
    lateinit var table_exercise_count: TableLayout // 각각 값 입력 받는 곳

    lateinit var button_exercise_add_done: Button // 운동 추가 버튼

    private var nameList = ArrayList<String>()
    private var partsList = ArrayList<String>()
    private var snum: Int = 0 // 세트 수
    private var isLoaded : Boolean = false
    private var date: Int = 0 // 현재 날짜
    private var name: String = "" // 운동 이름
    private var lastData = ArrayList<ExerciseData>()
    private var mode : Int = 0 // 무게+횟수 = 1, 횟수 = 2, 시간 = 3

    // 파이어베이스
    //private lateinit var database : DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_addition)

        toolbar = findViewById(R.id.toolbar)
        myDBHelper = myDBHelper(this)

        searchBar = findViewById(R.id.search_exercise)
        list_view = findViewById(R.id.lv_exercise)
        exercise_name = findViewById(R.id.exercise_name)
        set_num = findViewById(R.id.set_num)

        cb_chest = findViewById(R.id.cb_chest)
        cb_shoulder = findViewById(R.id.cb_shoulder)
        cb_back = findViewById(R.id.cb_back)
        cb_abs = findViewById(R.id.cb_abs)
        cb_arms = findViewById(R.id.cb_arms)
        cb_lower_body = findViewById(R.id.cb_lower_body)
        cb_hip = findViewById(R.id.cb_hip)
        cb_whole_body = findViewById(R.id.cb_whole_body)
        cb_aerobic = findViewById(R.id.cb_aerobic)

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

        readExercise()
        loadExercise()

        searchBar.setHint("운동 이름 검색")
        //searchBar.setTextColor(Color.BLACK)
        val listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameList)
        list_view.adapter = listAdapter
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {

            }

            // 검색창 클릭 여부부
            override fun onSearchStateChanged(enabled: Boolean) {
                if(enabled) list_view.visibility = View.VISIBLE
                else list_view.visibility = View.GONE
            }

            override fun onSearchConfirmed(text: CharSequence?) {

            }

        })
        searchBar.addTextChangeListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            // 검색어에 따라 ListView 내용 변경
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listAdapter.filter.filter(s)
            }

        })
        list_view.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                searchBar.disableSearch()
                Log.d("검색", listAdapter.getItem(position)!!.toString())
                Log.d("검색", "$position")
                exercise_name.setText("${listAdapter.getItem(position)!!.toString()}")

                setParts(partsList[position])
            }

        }

        // 무게, 횟수 버튼 클릭 시
        button_weight_number.setOnClickListener {
            snum = try {
                Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            } catch (nfe: NumberFormatException) {
                1;
            }
            mode = 1
            setWeightNumMode(snum, null, null)
        }
        // 횟수 버튼 클릭 시
        button_number.setOnClickListener {
            snum = try {
                Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            } catch (nfe: NumberFormatException) {
                1;
            }
            mode = 2
            setNumMode(snum, null)
        }
        // 시간 버튼 클릭 시
        button_time.setOnClickListener {
            snum = try {
                Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
            } catch (nfe: NumberFormatException) {
                1;
            }
            mode = 3
            setTimeMode(snum, null)
        }
        // 운동 추가 완료
        button_exercise_add_done.setOnClickListener {
            if (mode == 0) {
                Toast.makeText(this, "내용을 작성해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                snum = try {
                    Integer.parseInt(set_num.text.toString()) // 입력 없을시 에러 발생, 익셉션 처리 필요
                } catch (nfe: NumberFormatException) {
                    1;
                }

                // 정리 필요
                checkLastData()
                if(lastData.size > 0) {
                    when(mode) {
                        1 -> { // 무게, 횟수
                            var weight1 = lastData[0].weight!![0]
                            var rm1  = weight1 + (weight1 * lastData[0].num!![0] * 0.025f)
                            var weight2 = findViewById<EditText>(WEIGHT_ID).text.toString().toFloat()
                            var count = findViewById<EditText>(NUM_ID).text.toString().toInt()
                            var rm2 = weight2 + (weight2 * count * 0.025f)

                            if(rm1 > rm2) {
                                var dig = AlertDialog.Builder(this) // 대화상자
                                dig.setTitle("1RM 감소") // 제목
                                dig.setMessage("마지막 기록보다 1RM이 ${round((rm1 - rm2) * 10) / 10}kg 감소했습니다. 진행하시겠습니까?")
                                dig.setPositiveButton("예") { dialog, which ->
                                    addExercise()
                                    val intent = Intent(this, ExerciseActivity::class.java)
                                    intent.putExtra("NAME", exercise_name.text.toString())
                                    setResult(Activity.RESULT_OK, intent)
                                    Toast.makeText(this, "완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                dig.setNegativeButton("아니오", null)
                                dig.show()
                            }

                            var vol1 = 0f
                            var vol2 = 0f
                            for(i in 0 until lastData[0].weight!!.size) {
                                vol1 += lastData[0].weight!![i] * lastData[0].num!![i]
                            }
                            for(i in 0 until set_num.text.toString().toInt()) {
                                vol2 += findViewById<EditText>(WEIGHT_ID + i).text.toString().toFloat() * findViewById<EditText>(NUM_ID + i).text.toString().toInt()
                            }
                            if(vol1 > vol2) {
                                var dig = AlertDialog.Builder(this) // 대화상자
                                dig.setTitle("볼륨 감소") // 제목
                                dig.setMessage("마지막 기록보다 볼륨이 ${round((vol1 - vol2) * 10) / 10}kg 감소했습니다. 진행하시겠습니까?")
                                dig.setPositiveButton("예") { dialog, which ->
                                    addExercise()
                                    val intent = Intent(this, ExerciseActivity::class.java)
                                    intent.putExtra("NAME", exercise_name.text.toString())
                                    setResult(Activity.RESULT_OK, intent)
                                    Toast.makeText(this, "완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                dig.setNegativeButton("아니오", null)
                                dig.show()
                            }
                        }
                        2 -> { // 횟수
                            var vol1 = 0f
                            var vol2 = 0f
                            for(i in 0 until lastData[0].num!!.size) {
                                vol1 += lastData[0].num!![i]
                            }
                            for(i in 0 until set_num.text.toString().toInt()) {
                                vol2 += findViewById<EditText>(NUM_ID + i).text.toString().toInt()
                            }
                            if(vol1 > vol2) {
                                var dig = AlertDialog.Builder(this) // 대화상자
                                dig.setTitle("볼륨 감소") // 제목
                                dig.setMessage("마지막 기록보다 볼륨이 ${round((vol1 - vol2) * 10) / 10}회 감소했습니다. 진행하시겠습니까?")
                                dig.setPositiveButton("예") { dialog, which ->
                                    addExercise()
                                    val intent = Intent(this, ExerciseActivity::class.java)
                                    intent.putExtra("NAME", exercise_name.text.toString())
                                    setResult(Activity.RESULT_OK, intent)
                                    Toast.makeText(this, "완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                dig.setNegativeButton("아니오", null)
                                dig.show()
                            }

                        }
                        3 -> {
                            var time1 = 0
                            var time2 = 0
                            for(i in 0 until lastData[0].time!!.size) {
                                time1 += lastData[0].time!![i]
                            }
                            for(i in 0 until set_num.text.toString().toInt()) {
                                time2 += (findViewById<EditText>(HOUR_ID + i).text.toString().toInt() * 3600)
                                + (findViewById<EditText>(MIN_ID + i).text.toString().toInt() * 60)
                                + findViewById<EditText>(SEC_ID + i).text.toString().toInt()
                            }
                            if(time1 > time2) {
                                var time = time1 - time2
                                var dig = AlertDialog.Builder(this) // 대화상자
                                dig.setTitle("시간 감소") // 제목
                                dig.setMessage("마지막 기록보다 시간이 ${time / 3600}시 ${time / 3600 % 60}분 ${time % 3600 % 60}초 감소했습니다. 진행하시겠습니까?")
                                dig.setPositiveButton("예") { dialog, which ->
                                    addExercise()
                                    val intent = Intent(this, ExerciseActivity::class.java)
                                    intent.putExtra("NAME", exercise_name.text.toString())
                                    setResult(Activity.RESULT_OK, intent)
                                    Toast.makeText(this, "완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                dig.setNegativeButton("아니오", null)
                                dig.show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun readExercise() {
        var sortByName = FirebaseDatabase.getInstance().reference.child("exercise").orderByChild("exerciseName")
        sortByName.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nameList.clear()
                partsList.clear()
                for (snapshot in dataSnapshot.children) {
                    val get: ExerciseDB? = snapshot.getValue(ExerciseDB::class.java)
                    nameList.add(get!!.exerciseName)
                    partsList.add(get!!.parts)
                }
                /*arrayAdapter.clear()
                arrayAdapter.addAll(arrayData)
                arrayAdapter.notifyDataSetChanged()*/

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    // 정리 필요함
    private fun addExercise() {
        sqldb = myDBHelper.writableDatabase

        var weightArray : ArrayList<Float> ?= ArrayList() // 무게 배열
        var numArray : ArrayList<Int> ?= ArrayList() // 횟수 배열
        var timeArray : ArrayList<Int> ?= ArrayList() // 시간 배열
        var str : String = ""

        if (cb_chest.isChecked) {
            str += "${cb_chest.text},"
        }
        if (cb_shoulder.isChecked) {
            str += "${cb_shoulder.text},"
        }
        if (cb_back.isChecked) {
            str += "${cb_back.text},"
        }
        if (cb_abs.isChecked) {
            str += "${cb_abs.text},"
        }
        if (cb_arms.isChecked) {
            str += "${cb_arms.text},"
        }
        if (cb_lower_body.isChecked) {
            str += "${cb_lower_body.text},"
        }
        if (cb_hip.isChecked) {
            str += "${cb_hip.text},"
        }
        if (cb_whole_body.isChecked) {
            str += "${cb_whole_body.text},"
        }
        if (cb_aerobic.isChecked) {
            str += "${cb_aerobic.text},"
        }

        /*when(mode) {
            1 -> {

            }
            2 -> {

            }
            3 -> {

            }
        }*/
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
            sqldb.execSQL("INSERT INTO exercise_counter VALUES ($date,'${exercise_name.text}', '$str', " +
                    "${i + 1}, ${weightArray?.get(i)}, ${numArray?.get(i)}, '${timeArray?.get(i)}', 0);")
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

            var str : String = cursor.getString(cursor.getColumnIndex("tag"))
            setParts(str)

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
                    mode = 2
                    setNumMode(set_num.text.toString().toInt(), num)
                }
                else { // 세트, 횟수, 무게
                    mode = 1
                    setWeightNumMode(set_num.text.toString().toInt(), weight, num)
                }
            } else { // 세트, 시간
                mode = 3
                setTimeMode(set_num.text.toString().toInt(), time)
            }
        }
    }

    private fun setWeightNumMode(set: Int, weight: ArrayList<Float>?, num: ArrayList<Int>?) {
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
    private fun setTimeMode(set: Int, time: ArrayList<Int>?) {
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

    private fun setParts(str : String) {
        cb_chest.isChecked = false
        cb_shoulder.isChecked = false
        cb_back.isChecked = false
        cb_abs.isChecked = false
        cb_arms.isChecked = false
        cb_lower_body.isChecked = false
        cb_hip.isChecked = false
        cb_whole_body.isChecked = false
        cb_aerobic.isChecked = false

        var part : String = ""
        for(i in str.indices) {
            if(str[i] == ',') {
                when (part) {
                    "${cb_chest.text}" -> {
                        cb_chest.isChecked = true
                    }
                    "${cb_shoulder.text}" -> {
                        cb_shoulder.isChecked = true
                    }
                    "${cb_back.text}" -> {
                        cb_back.isChecked = true
                    }
                    "${cb_abs.text}" -> {
                        cb_abs.isChecked = true
                    }
                    "${cb_arms.text}" -> {
                        cb_arms.isChecked = true
                    }
                    "${cb_lower_body.text}" -> {
                        cb_lower_body.isChecked = true
                    }
                    "${cb_hip.text}" -> {
                        cb_hip.isChecked = true
                    }
                    "${cb_whole_body.text}" -> {
                        cb_whole_body.isChecked = true
                    }
                    "${cb_aerobic.text}" -> {
                        cb_aerobic.isChecked = true
                    }
                }
                part = ""
            } else {
                part += str[i]
            }
        }
    }

    private fun checkLastData() {
        sqldb = myDBHelper.readableDatabase
        lastData.clear()
        var weightList = ArrayList<Float>()
        var countList = ArrayList<Int>()
        var timeList = ArrayList<Int>()
        var date = 0

        val dateCursor : Cursor = sqldb.rawQuery("SELECT date " +
                "FROM exercise_counter " +
                "WHERE exercise_name = '${exercise_name.text}' " +
                "ORDER BY date DESC LIMIT 1;", null)
        if(dateCursor.moveToFirst()) { // 해당 데이터를 가지고 있으면
            date = dateCursor.getInt(dateCursor.getColumnIndex("date"))

            val cursor : Cursor = sqldb.rawQuery("SELECT weight, exercise_count, time " +
                    "FROM exercise_counter " +
                    "WHERE exercise_name = '${exercise_name.text}' AND date = $date;", null)

            cursor.moveToFirst()
            do {
                weightList.add(cursor.getFloat(cursor.getColumnIndex("weight")))
                countList.add(cursor.getInt(cursor.getColumnIndex("exercise_count")))
                timeList.add(cursor.getInt(cursor.getColumnIndex("time")))
            } while (cursor.moveToNext())
            lastData.add(ExerciseData(countList, weightList, timeList))
        }
    }
}