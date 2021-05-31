package com.example.bodymanagerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.menu.*
import com.example.bodymanagerapp.menu.Exercise.ExerciseFragment
import com.example.bodymanagerapp.menu.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    // BottomNavigationView
    lateinit var bottom_nav_view : BottomNavigationView
    lateinit var toolbar: Toolbar

    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDBHelper = myDBHelper(this)
        sqldb = myDBHelper.writableDatabase

        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        replaceFragment(ExerciseFragment())
        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)
    }

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
                //replaceFragment(DietFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 신체 메뉴 선택 시
            R.id.navigation_body -> {
                //replaceFragment(BodyFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 상태 메뉴 선택 시
            R.id.navigation_stats -> {
                //replaceFragment(StatsFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 펫 선택 시
            R.id.navigation_pet -> {
                //replaceFragment(PetFragment())
                return@OnNavigationItemSelectedListener true
            }

            // 그 외
            else -> false
        }
    }

    // 하단 메뉴 선택 시 fragment 변경 기능
    fun replaceFragment(fragment : Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.menu_settings -> {
                //replaceFragment(SettingsFragment())
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //var fragment : Fragment= supportFragmentManager.findFragmentById(R.id.exercise_fragment) as Fragment
        var fragment : Fragment= supportFragmentManager.findFragmentByTag("exercise_fragment") as Fragment
        var fragment1 = ExerciseFragment
        fragment.onActivityResult(requestCode, resultCode, data)
        Log.d("데이터 전달", "$requestCode, $resultCode, $data")
    }
}