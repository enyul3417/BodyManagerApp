package com.example.bodymanagerapp.menu.Stats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Body.BodyActivity
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.example.bodymanagerapp.menu.Pet.PetActivity
import com.example.bodymanagerapp.menu.Settings.SettingsActivity
import com.example.bodymanagerapp.menu.Stats.Body.BodyStatsFragment
import com.example.bodymanagerapp.menu.Stats.Exercise.ExerciseStatsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class StatsActivity : AppCompatActivity() {
    // BottomNavigationView
    lateinit var bottom_nav_view : BottomNavigationView
    lateinit var toolbar: Toolbar

    lateinit var btn_exercise : Button
    lateinit var btn_body : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        btn_exercise = findViewById(R.id.button_exercise_stats)
        btn_body = findViewById(R.id.button_body_stats)

        replaceFragment(ExerciseStatsFragment())
        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        bottom_nav_view.menu.findItem(R.id.navigation_stats).isChecked = true
        setSupportActionBar(toolbar)

        btn_exercise.isSelected = true

        btn_exercise.setOnClickListener {
            btn_exercise.isSelected = true
            btn_body.isSelected = false

            replaceFragment(ExerciseStatsFragment())
        }

        btn_body.setOnClickListener {
            btn_exercise.isSelected = false
            btn_body.isSelected = true
            replaceFragment(BodyStatsFragment())
        }
    }

    // ?????? ?????? ?????? ??? ??????
    private val bottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            // ?????? ?????? ?????? ???
            R.id.navigation_exercise -> {
                var intent : Intent = Intent(this, ExerciseActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }

            // ?????? ?????? ?????? ???
            R.id.navigation_diet -> {
                var intent : Intent = Intent(this, DietActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }

            // ?????? ?????? ?????? ???
            R.id.navigation_body -> {
                var intent : Intent = Intent(this, BodyActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }

            // ?????? ?????? ?????? ???
            R.id.navigation_stats -> {
                var intent : Intent = Intent(this, StatsActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }

            // ??? ?????? ???
            R.id.navigation_pet -> {
                var intent : Intent = Intent(this, PetActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }

            // ??? ???
            else -> false
        }
    }

    // ?????? ?????? ?????? ??? fragment ?????? ??????
    fun replaceFragment(fragment : Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_stats, fragment)
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
                var intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}