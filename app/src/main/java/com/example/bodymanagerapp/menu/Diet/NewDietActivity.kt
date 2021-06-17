package com.example.bodymanagerapp.menu.Diet

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.bodymanagerapp.Preference.MyPreference
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.menu.Exercise.ExerciseActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewDietActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    // DB
    lateinit var MyDBHelper: MyDBHelper
    lateinit var sqldb : SQLiteDatabase

    // 권한 관련 변수
    private val REQUEST_READ_EXTERNAL_STORAGE : Int = 2000
    private val REQUEST_CODE = 0
    private val REQUEST_CODE_GALLERY = 0
    private val REQUEST_CODE_CAMERA = 1

    lateinit var button_diet_save : Button // 저장 버튼
    lateinit var button_diet_cancel : Button // 삭제 버튼
    lateinit var text_time : TextView // 시간
    lateinit var image_diet : ImageView // 식단 사진
    lateinit var button_camera : ImageButton // 카메라 버튼
    lateinit var button_gallery : ImageButton // 갤러리 버튼
    lateinit var diet_memo : EditText // 메모

    var date : Int = 0
    var id : Int = 0
    var time : Int = 0

    var currenturi: Uri?=null // 사진 uri
    var imguri : String ?= null
    var isLoaded : Boolean = false

    // 포인트 값 가져오기
    var point : Int = MyPreference.prefs.getInt("point", 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_diet)

        MyDBHelper = MyDBHelper(this)

        var intent : Intent = getIntent()
        date = intent.getIntExtra("DATE", 0)
        id = intent.getIntExtra("ID", 0)

        button_diet_save = findViewById(R.id.button_diet_save) // 저장 버튼
        button_diet_cancel = findViewById(R.id.button_diet_cancel) // 삭제 버튼
        text_time = findViewById(R.id.text_diet_time) // 시간
        image_diet = findViewById(R.id.image_diet) // 식단 사진
        button_camera = findViewById(R.id.button_diet_camera) // 카메라 버튼
        button_gallery = findViewById(R.id.button_diet_image) // 갤러리 버튼
        diet_memo = findViewById(R.id.diet_memo) // 식단 메모

        if(id > 0) loadDiet()

        // 시간 텍스트 클릭 시
        text_time.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, h, m ->
                time = h * 60 + m
                text_time.text = "${h}시 ${m}분"
            }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
        }

        // 카메라로 사진 찍기
        button_camera.setOnClickListener {
            cameraPermission()
            //startCapture()
        }

        // 갤러리에서 사진 가져오기
        button_gallery.setOnClickListener {
            galleryPermission()
        }

        // 저장 버튼 클릭 시
        button_diet_save.setOnClickListener {
            if(id == 0) {
                saveDiet()
                MyPreference.prefs.setInt("point", (point + 40)) // 포인트 획득
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                updateDiet()
                Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, DietActivity::class.java)
            intent.putExtra("DATE", date)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // 취소 버튼 클릭 시
        button_diet_cancel.setOnClickListener{
            finish()
        }
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
                        var view = findViewById<ImageView>(R.id.image_diet)
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
                        image_diet.setImageBitmap(bitmap)
                        image_diet.visibility = View.VISIBLE
                    } else {
                        val decode = ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
                        val bitmap = ImageDecoder.decodeBitmap(decode)
                        image_diet.setImageBitmap(bitmap)
                        image_diet.visibility = View.VISIBLE
                    }
                } else if (resultCode == Activity.RESULT_CANCELED)
                    Toast.makeText(this, "사진 찍기 취소", Toast.LENGTH_LONG).show()
            }
        }

    }

    // 저장
    private fun saveDiet() {
        sqldb = MyDBHelper.writableDatabase
        
        var imgView = findViewById<ImageView>(R.id.image_diet)
        var memoView = findViewById<TextView>(R.id.diet_memo)
        
        var image : Drawable = imgView.drawable
        var memo : String = memoView.text.toString()
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
            sqldb.execSQL("INSERT INTO diet_record VALUES (null, $date, $time, null,'$memo')")
        } else { // 저장하려는 사진이 있는 경우
            var insQuery : String = "INSERT INTO diet_record (DId, date, time, diet_photo, memo) " +
                    "VALUES (null, '$date', $time, ?, '$memo')"
            var stmt : SQLiteStatement = sqldb.compileStatement(insQuery)
            stmt.bindBlob(1, byteArray)
            stmt.execute()
        }
    }

    // 수정
    private fun updateDiet() {
        sqldb = MyDBHelper.writableDatabase

        //var diet_date : String = date
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
            sqldb.execSQL("UPDATE diet_record SET " +
                    "time = $time, diet_photo = null, memo = '$memo' WHERE DId = $id")
        } else { // 저장하려는 사진이 있는 경우
            var udtQuery : String = "UPDATE diet_record SET "+
                    "time = $time, diet_photo = ?, memo = '$memo' WHERE DId = $id"
            var stmt : SQLiteStatement = sqldb.compileStatement(udtQuery)
            stmt.bindBlob(1, byteArray)
            stmt.execute()
        }
        sqldb.close()
    }

    // 불러오기
    private fun loadDiet() {
        sqldb = MyDBHelper.readableDatabase
        var cursor : Cursor = sqldb.rawQuery("SELECT * FROM diet_record WHERE DId = '${id}'", null)

        // 해당 날짜에 저장된 식단들 가져오기
        if(cursor.moveToFirst()) {
            // 시간 값 가져오기
            time = cursor.getInt(cursor.getColumnIndex("time"))
            text_time.text = "${time / 60}시 ${time % 60}분"
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

    // 카메라 권한 요청
    private fun cameraPermission() {
        var camPer = object : PermissionListener {
            override fun onPermissionGranted() {
                //Toast.makeText(this@NewDietActivity, "카메라 권한에 동의하셨습니다.", Toast.LENGTH_SHORT).show()
                startCapture()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@NewDietActivity, "카메라 권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
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
}