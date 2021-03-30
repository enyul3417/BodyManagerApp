package com.example.bodymanagerapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.menu.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    // BottomNavigationView
    lateinit var bottom_nav_view : BottomNavigationView

    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb : SQLiteDatabase

    // 하단 메뉴 선택 시 작동
    private val bottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            // 운동 메뉴 선택 시
            R.id.navigation_exercise -> {
                replaceFragment(ExerciseFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 식단 메뉴 선택 시
            R.id.navigation_diet -> {
                replaceFragment(DietFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 신체 메뉴 선택 시
            R.id.navigation_body -> {
                replaceFragment(BodyFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 상태 메뉴 선택 시
            R.id.navigation_stats -> {
                replaceFragment(StatsFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 환경설정 선택 시
            R.id.navigation_settings -> {
                replaceFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 그 외
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDBHelper = myDBHelper(this)
        sqldb = myDBHelper.writableDatabase

        bottom_nav_view = findViewById(R.id.bottom_nav_view)

        replaceFragment(ExerciseFragment())
        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
    }

    // 하단 메뉴 선택 시 fragment 변경 기능
    fun replaceFragment(fragment : Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_layout, fragment)
        fragmentTransaction.commit()
    }
}