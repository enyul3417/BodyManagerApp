package com.example.bodymanagerapp.menu.Diet

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.SettingsFragment
import com.example.bodymanagerapp.myDBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class DietActivity : AppCompatActivity() {

    // BottomNavigationView
    lateinit var bottom_nav_view: BottomNavigationView
    lateinit var toolbar: Toolbar

    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor : Cursor
    var data = ArrayList<DietData>()

    // View
    lateinit var rv : RecyclerView
    lateinit var rvAdapter: DietRecyclerViewAdapter

    //식단 관련 변수
    lateinit var text_date: TextView // 날짜
    lateinit var button_diet_update: Button // 수정 버튼
    lateinit var button_diet_delete: Button // 삭제 버튼

    lateinit var button_diet_add: Button // 식단 추가 버튼
    var date : String = ""

    lateinit var diet_layout: LinearLayout // 식단이 추가되는 부분
    lateinit var inflater: LayoutInflater

    var currenturi: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        myDBHelper = myDBHelper(this)
        rv = findViewById(R.id.recycler_diet)

        text_date = findViewById(R.id.date_text) // 날짜
        button_diet_add = findViewById(R.id.button_diet_add) // 식단 추가 버튼
        diet_layout = findViewById(R.id.diet_layout)

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)

        // 날짜 텍스트 클릭 시
        text_date.setOnClickListener {
            val cal = Calendar.getInstance()
            data.clear()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                date = "${y}년 ${m + 1}월 ${d}일"
                text_date.text = date
                data.addAll(loadDiet())
                rvAdapter = DietRecyclerViewAdapter(data, this, rv) {
                    data, num ->
                    var intent = Intent(this, NewDietActivity::class.java)
                    intent.putExtra("ID", data.id)
                    startActivity(intent)
                }
                rv.adapter = rvAdapter
                rv.layoutManager = LinearLayoutManager(this)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        // 식단 추가 버튼 클릭 시
        button_diet_add.setOnClickListener {
            if(date == "") {
                Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                var intent : Intent = Intent(this, NewDietActivity::class.java)
                intent.putExtra("DATE", date)
                startActivity(intent)
                //rvAdapter.notifyDataSetChanged()
            }
        }
    }

    // 하단 메뉴 선택 시 작동
    private val bottomNavItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                // 운동 메뉴 선택 시
                R.id.navigation_exercise -> {
                    return@OnNavigationItemSelectedListener true
                }

                // 식단 메뉴 선택 시
                R.id.navigation_diet -> {
                    var intent: Intent = Intent(this, DietActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }

                // 신체 메뉴 선택 시
                R.id.navigation_body -> {
                    return@OnNavigationItemSelectedListener true
                }

                // 상태 메뉴 선택 시
                R.id.navigation_stats -> {
                    return@OnNavigationItemSelectedListener true
                }

                // 펫 선택 시
                R.id.navigation_pet -> {
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
                replaceFragment(SettingsFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 프레그먼트 변경
    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_layout, fragment)
        fragmentTransaction.commit()
    }

    // 삭제
    private fun deleteDiet(id : Int) {
        sqldb = myDBHelper.writableDatabase
        sqldb.execSQL("DELETE FROM diet_record WHERE id = $id")
        sqldb.close()
    }


    // 불러오기
    private fun loadDiet() : ArrayList<DietData> {
        var dietData = ArrayList<DietData>()
        sqldb = myDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diet_record WHERE date = '${date}'", null)

        if(cursor.moveToFirst()) { // 저장된 글이 있으면
            var id : Int = 0
            var time : String = ""
            var bitmap : Bitmap ?= null
            var memo : String = ""

            do{
                try {
                    id = cursor.getInt(cursor.getColumnIndex("id"))
                    time = cursor.getString(cursor.getColumnIndex("time"))
                    memo = cursor.getString(cursor.getColumnIndex("memo"))
                    val image : ByteArray ?= cursor.getBlob(cursor.getColumnIndex("diet_photo"))
                    bitmap = BitmapFactory.decodeByteArray(image, 0, image!!.size)
                }
                catch (rte: RuntimeException) { // null 값이 있을 경우 exception
                    bitmap = null
                }
                if (bitmap != null ) { // 등록한 이미지가 있다면
                    dietData.add (DietData( id, date, time, bitmap, memo))
                } else { // 등록한 이미지가 없다면
                    dietData.add (DietData( id, date, time, null, memo))
                }
            } while (cursor.moveToNext())
            sqldb.close()
        } else { // 저장된 글이 없으면
            Toast.makeText(this, "저장된 식단이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return dietData
    }

    private fun refresh() { // 새로 고침
        var intent = Intent(this, this::class.java)
        //intent.putExtra("date", date)
        finish()
        startActivity(intent)

        //var gintent : Intent = getIntent()
        //date = gintent.getStringExtra("date").toString()
    }
}
