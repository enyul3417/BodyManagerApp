package com.example.bodymanagerapp.menu.Diet

import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Body.BodyActivity
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.example.bodymanagerapp.menu.Pet.PetActivity
import com.example.bodymanagerapp.menu.Stats.StatsActivity
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.menu.Settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DietActivity : AppCompatActivity() {
    private val REQUEST_ADD_DIET_CODE = 100

    // BottomNavigationView
    lateinit var bottom_nav_view: BottomNavigationView
    lateinit var toolbar: Toolbar

    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb: SQLiteDatabase
    lateinit var cursor : Cursor

    // View
    lateinit var rv : RecyclerView
    lateinit var rvAdapter: DietRecyclerViewAdapter
    var data = ArrayList<DietData>()

    //식단 관련 변수
    lateinit var text_date: TextView // 날짜
    lateinit var button_diet_add: Button // 식단 추가 버튼
    var date : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        MyDBHelper = MyDBHelper(this)
        rv = findViewById(R.id.recycler_diet)

        text_date = findViewById(R.id.text_diet_date) // 날짜
        button_diet_add = findViewById(R.id.button_diet_add) // 식단 추가 버튼

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        bottom_nav_view.menu.findItem(R.id.navigation_diet).isChecked = true
        setSupportActionBar(toolbar)

        var now = LocalDate.now()
        var today = now.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
        var dateformat = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        text_date.text = today.toString()
        date = dateformat.toInt()
        data.clear()
        data.addAll(loadDiet())
        rvAdapter = DietRecyclerViewAdapter(data, this, rv) /*{
                data, num ->
            var intent = Intent(this, NewDietActivity::class.java)
            intent.putExtra("ID", data.id)
            startActivity(intent)
        }*/
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)
        rv.visibility = View.VISIBLE

        // 날짜 텍스트 클릭 시
        text_date.setOnClickListener {
            val cal = Calendar.getInstance()
            data.clear()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                var ymd = "$y"
                ymd += if(m + 1 < 10)
                    "0${m+1}"
                else "${m+1}"
                ymd += if(d < 10)
                    "0$d"
                else "$d"
                date = ymd.toInt()
                text_date.text = "${y}년 ${m + 1}월 ${d}일"
                // 해당 날짜에 저장된 식단들 불러오기
                data.addAll(loadDiet())
                rvAdapter = DietRecyclerViewAdapter(data, this, rv) /*{
                    data, num ->
                    var intent = Intent(this, NewDietActivity::class.java)
                    intent.putExtra("ID", data.id)
                    startActivity(intent)
                }*/
                rv.adapter = rvAdapter
                rv.layoutManager = LinearLayoutManager(this)
                rv.visibility = View.VISIBLE
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }

        // 식단 추가 버튼 클릭 시
        button_diet_add.setOnClickListener {
            if(date == 0) {
                Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                var intent : Intent = Intent(this, NewDietActivity::class.java)
                intent.putExtra("DATE", date)
                //startActivity(intent)
                startActivityForResult(intent, REQUEST_ADD_DIET_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when(requestCode) {
            REQUEST_ADD_DIET_CODE -> {
                date = try {
                    intent?.getIntExtra("DATE", 0)!!
                } catch (re : RuntimeException) {
                    date
                }
                data.clear()
                data.addAll(loadDiet())
                rvAdapter = DietRecyclerViewAdapter(data, this, rv) /*{
                        data, num ->
                    var intent = Intent(this, NewDietActivity::class.java)
                    intent.putExtra("ID", data.id)
                    startActivity(intent)
                }*/
                rv.adapter = rvAdapter
                rv.layoutManager = LinearLayoutManager(this)
                rv.visibility = View.VISIBLE
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
                var intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 불러오기
    private fun loadDiet() : ArrayList<DietData> {
        var dietData = ArrayList<DietData>()
        sqldb = MyDBHelper.readableDatabase
        cursor = sqldb.rawQuery("SELECT * FROM diet_record WHERE date = $date ORDER BY time ASC", null)

        if(cursor.moveToFirst()) { // 저장된 글이 있으면
            var id : Int = 0
            var time : Int = 0
            var bitmap : Bitmap ?= null
            var memo : String = ""

            do{
                try {
                    id = cursor.getInt(cursor.getColumnIndex("DId"))
                    time = cursor.getInt(cursor.getColumnIndex("time"))
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
}
