package com.example.bodymanagerapp.menu.Body

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.Diet.DietRecyclerViewAdapter
import com.example.bodymanagerapp.menu.Diet.NewDietActivity
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class BodyActivity : AppCompatActivity() {
    // 권한 관련 변수
    private val REQUEST_READ_EXTERNAL_STORAGE : Int = 2000
    private val REQUEST_WRITE_EXTERNAL_STORAGE : Int = 3000
    private val REQUSET_CAMERA : Int = 4000
    private val REQUEST_CODE = 0


    // 상하단
    lateinit var bottom_nav_view: BottomNavigationView
    lateinit var toolbar: Toolbar

    lateinit var text_date : TextView // 날짜 텍스트
    var date : String = "" // 날짜 값 받는 곳
    lateinit var button_save : ImageButton // 저장 버튼
    lateinit var button_delete : ImageButton // 삭제 버튼
    lateinit var et_height : EditText // 키
    lateinit var et_weight : EditText // 몸무게
    lateinit var et_fat : EditText // 체지방량
    lateinit var et_muscle : EditText // 근육
    lateinit var button_inbody : Button // 인바디
    lateinit var button_camera : ImageButton // 카메라로 사진 찍는 버튼
    lateinit var button_gallery : ImageButton // 갤러리에서 사진 가져오는 버튼
    lateinit var body_image : ImageView // 눈바디

    var currenturi : Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body)

        //메뉴
        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        text_date = findViewById(R.id.text_body_date) // 날짜
        button_save = findViewById(R.id.button_body_save) // 저장 버튼
        button_delete = findViewById(R.id.button_body_delete) // 삭제 버튼
        et_height = findViewById(R.id.edit_text_body_height) // 키
        et_weight = findViewById(R.id.edit_text_body_weight) // 몸무게
        et_fat = findViewById(R.id.edit_text_body_fat) // 체지방량
        et_muscle = findViewById(R.id.edit_text_body_muscle) // 근육량
        button_inbody = findViewById(R.id.button_read_inbody) // 인바디 OCR 버튼
        button_camera = findViewById(R.id.button_body_camera) // 카메라
        button_gallery = findViewById(R.id.button_body_image) // 갤러리
        body_image = findViewById(R.id.image_body) // 눈바디

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)

        // 날짜 텍스트 클릭 시 달력으로 날짜 선택
        text_date.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                date = "${y}년 ${m + 1}월 ${d}일"
                text_date.text = date
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        // 카메라로 사진 찍기
        button_camera.setOnClickListener {

        }

        // 갤러리에서 사진 가져오기
        button_gallery.setOnClickListener {
            selectGallery()
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
                //replaceFragment(SettingsFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 사진 첨부 클릭 시 호출 + 사진 관련 권한 요청
    private fun selectGallery() {
        // 앨범 접근 권한
        var readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if(readPermission != PackageManager.PERMISSION_GRANTED) {
            // 권한이 허용되지 않음
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 이전에 이미 권한이 거부되었을 때 설명
                var dig = AlertDialog.Builder(this)
                dig.setTitle("권한이 필요한 이유")
                dig.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                dig.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                }
                dig.setNegativeButton("취소", null)
                dig.show()
            } else {
                // 처음 권한 요청
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            // 권한이 이미 허용됨
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    // 갤러리에서 사진 가져오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                data?.data?.let { uri ->
                    var view = findViewById<ImageView>(R.id.image_diet)
                    view.setImageURI(uri)
                    currenturi = uri
                }!!
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 카메라 권한 요청
    private fun cameraPermission() {
        // 앨범 접근 권한
        var writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if(writePermission != PackageManager.PERMISSION_GRANTED) {
            // 권한이 허용되지 않음
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 이전에 이미 권한이 거부되었을 때 설명
                var dig = AlertDialog.Builder(this)
                dig.setTitle("권한이 필요한 이유")
                dig.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다.")
                dig.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                }
                dig.setNegativeButton("취소", null)
                dig.show()
            } else {
                // 처음 권한 요청
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            // 권한이 이미 허용됨
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        }
    }
}