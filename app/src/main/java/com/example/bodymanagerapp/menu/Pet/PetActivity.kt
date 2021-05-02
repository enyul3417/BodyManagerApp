package com.example.bodymanagerapp.menu.Pet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Body.BodyActivity
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.example.bodymanagerapp.menu.Stats.StatsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PetActivity : AppCompatActivity() {
    // 상하단
    lateinit var bottom_nav_view: BottomNavigationView
    lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

        //메뉴
        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)
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
}