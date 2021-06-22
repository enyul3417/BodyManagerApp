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
import com.example.bodymanagerapp.Preference.MyPreference
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.MyDBHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

// 수정해야할 것
// 날짜 범위 조절 필요
// 그래프 이쁘게 고치기

class BodyStatsFragment : Fragment() {
    // DB
    lateinit var MyDBHelper: MyDBHelper
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

    var sex = MyPreference.prefs.getInt("sex", 0)

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

        MyDBHelper = MyDBHelper(ct)
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
                start_date = dateToInt(y, m + 1, d)
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
                end_date = dateToInt(y, m + 1, d)
                tv_end_date.text = "${y}년 ${m+1}월 ${d}일"
                if(start_date > 0) {
                    loadFeedbackData()
                    setFeedback(body_list, diet_list)
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }

        // 7일 버튼 클릭 시
        btn_7days.setOnClickListener {
            btn_7days.isSelected = true
            btn_1month.isSelected = false
            btn_3months.isSelected = false
            btn_1year.isSelected = false

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
            btn_7days.isSelected = false
            btn_1month.isSelected = true
            btn_3months.isSelected = false
            btn_1year.isSelected = false

            start_date = dateToInt(year, month - 1, date)
            tv_start_date.text = "${year}년 ${month - 1}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"

            loadFeedbackData()
            setFeedback(body_list, diet_list)
        }

        // 3개월 버튼 클릭 시
        btn_3months.setOnClickListener {
            btn_7days.isSelected = false
            btn_1month.isSelected = false
            btn_3months.isSelected = true
            btn_1year.isSelected = false

            start_date = dateToInt(year, month - 3, date)
            tv_start_date.text = "${year}년 ${month - 3}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"

            loadFeedbackData()
            setFeedback(body_list, diet_list)
        }

        // 1년 버튼 클릭 시
        btn_1year.setOnClickListener {
            btn_7days.isSelected = false
            btn_1month.isSelected = false
            btn_3months.isSelected = false
            btn_1year.isSelected = true

            start_date = dateToInt(year - 1, month, date)
            tv_start_date.text = "${year - 1}년 ${month}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"

            loadFeedbackData()
            setFeedback(body_list, diet_list)
        }

        // 키 버튼 클릭 시
        btn_height.setOnClickListener {
            btn_height.isSelected = true
            btn_weight.isSelected = false
            btn_muscle.isSelected = false
            btn_fat.isSelected = false
            btn_body_img.isSelected = false

            loadData("height")
            lineChartGraph(view, data_list, date_list, "키")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        // 몸무게 버튼 클릭 시
        btn_weight.setOnClickListener {
            btn_height.isSelected = false
            btn_weight.isSelected = true
            btn_muscle.isSelected = false
            btn_fat.isSelected = false
            btn_body_img.isSelected = false

            loadData("weight")
            lineChartGraph(view, data_list, date_list, "몸무게")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        // 골격근량 버튼 클릭 시
        btn_muscle.setOnClickListener {
            btn_height.isSelected = false
            btn_weight.isSelected = false
            btn_muscle.isSelected = true
            btn_fat.isSelected = false
            btn_body_img.isSelected = false

            loadData("muscle_mass")
            lineChartGraph(view, data_list, date_list, "골격근량")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }

        // 체지방량 버튼 클릭 시
        btn_fat.setOnClickListener {
            btn_height.isSelected = false
            btn_weight.isSelected = false
            btn_muscle.isSelected = false
            btn_fat.isSelected = true
            btn_body_img.isSelected = false

            loadData("fat_mass")
            lineChartGraph(view, data_list, date_list, "체지방량")
            lineChart.visibility = View.VISIBLE
            rv.visibility = View.GONE
        }
        
        // 눈바디 버튼 클릭 시
        btn_body_img.setOnClickListener {
            btn_height.isSelected = false
            btn_weight.isSelected = false
            btn_muscle.isSelected = false
            btn_fat.isSelected = false
            btn_body_img.isSelected = true

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

        sqldb = MyDBHelper.readableDatabase
        var cursor : Cursor = sqldb.rawQuery("SELECT $str, date FROM body_record WHERE date >= $start_date AND date <= $end_date", null)

        if(cursor.moveToFirst()) {
            do {
                var temp = cursor.getFloat(cursor.getColumnIndex("$str"))
                if (temp != 0.0f) {
                    data_list.add(temp)
                    date_list.add(cursor.getInt(cursor.getColumnIndex("date")))
                }
            } while (cursor.moveToNext())
        }
    }

    private fun loadFeedbackData() {
        body_list .clear()
        diet_list.clear()

        sqldb = MyDBHelper.readableDatabase

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
        var dates : ArrayList<String> = ArrayList()
        for(i in 0 until date_list.size) {
            val year = date_list[i] / 10000
            val month = (date_list[i] % 10000) / 100
            val date = date_list[i] % 100
            dates.add("${year}년 ${month}월 ${date}일")
        }

        var entries : ArrayList<Entry> = ArrayList() // 그래프에서 표현하려는 데이터 리스트
        for(i in 0 until data_list.size) {
            entries.add(Entry(date_list[i].toFloat(), data_list[i]))
        }

        val xAxis : XAxis = lineChart.xAxis // x축 가져오기
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // x축 데이터의 위치를 아래로
            textSize = 10f // 텍스트 크기 지정
            setDrawGridLines(false) // 배경 그리드 라인
            valueFormatter = DateFormatter()
            labelCount = 3

        }

        val yAxis : YAxis = lineChart.axisLeft
        yAxis.labelCount = 5

        lineChart.apply { // 라인차트 세팅
            axisRight.isEnabled = false // y축의 오른쪽 데이터 비활성화
            axisLeft.textColor = Color.BLACK // y축 왼쪽 데이터 글자 색
            setBackgroundColor(Color.WHITE) // 배경 색상
            description.text = "날짜" // description 글자
            //setDescriptionTextSize(12f) // description 글자 크기
        }


        var depenses : LineDataSet = LineDataSet(entries, "$str")
        depenses.axisDependency = YAxis.AxisDependency.LEFT



        var data_sets : ArrayList<ILineDataSet> = ArrayList()
        data_sets.add(depenses)
        var data : LineData = LineData(data_sets)
        depenses.color = Color.BLACK
        depenses.valueTextSize = 10f
        //depenses.valueFormatter = MyValueFormatter()
        depenses.setCircleColor(Color.BLACK)

        lineChart.data = data
        lineChart.invalidate()
    }

    inner class DateFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val year = value.toInt() / 10000
            val month = (value.toInt() % 10000) / 100
            val day = (value.toInt() % 10000) % 100
            return "${year}/${month}/${day}"
        }
        /*override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
            val year = value.toInt() / 10000
            val month = (value.toInt() % 10000) / 100
            val day = (value.toInt() % 10000) % 100
            return "${year}/${month}/${day}"
        }*/
    }

    // 저장된 이미지 불러오기
    private fun loadImage() : ArrayList<BodyImageData> {
        var imgData = ArrayList<BodyImageData>()
        sqldb = MyDBHelper.readableDatabase
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
        var fatPercent = 0f
        var bmi = 0f
        if (body.size == 0 && diet.size == 0 || sex == 0) {
            Toast.makeText(ct, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        if(body.size > 0) {
            fatPercent = body[body.size-1].fat_percent
            bmi = body[body.size-1].bmi

            if(sex != 0 && fatPercent > 0) { // 체지방
                feedback += " 최근 체지방률은 ${fatPercent}%입니다. "
                feedback += when {
                    fatPercent <= 5 && sex == 1 -> { // 남성의 매우 낮은 체지방
                        "체지방률이 매우 낮은 상태입니다. 건강에 안좋은 영향을 줄 수 있으니 " +
                                "대회 등을 목적으로 하는 것이 아니라면 체지방을 증가해야해요!"
                    }
                    fatPercent < 11 && sex == 2 -> { // 여성의 매우 낮은 체지방
                        "체지방률이 매우 낮은 상태입니다. 생리 등 건강 상의 문제가 생길 수 있으니 " +
                                "대회 등을 목적으로 하는 것이 아니면 체지방을 증가하는 것을 추천드려요!"
                    }
                    fatPercent < 20 -> { // 낮은 체지방
                        "적정 체지방률 보다 낮은 체지방을 가지고 계시는군요. 마른 것도 좋지만 " +
                                "체지방이 낮을 경우 건강에 문제가 생길 수도 있으니 주의하세요!"
                    }
                    fatPercent < 25 -> { // 적정 체지방
                        "적정 체지방률에 해당합니다!"
                    }
                    fatPercent < 30 -> { // 높은 체지방
                        "적정 체지방률 보다 조금 높은 상태입니다. 운동을 통해 관리하시는 것이 좋겠어요."
                    }
                    else -> { // 매우 높은 체지방
                        "체지방률이 매우 높습니다. 비만으로 인해 고혈압, 당뇨병, 각종 암 등에 걸릴 " +
                                "확률이 높아집니다. 꾸준한 운동과 식단 조절을 통해 적정 체지방률인 20%~25%가 될 " +
                                "수 있도록 노력해주세요!"
                    }
                }
            }
            if(bmi > 0) {
                feedback += "\n 최근 BMI는 ${bmi}kg/m\u00B2으로, "
                feedback += when {
                    bmi < 18.5 -> {
                        "저체중입니다. 저체중도 비만만큼 위험하답니다. 저체중일 경우 골다공증, 만성피로, " +
                                "무기력증 등의 위험이 커지며 면역력도 떨어지게 됩니다. 규칙적인 식사와 " +
                                "운동으로 건강하게 체중을 증가하는 것이 좋아요:) 계속해서 건강이 안좋다면 " +
                                "꼭 전문가와 상담을 하셔야해요."
                    }
                    bmi < 23 -> {
                        "정상 체중입니다. 체중을 지금처럼 유지해봐요!"
                    }
                    bmi < 25 -> {
                        "과체중입니다. 과체중이거나 비만일 경우 암 위험이 평균 12% 높다고해요. 건강을 위해서 " +
                                "지금보다 체중을 낮추려고 노력해봐요. 무작정 굶으면 오히려 건강에 좋지 않으니 " +
                                "꾸준한 운동과 식단 조절을 통해서 감소해봐요!"
                    }
                    else -> {
                        "비만입니다. 비만일 경우 만성피로, 두통, 소화불량, 체력 부족 등의 문제를 겪을 수 있으며, " +
                                "고혈압, 당뇨 등의 위험성도 커진답니다. 식단 조절과 운동을 통해서 건강하게 체중을 " +
                                "줄여봐요. 주기적인 검사와 전문가의 상담을 받으면 더욱 좋겠죠?"
                    }
                }
            }
        }

        if(diet.size > 0) {
            var days = dateToDays(start_date, end_date)
            var avgDiet = round((diet.size.toFloat() / days) * 10) / 10 // 소수 둘째 자리에서 반올림
            var late = 0

            feedback += "\n 일일 평균 음식 섭취 수는 약 ${avgDiet}회로, "

            if(avgDiet < 3) { // 하루에 음식 섭취 수가 3번 미만이면
                feedback += "음식 섭취량이 적은 것으로 판단됩니다. 음식 섭취량이 너무 적으면, 영양분 저장을 위해 " +
                        "몸이 더 살찌기 쉽게 변한답니다."
                if (bmi < 18.5 && bmi > 0) {
                    feedback += "사용자님은 저체중이기 때문에 식사량을 늘리는 것을 추천드려요. 한 번에 많은 음식을 못드신다면 " +
                            "조금씩 나누어 자주 먹는 것도 좋아요."
                }
                if(bmi > 25) {
                    feedback += "만약 체중 감량을 위해 식사량을 급격히 줄인 것이라면 그 방법은 추천드리지 않아요. 요요가 올 수도 " +
                            "있으며, 폭식이나 과식을 할수도 있기 때문이에요. 이것 보다는 열량이 낮고 영양소가 골고루 " +
                            "들어간 식사를 세끼를 드시고, 간식을 줄이는 것을 추천드려요. 간식이 필요하다면 과자, 케이크 등" +
                            " 보다는 오이, 삶은 계란, 방울토마토와 같은 건강 간식을 드시는 것이 좋아요!"
                }
            } else if (avgDiet > 5) { // 하루에 5번 초과로 음식을 먹는다면
                feedback += "음식 섭취량이 많은 것으로 판단됩니다. 소량의 음식이고, 운동을 병행하고 있다면 괜찮지만, " +
                        "속이 더부룩할 정도의 양을 자주 드신다면 음식 섭취를 줄이는 것을 추천드려요."
            }

            for(i in 0 until diet.size) {
                if(diet[i].time > 21 * 60) { // 9시 이후에 음식을 섭취했으면
                    late++
                }
            }
            if(late.toFloat() / days * 100 > 40) { // 해당 기간의 40% 이상의 식단을 늦은 시간에 섭취했다면
                feedback += "\n 사용자님께서는 늦은 시간에 음식을 자주 섭취하시네요. 야식은 가끔 먹는 것은 괜찮아요." +
                        "하지만 야식을 자주 먹으면 수면의 질이 저하되며, 역류성 식도염 등과 같은 질병이 생길 수도 있어요. " +
                        "또 밤에는 몸이 에너지를 저장하려 하기에 야식을 먹으면 살이 더 잘찐답니다. 배가 너무 고프다면 " +
                        "바나나 하나, 우유 한 컵 등의 건강한 간식을 소량 섭취하시는 것을 추천드려요."
            }

        }

        tv_feedback.text = feedback
    }

    private fun dateToDays(start : Int, end : Int) : Int {
        var format = SimpleDateFormat("yyyyMMdd")
        var date1 = format.parse(start.toString())
        var date2 = format.parse(end.toString())

        return ((date2.time - date1.time) / (60 * 60 * 24 * 1000)).toInt() + 1
    }

    companion object {

    }

}