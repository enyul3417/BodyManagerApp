package com.example.bodymanagerapp.menu.Diet

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.myDBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class DietActivity : AppCompatActivity() {

    // BottomNavigationView
    lateinit var bottom_nav_view : BottomNavigationView
    lateinit var toolbar: Toolbar

    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb : SQLiteDatabase
    lateinit var ids : ArrayList<Int>

    // 권한 관련 변수
    private val REQUEST_READ_EXTERNAL_STORAGE : Int = 2000
    private val REQUEST_CODE = 0

    lateinit var text_date : TextView // 날짜
    lateinit var button_diet_save : Button // 저장 버튼
    lateinit var button_diet_delete : Button // 삭제 버튼

    //식단 관련 변수
    lateinit var text_time : TextView // 시간
    lateinit var image_diet : ImageView // 식단 사진
    lateinit var diet_memo : EditText // 메모

    var currenturi: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.toolbar)

        myDBHelper = myDBHelper(this)

        text_date = findViewById(R.id.date_text) // 날짜
        button_diet_save = findViewById(R.id.button_diet_save) // 저장 버튼
        button_diet_delete = findViewById(R.id.button_diet_delete) // 삭제 버튼
        text_time = findViewById(R.id.text_diet_time) // 시간
        image_diet = findViewById(R.id.image_diet) // 식단 사진
        diet_memo = findViewById(R.id.diet_memo) // 식단 메모

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)

        // 날짜 텍스트 클릭 시
        text_date.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                text_date.text = "${y}년 ${m+1}월 ${d}일"
                loadDiet()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        // 시간 텍스트 클릭 시
        text_time.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, h, m ->
                text_time.text = "${h}시 ${m}분"
            }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
        }

        // 이미지 그림 클릭 시
        image_diet.setOnClickListener{
            selectGallery()
        }

        // 저장 버튼 클릭 시
        button_diet_save.setOnClickListener {
            if(ids.size < 0) {
                saveDiet()
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                updateDiet()
                Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }

        }

        // 삭제 버튼 클릭 시
        button_diet_delete.setOnClickListener {
            deleteDiet()
            Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            var intent : Intent = Intent(this, DietActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    // 하단 메뉴 선택 시 작동
    private val bottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            // 운동 메뉴 선택 시
            R.id.navigation_exercise -> {
                return@OnNavigationItemSelectedListener true
            }

            // 식단 메뉴 선택 시
            R.id.navigation_diet -> {
                var intent : Intent = Intent(this, DietActivity::class.java)
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

    private fun selectGallery() {
        // 앨범 접근 권한
        var readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        //Log.d("mainActivity", "$mainActivity: ")
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
                    image_diet.setImageURI(uri)
                    currenturi = uri
                }!!
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveDiet() {
        sqldb = myDBHelper.writableDatabase

        var diet_date : String = text_date.text.toString()
        var diet_time : String = text_time.text.toString()
        var image : Drawable = image_diet.drawable
        var memo : String = diet_memo.text.toString()
        var byteArray : ByteArray ?= null

        try {
            // 이미지 파일을 Bitmap 파일로, Bitmap 파일을 byteArray로 변환시켜서 BLOB 형으로 DB에 저장
            val bitmapDrawable = image as BitmapDrawable?
            val bitmap = bitmapDrawable?.bitmap
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, stream)
            byteArray = stream.toByteArray()
        } catch (cce: ClassCastException) { // 사진을 따로 저장안할 경우
            Log.d("image null", "이미지 저장 안함")
        }

        if(byteArray == null) { // 저장하려는 사진이 없을 경우
            sqldb.execSQL("INSERT INTO diet_record VALUES (null,'$diet_date','$diet_time', null,'$memo')")
        } else { // 저장하려는 사진이 있는 경우
            var insQuery : String = "INSERT INTO diet_record (id, date, time, diet_photo, memo) " +
                    "VALUES (null, '$diet_date', '$diet_time', ?, '$memo')"
            var stmt : SQLiteStatement = sqldb.compileStatement(insQuery)
            stmt.bindBlob(1, byteArray)
            stmt.execute()
        }
    }

    // 삭제
    private fun deleteDiet() {
        sqldb = myDBHelper.writableDatabase
        var id : Int = ids[0]
        sqldb.execSQL("DELETE FROM diet_record WHERE id = $id")
    }

    // 수정
    private fun updateDiet() {
        sqldb = myDBHelper.writableDatabase

        var id : Int = ids[0]
        var diet_date : String = text_date.text.toString()
        var diet_time : String = text_time.text.toString()
        var image : Drawable = image_diet.drawable
        var memo : String = diet_memo.text.toString()
        var byteArray : ByteArray ?= null

        try {
            // 이미지 파일을 Bitmap 파일로, Bitmap 파일을 byteArray로 변환시켜서 BLOB 형으로 DB에 저장
            val bitmapDrawable = image as BitmapDrawable?
            val bitmap = bitmapDrawable?.bitmap
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, stream)
            byteArray = stream.toByteArray()
        } catch (cce: ClassCastException) { // 사진을 따로 저장안할 경우
            Log.d("image null", "이미지 저장 안함")
        }

        if(byteArray == null) { // 저장하려는 사진이 없을 경우
            sqldb.execSQL("UPDATE diet_record SET date = '$diet_date', " +
                    "time = '$diet_time', diet_photo = null, memo = '$memo' WHERE id = $id")
        } else { // 저장하려는 사진이 있는 경우
            var udtQuery : String = "UPDATE diet_record SET date = '$diet_date', "+
                    "time = '$diet_time', diet_photo = ?, memo = '$memo' WHERE id = $id"
            var stmt : SQLiteStatement = sqldb.compileStatement(udtQuery)
            stmt.bindBlob(1, byteArray)
            stmt.execute()
        }
    }

    // 불러오기
    private fun loadDiet() {
        ids = ArrayList<Int>()
        text_time.text = "시간을 선택해주세요"
        image_diet.setImageResource(R.drawable.ic_baseline_image_24)
        diet_memo.setText("메모")

        sqldb = myDBHelper.readableDatabase
        var cursor : Cursor = sqldb.rawQuery("SELECT * FROM diet_record WHERE date = '${text_date.text.toString()}'", null)

        // 해당 날짜에 저장된 식단들 가져오기
        if(cursor.moveToFirst()) {
            // id 값 가져오기
            var i : Int = 0
            ids.add(cursor.getInt(cursor.getColumnIndex("id")))
            i++
            // 시간 값 가져오기
            text_time.text = cursor.getString(cursor.getColumnIndex("time"))
            // 사진 가져오기
            try {
                val image = cursor.getBlob(cursor.getColumnIndex("diet_photo")) ?: null
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image!!.size)
                image_diet.setImageBitmap(bitmap)
            } catch (knpe : KotlinNullPointerException) {
                Toast.makeText(this, "저장된 사진이 없습니다.", Toast.LENGTH_SHORT).show()
            }
            // 메모 내용 가져오기
            diet_memo.setText(cursor.getString(cursor.getColumnIndex("memo")))
        }
    }
}