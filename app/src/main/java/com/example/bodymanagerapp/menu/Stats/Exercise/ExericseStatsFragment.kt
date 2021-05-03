package com.example.bodymanagerapp.menu.Stats.Exercise

import android.app.DatePickerDialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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


class ExericseStatsFragment : Fragment() {
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    lateinit var tv_start_date : TextView // 시작 날짜
    lateinit var tv_end_date : TextView // 끝 날짜
    lateinit var btn_7days : Button // 최근 7일
    lateinit var btn_1month : Button // 최근 한 달
    lateinit var btn_3months : Button // 최근 3개월
    lateinit var btn_1year : Button // 최근 1년
    lateinit var spinner : Spinner
    lateinit var btn_max_weight : Button
    lateinit var btn_volume : Button
    lateinit var btn_time : Button
    lateinit var lineChart : LineChart

    // 날짜
    var start_date : Int= 0
    var end_date : Int= 0
    var now_date : Int= 0

    // 불러온 데이터를 저장할 배열
    var nameList = ArrayList<String>()
    var exerciseData = ArrayList<ExerciseStatsData>()
    var maxWeightList = ArrayList<Float>()
    var volumeList = ArrayList<Float>()
    var timeList = ArrayList<String>()

    lateinit var ct : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exericse_stats, container, false)
        ct = container!!.context

        myDBHelper = myDBHelper(ct)

        var calendar : Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DATE)
        now_date = dateToInt(year, month, date)

        tv_start_date = view.findViewById(R.id.tv_se_start_date)
        tv_end_date = view.findViewById(R.id.tv_se_end_date)
        btn_7days = view.findViewById(R.id.button_se_7days)
        btn_1month = view.findViewById(R.id.button_se_1month)
        btn_3months = view.findViewById(R.id.button_se_3months)
        btn_1year = view.findViewById(R.id.button_se_1year)
        spinner = view.findViewById(R.id.spinner_se)
        btn_max_weight = view.findViewById(R.id.button_se_max_weight)
        btn_volume = view.findViewById(R.id.button_se_volume)
        btn_time = view.findViewById(R.id.button_se_time)
        lineChart = view.findViewById(R.id.se_chart)

        // spinner 항목 불러와서 연결
        loadExerciseName()
        val spinnerAdapter = ArrayAdapter(ct, android.R.layout.simple_spinner_dropdown_item, nameList)
        spinner.adapter = spinnerAdapter

        // 시작 날짜 선택
        tv_start_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                start_date = dateToInt(y, m, d)
                tv_start_date.text = "${y}년 ${m+1}월 ${d}일"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show()
        }

        // 끝 날짜 선택
        tv_end_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                end_date = dateToInt(y, m, d)
                tv_end_date.text = "${y}년 ${m+1}월 ${d}일"
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
        }

        // 1개월 버튼 클릭 시
        btn_1month.setOnClickListener {
            start_date = dateToInt(year, month - 1, date)
            tv_start_date.text = "${year}년 ${month - 1}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"
        }

        // 3개월 버튼 클릭 시
        btn_3months.setOnClickListener {
            start_date = dateToInt(year, month - 3, date)
            tv_start_date.text = "${year}년 ${month - 3}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"
        }

        // 1년 버튼 클릭 시
        btn_1year.setOnClickListener {
            start_date = dateToInt(year - 1, month, date)
            tv_start_date.text = "${year - 1}년 ${month}월 ${date}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"
        }

        // 최대 무게 (1RM) 버튼 클릭 시
        btn_max_weight.setOnClickListener {
            exerciseData.clear()
            maxWeightList.clear()
            exerciseData.addAll(loadData())

            for(i in 0 until exerciseData.size) {
                if(exerciseData[i].weightList!![0] == 0f) { // 무게 값이 없을 경우 rm 계산 불가
                    maxWeightList.add(0f)
                    Toast.makeText(ct, "저장된 무게가 없어서 1RM을 구할 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    var weight = exerciseData[i].weightList!![0]
                    var rm  = weight + (weight * exerciseData[i].exerciseCount!![0] * 0.025f)
                    maxWeightList.add(rm)
                }
            }

            lineChartGraph(view, maxWeightList)
            lineChart.visibility = View.VISIBLE
        }

        // 볼륨 버튼 클릭 시
        btn_volume.setOnClickListener {
            exerciseData.clear()
            volumeList.clear()
            exerciseData.addAll(loadData())

            // 볼륨 계산해서 배열에 넣기
            for(i in 0 until exerciseData.size) {
                var volume = 0f

                if(exerciseData[i].timeList!![0] == "null") {
                    if (exerciseData[i].weightList!![0] == null) { // 세트와 횟수만
                        for (j in 0 until exerciseData[i].setNum) {
                            volume += exerciseData[i].exerciseCount!![j]
                        }
                    } else { // 세트, 무게, 횟수
                        for (j in 0 until exerciseData[i].setNum) {
                            volume += exerciseData[i].weightList!![j] * exerciseData[i].exerciseCount!![j]
                        }
                    }
                } else {
                    Toast.makeText(ct, "운동 시간만으로는 볼륨을 계산할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                volumeList.add(volume)
            }

            lineChartGraph(view, volumeList)
            lineChart.visibility = View.VISIBLE
        }

        // 시간 버튼 클릭 시
        btn_time.setOnClickListener {
            exerciseData.clear()
            timeList.clear()
            exerciseData.addAll(loadData())

            for(i in 0 until exerciseData.size) {

            }
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

    // Spinner에 넣을 운동 이름 불러오기
    private fun loadExerciseName() {
        nameList.clear()

        sqldb = myDBHelper.readableDatabase
        var cursor : Cursor = sqldb.rawQuery("SELECT DISTINCT exercise_name FROM exercise_counter ORDER BY exercise_name ASC", null)

        if(cursor.moveToFirst()) {
            do {
               nameList.add(cursor.getString(cursor.getColumnIndex("exercise_name")))
            } while (cursor.moveToNext())
        } else {
            nameList.add("운동 기록이 없습니다.")
        }
        sqldb.close()
    }

    // 그래프에 사용될 데이터들 불러오기
    private fun loadData() : ArrayList<ExerciseStatsData> {
        var data = ArrayList<ExerciseStatsData>()

        sqldb = myDBHelper.readableDatabase
        val dateCursor : Cursor = sqldb.rawQuery("SELECT DISTINCT date FROM exercise_counter " +
                "WHERE exercise_name = '${nameList[spinner.selectedItemPosition]}' AND date >= $start_date AND date <= $end_date " +
                "ORDER BY date ASC", null)
        if(dateCursor.moveToFirst()) {
            // 날짜와 이름에 해당하는 운동 기록 가져오기
            do {
                val date = dateCursor.getInt(dateCursor.getColumnIndex("date"))
                val cursor : Cursor = sqldb.rawQuery("SELECT DISTINCT * FROM exercise_counter WHERE date = $date AND exercise_name = '${nameList[spinner.selectedItemPosition]}'", null)

                var setCount : Int = 0
                var weightList = ArrayList<Float>()
                var numList = ArrayList<Int>()
                var timeList = ArrayList<String>()

                setCount = cursor.count

                if(cursor.moveToFirst()) {
                    do {
                        weightList?.add(cursor.getFloat(cursor.getColumnIndex("weight")))
                        numList?.add(cursor.getInt(cursor.getColumnIndex("exercise_count")))
                        timeList?.add(cursor.getString(cursor.getColumnIndex("time")))
                    } while (cursor.moveToNext())
                }
                data.add(ExerciseStatsData(date, setCount, weightList, numList, timeList))
            } while(dateCursor.moveToNext())
        }
        sqldb.close()
        return data
    }

    // 운동 기록을 그래프로 나타냄
    private fun lineChartGraph(view : View, dataList : ArrayList<Float> ) {
        var entries : ArrayList<Entry> = ArrayList() // 그래프에서 표현하려는 데이터 리스트
        for(i in 0 until dataList.size) {
            entries.add(Entry(dataList[i], i))
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

        var depenses : LineDataSet = LineDataSet(entries, "depenses")
        depenses.axisDependency = YAxis.AxisDependency.LEFT

        var dates : ArrayList<String> = ArrayList()
        for(i in 0 until exerciseData.size) {
            val year = exerciseData[i].date / 10000
            val month = (exerciseData[i].date % 10000) / 100
            val date = exerciseData[i].date % 100
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

    companion object {

    }
}