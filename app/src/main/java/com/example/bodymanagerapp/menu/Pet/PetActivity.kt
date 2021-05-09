package com.example.bodymanagerapp.menu.Pet

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.bodymanagerapp.Preference.MyPreference
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Body.BodyActivity
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.example.bodymanagerapp.menu.Stats.StatsActivity
import com.example.bodymanagerapp.myDBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class PetActivity : AppCompatActivity() {
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    // 상하단
    lateinit var bottom_nav_view: BottomNavigationView
    lateinit var toolbar: Toolbar

    lateinit var tv_point : TextView
    lateinit var tv_meal : TextView
    lateinit var tv_health : TextView
    lateinit var btn_feeding : Button
    lateinit var btn_exercise : Button
    lateinit var btn_snack : Button
    lateinit var img_pet : ImageView
    lateinit var gridLayout: GridLayout

    // 펫 수치 관련
    var point : Int = 0
    var meal : Int = 0
    var health : Int = 0
    /*lateinit var lastTime : Date // 마지막 접속 시간
    lateinit var nowTime : Date // 현재 접속 시간*/
    var lastTime : Long = 0L
    var nowTime : Long = 0L

    // 펫 이동
    var timerTask : Timer? = null
    var time = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

        myDBHelper = myDBHelper(this)

        //메뉴
        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        tv_point = findViewById(R.id.tv_pet_point)
        tv_meal = findViewById(R.id.tv_pet_meal)
        tv_health = findViewById(R.id.tv_pet_health)
        btn_feeding = findViewById(R.id.btn_pet_feeding)
        btn_exercise = findViewById(R.id.btn_pet_exercise)
        btn_snack = findViewById(R.id.btn_pet_snack)
        img_pet = findViewById(R.id.img_pet)
        gridLayout = findViewById(R.id.gridLayout)

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)

        loadData()



        tv_point.text = point.toString()
        tv_meal.text = "${meal}%"
        tv_health.text = "${health}%"

        imageMove(gridLayout, img_pet)
        //MyPreference.prefs.setInt("point", 200)

        // 밥주기 버튼
        btn_feeding.setOnClickListener {
            if(point >= 50) {
                // 포인트 감소
                point -= 50
                MyPreference.prefs.setInt("point", point)
                tv_point.text = point.toString()

                // 포만감 증가
                if (meal + 20 > 100) meal = 100
                else meal += 20
                MyPreference.prefs.setInt("meal", meal)
                tv_meal.text = "${meal}%"

                // 건강 감소
                if (health - 5 < 0) health = 0
                else health -= 5
                MyPreference.prefs.setInt("health", health)
                tv_health.text = "${health}%"
            } else {
                Toast.makeText(this, "포인트가 부족합니다.", Toast.LENGTH_SHORT).show()
            }

        }
        // 운동하기 버튼
        btn_exercise.setOnClickListener {
            if(point >= 30) {
                // 포인트 감소
                point -= 30
                MyPreference.prefs.setInt("point", point)
                tv_point.text = point.toString()

                // 포만감 감소
                if (meal - 10 < 0) meal = 0
                else meal -= 10
                MyPreference.prefs.setInt("meal", meal)
                tv_meal.text = "${meal}%"

                // 건강 증가
                if (health + 20 > 100) health = 100
                else health += 20
                MyPreference.prefs.setInt("health", health)
                tv_health.text = "${health}%"
            } else {
                Toast.makeText(this, "포인트가 부족합니다.", Toast.LENGTH_SHORT).show()
            }

        }
        // 간식주기 버튼
        btn_snack.setOnClickListener {
            if(point >= 10) {
                // 포인트 감소
                point -= 10
                MyPreference.prefs.setInt("point", point)
                tv_point.text = point.toString()

                // 포만감 증가
                if (meal + 5 > 100) meal = 100
                else meal += 5
                MyPreference.prefs.setInt("meal", meal)
                tv_meal.text = "${meal}%"

                // 건강 감소
                if (health - 10 < 0) health = 0
                else health -= 10
                MyPreference.prefs.setInt("health", health)
                tv_health.text = "${health}%"
            } else {
                Toast.makeText(this, "포인트가 부족합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 하단 메뉴 선택 시 작동
    private val bottomNavItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    // 운동 메뉴 선택 시
                    R.id.navigation_exercise -> {
                        var intent: Intent = Intent(this, ExerciseActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 식단 메뉴 선택 시
                    R.id.navigation_diet -> {
                        var intent: Intent = Intent(this, DietActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 신체 메뉴 선택 시
                    R.id.navigation_body -> {
                        var intent: Intent = Intent(this, BodyActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 통계 메뉴 선택 시
                    R.id.navigation_stats -> {
                        var intent : Intent = Intent(this, StatsActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 펫 선택 시
                    R.id.navigation_pet -> {
                        var intent : Intent = Intent(this, PetActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@OnNavigationItemSelectedListener true
                    }

                    // 그 외
                    else -> false
                }
            }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    // 상단 메뉴
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                //replaceFragment(SettingsFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 화면 벗어남 수정 필요
    // 펫 이미지 이동
    private fun imageMove(view : View, img : ImageView) {
        val rand = Random()
        var numX = 0
        var numY = 0

        Log.d("좌표 뷰", "(${view.width}, ${view.height})")

        timerTask = timer(period=1000) {
            time++

            if(time == 10) {
                numX = rand.nextInt(view.width)
                numY = rand.nextInt(view.height)
                Log.d("좌표 값", "($numX, $numY)")

                img
                        .animate()
                        .translationX(numX.toFloat())
                        .translationY(numY.toFloat())
                        .setDuration(4000)

                time = 0
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadData() {
        nowTime = System.currentTimeMillis() // 현재 접속 시간
        lastTime = MyPreference.prefs.getLong("last", nowTime) // 마지막 접속 시간
        MyPreference.prefs.setLong("last", nowTime) // 현재 접속 시간을 마지막 접속 시간으로 변경
        var subTime = (nowTime - lastTime).toInt() / (1000 * 60) // 시간 차이를 분 단위로 변경

        Log.d("시간", "$nowTime || $lastTime || $subTime")

        point = MyPreference.prefs.getInt("point", 0) // 포인트 값 가져오기
        meal = MyPreference.prefs.getInt("meal", 50) // 식사 값
        health = MyPreference.prefs.getInt("health", 50) // 건강 값

        meal -= (subTime / 20) * 3 // 20분 당 3% 감소
        health -= (subTime / 10) * 1 // 10분 당 1% 감소

        // 변경된 값 저장해두기
        MyPreference.prefs.setInt("meal", meal)
        MyPreference.prefs.setInt("health", health)

        // 사용자의 최신 BMI 값 가져와서 펫 이미지 설정
        sqldb = myDBHelper.readableDatabase
        var cursor : Cursor = sqldb.rawQuery("SELECT bmi FROM body_record ORDER BY date DESC", null)
        if(cursor.moveToFirst()) { // 데이터가 있으면
            var bmi = cursor.getFloat(cursor.getColumnIndex("bmi"))
            Log.d("BMI", "$bmi")
            when {
                bmi >= 25 -> img_pet.setImageResource(R.drawable.pigcat) // 비만 이상은 뚱냥이
                bmi >= 23 -> img_pet.setImageResource(R.drawable.chubbycat) // 과체중은 포동냥이
                else -> img_pet.setImageResource(R.drawable.cat) // 그 외는 그냥 냥이
            }
        } else { // 데이터가 없으면
            img_pet.setImageResource(R.drawable.pigcat) // 뚱냥이
        }

        // 7일 이상 접속 기록이 없으면 뚱냥이 넣기
        if(subTime / (60 * 24) >= 7) img_pet.setImageResource(R.drawable.pigcat)
    }
}