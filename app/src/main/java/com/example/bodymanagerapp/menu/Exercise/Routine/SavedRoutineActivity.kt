package com.example.bodymanagerapp.menu.Exercise.Routine

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.MainActivity
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.SettingsFragment
import com.example.bodymanagerapp.myDBHelper

class SavedRoutineActivity : AppCompatActivity() {
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    lateinit var toolbar: Toolbar

    lateinit var spinner : Spinner
    lateinit var et_routine : EditText
    lateinit var rv : RecyclerView
    lateinit var rvAdapter : SavedRoutineRVAdapter
    lateinit var btn_save : Button

    var nameList = ArrayList<String>()
    var routineData = ArrayList<RoutineData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_routine)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        myDBHelper = myDBHelper(this)

        spinner = findViewById(R.id.spinner_routine_save)
        et_routine = findViewById(R.id.et_routine_name)
        rv = findViewById(R.id.recycler_routine_save)
        btn_save = findViewById(R.id.btn_routine_save)

        loadRoutineName() // 루틴 이름 불러오기
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nameList)
        spinner.adapter = spinnerAdapter

        if(nameList[spinner.selectedItemPosition] == "직접 입력") {
            et_routine.visibility = View.VISIBLE
        }

        btn_save.setOnClickListener {
            saveRoutine()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.menu_settings -> {
                MainActivity().replaceFragment(SettingsFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadRoutineName() {
        nameList.clear()
        nameList.add("직접 입력")
        sqldb = myDBHelper.readableDatabase
        var cursor = sqldb.rawQuery("SELECT DISTINCT routine_name FROM routine_info ORDER BY routine_name ASC;", null)

        if(cursor.moveToFirst()) {
            do{
                nameList.add(cursor.getString(cursor.getColumnIndex("routine_name")))
            } while(cursor.moveToNext())
        }

    }

    private fun saveRoutine() {

    }
}