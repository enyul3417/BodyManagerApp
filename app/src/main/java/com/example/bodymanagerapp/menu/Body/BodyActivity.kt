package com.example.bodymanagerapp.menu.Body

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Diet.DietActivity
import com.example.bodymanagerapp.menu.Diet.DietData
import com.example.bodymanagerapp.menu.Diet.DietRecyclerViewAdapter
import com.example.bodymanagerapp.menu.Diet.NewDietActivity
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.example.bodymanagerapp.menu.Stats.StatsActivity
import com.example.bodymanagerapp.myDBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// 수정해야할 것
// 삭제 후 해당 날짜로 새로고침 넣기
// 사진 사이즈 조절하기

class BodyActivity : AppCompatActivity() {
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb : SQLiteDatabase

    // 권한 관련 변수
    private val REQUEST_READ_EXTERNAL_STORAGE : Int = 2000
    private val REQUEST_CODE_GALLERY = 0
    private val REQUEST_CODE_CAMERA = 1
    private val REQUEST_CODE_DELETE = 2

    // 상하단
    lateinit var bottom_nav_view: BottomNavigationView
    lateinit var toolbar: Toolbar

    lateinit var text_date : TextView // 날짜 텍스트
    var date : Int = 0 // 날짜 값 받는 곳
    lateinit var date_format : SimpleDateFormat
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
    var imguri : String ?= null
    var isLoaded : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body)

        myDBHelper = myDBHelper(this)

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

        date_format = SimpleDateFormat("yyyyMMdd")

        bottom_nav_view.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        setSupportActionBar(toolbar)

        // 날짜 텍스트 클릭 시 달력으로 날짜 선택
        text_date.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                var ymd = "$y"
                ymd += if(m < 10)
                    "0${m+1}"
                else "${m+1}"
                ymd += if(d < 10)
                    "0$d"
                else "$d"
                date = ymd.toInt()
                text_date.text = "${y}년 ${m+1}월 ${d}일"
                loadBody()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        // 카메라로 사진 찍기
        button_camera.setOnClickListener {
            cameraPermission()
            startCapture()
        }

        // 갤러리에서 사진 가져오기
        button_gallery.setOnClickListener {
            galleryPermission()
        }

        button_save.setOnClickListener {
            if (date == 0)
                Toast.makeText(this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show()
            else {
                if(isLoaded) updateBody() // 저장된 내용이 있으면 수정하기
                else saveBody() // 없으면 새로운 레코드 추가
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        button_delete.setOnClickListener {
            if(isLoaded) {
                var dig = AlertDialog.Builder(this) // 대화상자
                dig.setTitle("삭제 확인") // 제목
                dig.setMessage("삭제하시겠습니까?")
                dig.setPositiveButton("확인") { dialog, which ->
                    deleteBody()
                    var intent = Intent(this, BodyActivity::class.java)
                    intent.putExtra("DATE", date)
                    Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                }

                dig.setNegativeButton("취소", null)
                dig.show()
            }
            else {
                Toast.makeText(this, "저장된 데이터가 없습니다..", Toast.LENGTH_SHORT).show()
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

                // 상태 메뉴 선택 시
                R.id.navigation_stats -> {
                    var intent : Intent = Intent(this, StatsActivity::class.java)
                    startActivity(intent)
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
    private fun galleryPermission() {
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
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            REQUEST_CODE_GALLERY -> { // 갤러리에서 가져온 사진 넣기
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        var view = findViewById<ImageView>(R.id.image_body)
                        view.setImageURI(uri)
                        currenturi = uri
                        view.visibility = View.VISIBLE
                    }!!
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_CODE_CAMERA -> { // 촬영한 사진 넣기
                if (resultCode == Activity.RESULT_OK) {
                    galleryAddPic()
                    val file = File(imguri)
                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media
                            .getBitmap(contentResolver, Uri.fromFile(file))
                        body_image.setImageBitmap(bitmap)
                        body_image.visibility = View.VISIBLE
                    } else {
                        val decode = ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
                        val bitmap = ImageDecoder.decodeBitmap(decode)
                        body_image.setImageBitmap(bitmap)
                        body_image.visibility = View.VISIBLE
                    }
                } else if (resultCode == Activity.RESULT_CANCELED)
                    Toast.makeText(this, "사진 찍기 취소", Toast.LENGTH_LONG).show()
            }
        }

    }

    // 카메라 권한 요청
    private fun cameraPermission() {
        var camPer = object : PermissionListener {
            override fun onPermissionGranted() {
                //Toast.makeText(this@BodyActivity, "카메라 권한에 동의하셨습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                //Toast.makeText(this@BodyActivity, "카메라 권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(camPer)
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .check()
    }

    private fun createImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "BODY_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            imguri = absolutePath
        }
    }

    private fun startCapture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile : File? = try {
                    createImageFile()
                } catch (ioe : IOException) {
                    null
                }
                photoFile?.also {
                    val  photoURI : Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.bodymanagerapp.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(imguri)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    // 저장하기
    private fun saveBody() {
        sqldb = myDBHelper.writableDatabase

        var height : Float = et_height.text.toString().toFloat()
        var weight : Float = et_weight.text.toString().toFloat()
        var muscle : Float = et_muscle.text.toString().toFloat()
        var fat : Float = et_fat.text.toString().toFloat()
        var image : Drawable = body_image.drawable
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
            sqldb.execSQL("INSERT INTO body_record VALUES ($date, $height, $weight, $muscle, $fat, null)")
        } else { // 저장하려는 사진이 있는 경우
            var insQuery : String = "INSERT INTO body_record (date, height, weight, muscle_mass, fat_mass, body_photo) " +
                    "VALUES ($date, $height, $weight, $muscle, $fat, ?)"
            var stmt : SQLiteStatement = sqldb.compileStatement(insQuery)
            stmt.bindBlob(1, byteArray)
            stmt.execute()
        }
    }

    // 불러오기
    private fun loadBody() {
        et_height.text = null
        et_weight.text = null
        et_muscle.text = null
        et_fat.text = null
        body_image.setImageBitmap(null)
        body_image.visibility = View.GONE
        isLoaded = false

        sqldb = myDBHelper.readableDatabase
        val cursor = sqldb.rawQuery("SELECT * FROM body_record WHERE date = $date", null)

        if(cursor.moveToFirst()) { // 저장된 글이 있으면
            var bitmap : Bitmap ?= null

            var height = cursor.getFloat(cursor.getColumnIndex("height"))
            var weight = cursor.getFloat(cursor.getColumnIndex("weight"))
            var muscle = cursor.getFloat(cursor.getColumnIndex("muscle_mass"))
            var fat = cursor.getFloat(cursor.getColumnIndex("fat_mass"))
            bitmap = try {
                val image : ByteArray ?= cursor.getBlob(cursor.getColumnIndex("body_photo"))
                BitmapFactory.decodeByteArray(image, 0, image!!.size)
            } catch (rte: RuntimeException) { // 이미지가 없을 경우
                null
            }

            et_height.setText(height.toString())
            et_weight.setText(weight.toString())
            et_muscle.setText(muscle.toString())
            et_fat.setText(fat.toString())
            body_image.setImageBitmap(bitmap)
            if (bitmap != null ) { // 등록한 이미지가 있다면
                body_image.visibility = View.VISIBLE
            } else { // 등록한 이미지가 없다면
                body_image.visibility = View.GONE

            }
            isLoaded = true
            sqldb.close()
        }
    }

    private fun updateBody() {
        sqldb = myDBHelper.writableDatabase

        var height : Float = et_height.text.toString().toFloat()
        var weight : Float = et_weight.text.toString().toFloat()
        var muscle : Float = et_muscle.text.toString().toFloat()
        var fat : Float = et_fat.text.toString().toFloat()
        var image : Drawable = body_image.drawable
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
            sqldb.execSQL("UPDATE body_record SET " +
                    "height = $height, weight = $weight, muscle_mass = $muscle, fat_mass = $fat, body_photo = null" +
                    "where date = $date")
        } else { // 저장하려는 사진이 있는 경우
            var udtQuery : String = "UPDATE body_record SET " +
                    "height = $height, weight = $weight, muscle_mass = $muscle, fat_mass = $fat, body_photo = ?" +
                    "where date = $date"
            var stmt : SQLiteStatement = sqldb.compileStatement(udtQuery)
            stmt.bindBlob(1, byteArray)
            stmt.execute()
        }
        sqldb.close()
    }

    private fun deleteBody() {
        sqldb = myDBHelper.writableDatabase
        sqldb.execSQL("DELETE FROM body_record WHERE date = $date")
        sqldb.close()
    }
}