package com.example.bodymanagerapp.menu.Stats.Body

import android.app.DatePickerDialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.myDBHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*
import kotlin.collections.ArrayList

// 수정해야할 것
// 날짜 범위 조절 필요
// 그래프 이쁘게 고치기

class BodyStatsFragment : Fragment() {
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    // recyclerView
    lateinit var rv : RecyclerView
    lateinit var rvAdapter: BodyImageRecyclerViewAdapter
    var data = ArrayList<BodyImageData>()

    lateinit var tv_start_date : TextView // 시작 날짜
    lateinit var tv_end_date : TextView // 끝 날짜
    lateinit var btn_7days : Button // 최근 7일
    lateinit var btn_1month : Button // 최근 한 달
    lateinit var btn_3months : Button // 최근 3개월
    lateinit var btn_1year : Button // 최근 1년
    lateinit var btn_height : Button // 키
    lateinit var btn_weight : Button // 몸무게
    lateinit var btn_muscle : Button // 골격근량
    lateinit var btn_fat : Button // 체지방량
    lateinit var btn_body_img : Button // 눈바디
    lateinit var lineChart : LineChart
    lateinit var tv_feedback : TextView

    var start_date : Int= 0
    var end_date : Int= 0
    var now_date : Int= 0

    // 차트에 사용할 ArrayList
    var date_list = ArrayList<Int>()
    var data_list = ArrayList<Float>()

    // 피드백에 사용할 ArrayList
    var body_list = ArrayList<BodyDietData>()
    var diet_list = ArrayList<BodyDietData>()

    lateinit var ct : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_body_stats, container, false)
        ct = container!!.context

        myDBHelper = myDBHelper(ct)
        rv = view.findViewById(R.id.recycler_sb)

        var calendar : Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DATE)
        now_date = dateToInt(year, month, date)

        tv_start_date = view.findViewById(R.id.tv_sb_start_date)
        tv_end_date = view.findViewById(R.id.tv_sb_end_date)
        btn_7days = view.findViewById(R.id.button_sb_7days)
        btn_1month = view.findViewById(R.id.button_sb_1month)
        btn_3months = view.findViewById(R.id.button_sb_3months)
        btn_1year = view.findViewById(R.id.button_sb_1year)
        btn_height = view.findViewById(R.id.button_sb_height)
        btn_weight = view.findViewById(R.id.button_sb_weight)
        btn_muscle = view.findViewById(R.id.button_sb_muscle)
        btn_fat = view.findViewById(R.id.button_sb_fat)
        btn_body_img = view.findViewById(R.id.button_sb_img)
        lineChart = view.findViewById(R.id.sb_chart)
        tv_feedback = view.findViewById(R.id.tv_sb_feedback)

        // 시작 날짜 선택
        tv_start_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                start_date = dateToInt(y, m, d)
                tv_start_date.text = "${y}년 ${m+1}월 ${d}일"
                if(end_date > 0) {
                    loadFeedbackData()
                    setFeedback(body_list, diet_list)
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show()
        }

        // 끝 날짜 선택
        tv_end_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                end_date = dateToInt(y, m, d)
                tv_end_date.text = "${y}년 ${m+1}월 ${d}일"
                if(start_date > 0) {
                    loadFeedbackData()
                    setFeedback(body_list, diet_list)
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show()
        }

        // 7일 버튼 클릭 시
        btn_7days.setOnClickListener {
            if(date > 7) {
                start_date = dateToInt(year, month, date - 7)
                tv_start_date.text = "${year}년 ${month}월 ${date - 7}일"
            } else {
                var monthDate : Int = 31
                if(month == 4 || month == 6 || month == 9 || month == 11)
                    monthDate = 30
                else if(month == 2) {
                    if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                        monthDate = 29
                    else monthDate = 28
                }

                var i : Int = 7 - date
                start_date = dateToInt(year, month - 1, monthDate - i)
                tv_start_date.text = "${year}년 ${month - 1}월 ${monthDate - i}일"
            }
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"

            loadFeedbackData()
            setFeedback(body_list, diet_list)
        }

        // 1개월 버튼 클릭 시
        btn_1month.setOnClickListener {
            start_date = dateToInt(year, month - 1, date)
            tv_start_date.text = "${year}년 ${month - 1}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"

            loadFeedbackData()
            setFeedback(body_list, diet_list)
        }

        // 3개월 버튼 클릭 시
        btn_3months.setOnClickListener {
            start_date = dateToInt(year, month - 3, date)
            tv_start_date.text = "${year}년 ${month - 3}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"

            loadFeedbackData()
            setFeedback(body_list, diet_list)
        }

        // 1년 버튼 클릭 시
        btn_1year.setOnClickListener {
            start_date = dateToInt(year - 1, month, date)
            tv_start_date.text = "${year - 1}년 ${month}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"

            loadFeedbackData()
            setFeedback(body_list, diet_list)
        }

        // 키 버튼 클릭 시
        btn_height.setOnClickListener {
            loadData("height")
            lineChartGraph(view, data_list, date_list, "키")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        // 몸무게 버튼 클릭 시
        btn_weight.setOnClickListener {
            loadData("weight")
            lineChartGraph(view, data_list, date_list, "몸무게")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        // 골격근량 버튼 클릭 시
        btn_muscle.setOnClickListener {
            loadData("muscle_mass")
            lineChartGraph(view, data_list, date_list, "골격근량")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        // 체지방량 버튼 클릭 시
        btn_fat.setOnClickListener {
            loadData("fat_mass")
            lineChartGraph(view, data_list, date_list, "체지방량")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }
        
        // 눈바디 버튼 클릭 시
        btn_body_img.setOnClickListener {
            data.clear()
            data.addAll(loadImage())
            rvAdapter = BodyImageRecyclerViewAdapter(data, ct, rv)
            rv.adapter = rvAdapter
            rv.layoutManager = LinearLayoutManager(ct)
            rv.visibility = View.VISIBLE
            lineChart.visibility = View.GONE
        }

        return view
    }

    // 날짜를 yyyyMMdd 형식의 Int로 바꾸어 저장
    private fun dateToInt(year : Int, month : Int, date : Int) : Int {
        var ymd = "$year"

        ymd += if(month < 10)
            "0$month"
        else "$month"
        ymd += if(date < 10)
            "0$date"
        else "$date"

        return ymd.toInt()
    }

    // 데이터 불러오기
    private fun loadData(str : String) {
        data_list.clear()
        date_list.clear()

        sqldb = myDBHelper.readableDatabase
        var cursor : Cursor = sqldb.rawQuery("SELECT $str, date FROM body_record WHERE date >= $start_date AND date <= $end_date", null)

        if(cursor.moveToFirst()) {
            do {
                data_list.add(cursor.getFloat(cursor.getColumnIndex("$str")))
                date_list.add(cursor.getInt(cursor.getColumnIndex("date")))
            } while (cursor.moveToNext())
        }
    }

    private fun loadFeedbackData() {
        body_list .clear()
        diet_list.clear()

        sqldb = myDBHelper.readableDatabase

        var bodyCursor : Cursor = sqldb.rawQuery("SELECT * FROM body_record " +
                "WHERE date >= $start_date AND date <= $end_date " +
                "ORDER BY date ASC", null)

        // 첫 데이터와 마지막 데이터 가져오기
        if(bodyCursor.moveToFirst()) {
            var date = bodyCursor.getInt(bodyCursor.getColumnIndex("date"))
            var weight = bodyCursor.getFloat(bodyCursor.getColumnIndex("weight"))
            var muscle_mass = bodyCursor.getFloat(bodyCursor.getColumnIndex("muscle_mass"))
            var fat_mass = bodyCursor.getFloat(bodyCursor.getColumnIndex("fat_mass"))
            var bmi = bodyCursor.getFloat(bodyCursor.getColumnIndex("bmi"))
            var fat_percent = bodyCursor.getFloat(bodyCursor.getColumnIndex("fat_percent"))
            body_list.add(BodyDietData(date, weight, muscle_mass, fat_mass, bmi, fat_percent))
        }
        if(bodyCursor.moveToLast()) {
            var date = bodyCursor.getInt(bodyCursor.getColumnIndex("date"))
            var weight = bodyCursor.getFloat(bodyCursor.getColumnIndex("weight"))
            var muscle_mass = bodyCursor.getFloat(bodyCursor.getColumnIndex("muscle_mass"))
            var fat_mass = bodyCursor.getFloat(bodyCursor.getColumnIndex("fat_mass"))
            var bmi = bodyCursor.getFloat(bodyCursor.getColumnIndex("bmi"))
            var fat_percent = bodyCursor.getFloat(bodyCursor.getColumnIndex("fat_percent"))
            body_list.add(BodyDietData(date, weight, muscle_mass, fat_mass, bmi, fat_percent))
        }

        var dietCursor : Cursor = sqldb.rawQuery("SELECT date, time FROM diet_record " +
                "WHERE date >= $start_date AND date <= $end_date " +
                "ORDER BY date ASC", null)

        if(dietCursor.moveToFirst()) {
            do {
                var date = dietCursor.getInt(dietCursor.getColumnIndex("date"))
                var time = dietCursor.getInt(dietCursor.getColumnIndex("time"))
                diet_list.add(BodyDietData(date, time))
            } while (dietCursor.moveToNext())
        }

    }

    // 이미지를 제외한 값들의 그래프
    private fun lineChartGraph(view : View, data_list : ArrayList<Float>, date_list : ArrayList<Int>, str : String ) {


        var entries : ArrayList<Entry> = ArrayList() // 그래프에서 표현하려는 데이터 리스트
        for(i in 0 until data_list.size) {
            entries.add(Entry(data_list[i], i))
        }

        val xAxis : XAxis = lineChart.xAxis // x축 가져오기
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // x축 데이터의 위치를 아래로
            textSize = 10f // 텍스트 크기 지정
            setDrawGridLines(false) // 배경 그리드 라인
            //xAxis.setAxisMaxValue(end_date.toFloat())
            //xAxis.setAxisMinValue(start_date.toFloat())
        }

        lineChart.apply { // 라인차트 세팅
            axisRight.isEnabled = false // y축의 오른쪽 데이터 비활성화
            axisLeft.textColor = Color.BLACK // y축 왼쪽 데이터 글자 색
            setBackgroundColor(Color.WHITE) // 배경 색상
            setDescription("날짜") // description 글자
            setDescriptionTextSize(12f) // description 글자 크기
        }

        var depenses : LineDataSet = LineDataSet(entries, "$str")
        depenses.axisDependency = YAxis.AxisDependency.LEFT

        var dates : ArrayList<String> = ArrayList()
        for(i in 0 until date_list.size) {
            val year = date_list[i] / 10000
            val month = (date_list[i] % 10000) / 100
            val date = date_list[i] % 100
            dates.add("${year}년 ${month}월 ${date}일")
        }

        var data_sets : ArrayList<ILineDataSet> = ArrayList()
        data_sets.add(depenses)
        var data : LineData = LineData(dates, data_sets)
        depenses.color = Color.BLACK
        depenses.valueTextSize = 10f
        //depenses.valueFormatter = MyValueFormatter()
        depenses.setCircleColor(Color.BLACK)

        lineChart.data = data
        lineChart.invalidate()
    }

    // 저장된 이미지 불러오기
    private fun loadImage() : ArrayList<BodyImageData> {
        var imgData = ArrayList<BodyImageData>()
        sqldb = myDBHelper.readableDatabase
        val cursor = sqldb.rawQuery("SELECT date, body_photo FROM body_record WHERE date >= $start_date AND date <= $end_date", null)

        if(cursor.moveToFirst()) { // 저장된 글이 있으면
            do {
                val bodyImg = cursor.getBlob(cursor.getColumnIndex("body_photo"))
                if(bodyImg != null) { // 사진이 저장되어 있으면
                    val bitmap = BitmapFactory.decodeByteArray(bodyImg, 0, bodyImg!!.size)
                    val imgDate = cursor.getInt(cursor.getColumnIndex("date"))

                    val y = imgDate / 10000
                    val m = (imgDate % 10000) / 100
                    val d = imgDate % 100
                    imgData.add(BodyImageData("${y}년 ${m}월 ${d}일", bitmap)) // 데이터 추가
                }
            } while (cursor.moveToNext())
        }
        return imgData
    }

    private fun setFeedback(body : ArrayList<BodyDietData>, diet : ArrayList<BodyDietData>) {
        var feedback = ""
        if (body.size == 0 && diet.size == 0) {
            Toast.makeText(ct, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        if(body.size > 0) {
            feedback += " 현재 체지방률은 ${body[1].fat_percent}%입니다. "
            // 남성과 여성 나눠야함 일단 여성의 경우만 작성
            if(body[body.size-1].fat_percent < 11) {
                feedback += "체지방률이 매우 낮은 상태입니다. 생리 등 건강 상의 문제가 생길 수 있으니 " +
                        "체지방을 증가하는 것을 추천드려요!"
            } else if(body[body.size-1].fat_percent < 20) {
                feedback += "적정 체지방률 보다 낮은 체지방을 가지고 계시는군요. 마른 것도 좋지만 " +
                        "체지방이 낮을 경우 건강에 문제가 생길 수도 있으니 주의하세요!"
            } else if(body[body.size-1].fat_percent < 25) {
                feedback += "적정 체지방률에 해당합니다!"
            } else if(body[body.size-1].fat_percent < 30) {
                feedback += "적정 체지방률 보다 조금 높은 상태입니다. 운동을 통해 관리하시는 것이 좋겠어요."
            } else {
                feedback += "체지방률이 매우 높습니다. 비만으로 인해 고혈압, 당뇨병, 각종 암 등에 걸릴 " +
                        "확률이 높아집니다. 꾸준한 운동과 식단 조절을 통해 적정 체지방률인 20%~25%가 될 " +
                        "수 있도록 노력해주세요!"
            }
        }

        tv_feedback.text = feedback
    }

    companion object {

    }

}