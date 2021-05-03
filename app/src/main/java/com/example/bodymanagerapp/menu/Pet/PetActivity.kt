package com.example.bodymanagerapp.menu.Pet

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.bodymanagerapp.Preference.MyPreference
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Body.BodyActivity
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.example.bodymanagerapp.menu.Stats.StatsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class PetActivity : AppCompatActivity() {
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

    var point : Int = MyPreference.prefs.getInt("point", 0) // 포인트 값 가져오기
    var meal : Int = MyPreference.prefs.getInt("meal", 0) // 식사 값
    var health : Int = MyPreference.prefs.getInt("health", 0) // 건강 값

    // 펫 랜덤 이동
    var handler : Handler = Handler()
    var runnable : Runnable = Runnable {  }
    var random = Random()
    var ranX = 0
    var ranY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

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

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)

        tv_point.text = point.toString()
        tv_meal.text = "${meal}%"
        tv_health.text = "${health}%"

        imageMove(img_pet, 600f, 600f, 5000L)
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

    // 이미지 이동
    private fun imageMove(image : ImageView, posX : Float, posY : Float, duration1 : Long) {
        runnable = object : Runnable {
            override fun run() {
                ObjectAnimator.ofFloat(image, "translationX", posX).apply {
                    duration = duration1
                    start()
                }
                ObjectAnimator.ofFloat(image, "translationY", posY).apply {
                    duration = duration1
                    start()
                }
                handler.postDelayed(runnable, duration1)
            }
        }
        handler.post(runnable)
    }
}