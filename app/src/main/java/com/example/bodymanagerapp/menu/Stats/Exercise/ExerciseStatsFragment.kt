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
import com.example.bodymanagerapp.MyDBHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
/*import com.github.mikephil.charting.formatter.YAxisValueFormatter*/
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ExerciseStatsFragment : Fragment() {
    // DB
    lateinit var MyDBHelper: MyDBHelper
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
    lateinit var tv_total_time : TextView
    lateinit var tv_total_steps : TextView
    lateinit var tv_max_part : TextView
    lateinit var tv_min_part : TextView
    lateinit var tv_feedback : TextView // 피드백

    // 날짜
    private var start_date : Int= 0
    private var end_date : Int= 0
    private var now_date : Int= 0

    // 불러온 데이터를 저장할 배열
    var nameList = ArrayList<String>()
    var exerciseData = ArrayList<ExerciseStatsData>()
    var maxWeightList = ArrayList<Float>()
    var volumeList = ArrayList<Float>()
    var timeList = ArrayList<Float>()

    var total_time = 0
    var total_steps = 0
    var exercise_days = 0

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

        MyDBHelper = MyDBHelper(ct)

        var calendar : Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DATE)
        now_date = dateToInt(year, month, day)

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
        tv_total_time = view.findViewById(R.id.tv_se_total_time)
        tv_total_steps = view.findViewById(R.id.tv_se_total_steps)
        tv_max_part = view.findViewById(R.id.tv_se_max_part)
        tv_min_part = view.findViewById(R.id.tv_se_min_part)
        tv_feedback = view.findViewById(R.id.tv_se_feedback)

        // spinner 항목 불러와서 연결
        loadExerciseName()
        val spinnerAdapter = ArrayAdapter(ct, android.R.layout.simple_spinner_dropdown_item, nameList)
        spinner.adapter = spinnerAdapter

        // 시작 날짜 선택
        tv_start_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                start_date = dateToInt(y, m + 1, d)
                tv_start_date.text = dateToString(start_date)
                if(end_date > 0) {
                    exerciseData.clear()
                    exerciseData.addAll(loadExerciseData())
                    loadExerciseRecord()
                    setFeedback(exerciseData)
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show()
        }

        // 끝 날짜 선택
        tv_end_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                end_date = dateToInt(y, m + 1, d)
                tv_end_date.text = dateToString(end_date)
                if(start_date > 0) {
                    exerciseData.clear()
                    exerciseData.addAll(loadExerciseData())
                    loadExerciseRecord()
                    setFeedback(exerciseData)
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

            if(day > 7) {
                start_date = dateToInt(year, month, day - 7)
                tv_start_date.text = dateToString(start_date)
            } else {
                var monthDate : Int = 31
                if(month == 4 || month == 6 || month == 9 || month == 11)
                    monthDate = 30
                else if(month == 2) {
                    if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                        monthDate = 29
                    else monthDate = 28
                }

                var i : Int = 7 - day
                start_date = dateToInt(year, month - 1, monthDate - i)
                tv_start_date.text = dateToString(start_date)
            }
            end_date = dateToInt(year, month, day)
            tv_end_date.text = dateToString(end_date)

            exerciseData.clear()
            exerciseData.addAll(loadExerciseData())
            loadExerciseRecord()
            setFeedback(exerciseData)
        }

        // 1개월 버튼 클릭 시
        btn_1month.setOnClickListener {
            btn_7days.isSelected = false
            btn_1month.isSelected = true
            btn_3months.isSelected = false
            btn_1year.isSelected = false

            start_date = dateToInt(year, month - 1, day)
            tv_start_date.text = dateToString(start_date)
            end_date = dateToInt(year, month, day)
            tv_end_date.text = dateToString(end_date)

            exerciseData.clear()
            exerciseData.addAll(loadExerciseData())
            loadExerciseRecord()
            setFeedback(exerciseData)
        }

        // 3개월 버튼 클릭 시
        btn_3months.setOnClickListener {
            btn_7days.isSelected = false
            btn_1month.isSelected = false
            btn_3months.isSelected = true
            btn_1year.isSelected = false

            start_date = dateToInt(year, month - 3, day)
            tv_start_date.text = dateToString(start_date)
            end_date = dateToInt(year, month, day)
            tv_end_date.text = dateToString(end_date)

            exerciseData.clear()
            exerciseData.addAll(loadExerciseData())
            loadExerciseRecord()
            setFeedback(exerciseData)
        }

        // 1년 버튼 클릭 시
        btn_1year.setOnClickListener {
            btn_7days.isSelected = false
            btn_1month.isSelected = false
            btn_3months.isSelected = false
            btn_1year.isSelected = true

            start_date = dateToInt(year - 1, month, day)
            tv_start_date.text = dateToString(start_date)
            end_date = dateToInt(year, month, day)
            tv_end_date.text = dateToString(end_date)

            exerciseData.clear()
            exerciseData.addAll(loadExerciseData())
            loadExerciseRecord()
            setFeedback(exerciseData)
        }

        // 최대 무게 (1RM) 버튼 클릭 시
        btn_max_weight.setOnClickListener {
            btn_max_weight.isSelected = true
            btn_volume.isSelected = false
            btn_time.isSelected = false
            exerciseData.clear()
            maxWeightList.clear()
            exerciseData.addAll(loadGraphData())

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

            lineChartGraph(maxWeightList, "1RM")
            lineChart.visibility = View.VISIBLE
        }

        // 볼륨 버튼 클릭 시
        btn_volume.setOnClickListener {
            btn_max_weight.isSelected = false
            btn_volume.isSelected = true
            btn_time.isSelected = false

            exerciseData.clear()
            volumeList.clear()
            exerciseData.addAll(loadGraphData())

            // 볼륨 계산해서 배열에 넣기
            for(i in 0 until exerciseData.size) {
                var volume = 0f

                if(exerciseData[i].timeList!![0] == 0) {
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

            lineChartGraph(volumeList, "볼륨")
            lineChart.visibility = View.VISIBLE
        }

        // 시간 버튼 클릭 시
        btn_time.setOnClickListener {
            btn_max_weight.isSelected = false
            btn_volume.isSelected = false
            btn_time.isSelected = true

            exerciseData.clear()
            timeList.clear()
            exerciseData.addAll(loadGraphData())

            for(i in 0 until exerciseData.size) {
                var time = 0
                if(exerciseData[i].timeList!![0] != 0) {
                    for(j in 0 until exerciseData[i].timeList!!.size) {
                        time += exerciseData[i].timeList!![j]
                    }
                    timeList.add(time.toFloat())
                } else {
                    Toast.makeText(context, "기록된 시간이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            lineChartGraph(timeList, "시간(시:분:초)")
            lineChart.visibility = View.VISIBLE
        }
        return view
    }

    // 날짜를 yyyyMMdd 형식의 Int로 바꾸어 저장
    private fun dateToInt(year : Int, month : Int, day : Int) : Int {
        var ymd = "$year"

        ymd += if(month < 10)
            "0$month"
        else "$month"
        ymd += if(day < 10)
            "0$day"
        else "$day"

        return ymd.toInt()
    }

    private fun dateToString(date : Int) : String {
        var year = date / 10000
        var month = date % 10000 / 100
        var day = date % 10000 % 100

        return "${year}년 ${month}월 ${day}일"
    }

    // Spinner에 넣을 운동 이름 불러오기
    private fun loadExerciseName() {
        nameList.clear()

        sqldb = MyDBHelper.readableDatabase
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
    private fun loadGraphData() : ArrayList<ExerciseStatsData> {
        var data = ArrayList<ExerciseStatsData>()

        sqldb = MyDBHelper.readableDatabase
        val dateCursor : Cursor = sqldb.rawQuery("SELECT DISTINCT date FROM exercise_counter " +
                "WHERE exercise_name = '${nameList[spinner.selectedItemPosition]}' AND date >= $start_date AND date <= $end_date " +
                "ORDER BY date ASC", null)
        if(dateCursor.moveToFirst()) {
            // 날짜와 이름에 해당하는 운동 기록 가져오기
            do {
                val date = dateCursor.getInt(dateCursor.getColumnIndex("date"))
                val cursor : Cursor = sqldb.rawQuery("SELECT * FROM exercise_counter WHERE date = $date AND exercise_name = '${nameList[spinner.selectedItemPosition]}'", null)

                var setCount : Int = cursor.count
                var weightList = ArrayList<Float>()
                var numList = ArrayList<Int>()
                var timeList = ArrayList<Int>()

                if(cursor.moveToFirst()) {
                    do {
                        weightList?.add(cursor.getFloat(cursor.getColumnIndex("weight")))
                        numList?.add(cursor.getInt(cursor.getColumnIndex("exercise_count")))
                        timeList?.add(cursor.getInt(cursor.getColumnIndex("time")))
                    } while (cursor.moveToNext())
                }
                data.add(ExerciseStatsData(date, setCount, weightList, numList, timeList))
            } while(dateCursor.moveToNext())
        }
        sqldb.close()
        return data
    }

    // 운동 기록을 그래프로 나타냄
    private fun lineChartGraph(dataList : ArrayList<Float>, str : String ) {
        lineChart.clear()

        var entries : ArrayList<Entry> = ArrayList() // 그래프에서 표현하려는 데이터 리스트
        for(i in 0 until dataList.size) {
            entries.add(Entry(exerciseData[i].date.toFloat(), dataList[i]))
        }

        val xAxis : XAxis = lineChart.xAxis // x축 가져오기
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // x축 데이터의 위치를 아래로
            textSize = 10f // 텍스트 크기 지정
            setDrawGridLines(false) // 배경 그리드 라인
            valueFormatter = DateFormatter()
        }
        xAxis.labelCount = 3

        lineChart.apply { // 라인차트 세팅
            axisRight.isEnabled = false // y축의 오른쪽 데이터 비활성화
            axisLeft.textColor = Color.BLACK // y축 왼쪽 데이터 글자 색
            if(dataList == timeList){ // 시간이면 시간 형태에 맞춰서 표기
                axisLeft.valueFormatter = YAxisFormatter()
            } else {
                axisLeft.valueFormatter = null
            }
            setBackgroundColor(Color.WHITE) // 배경 색상
            description.text = "날짜" // description 글자
            //setDescriptionTextSize(12f) // description 글자 크기
        }

        var depenses : LineDataSet = LineDataSet(entries, "$str")
        depenses.axisDependency = YAxis.AxisDependency.LEFT

        // 날짜 값 변형
        var dates : ArrayList<String> = ArrayList()
        for(i in 0 until exerciseData.size) {
            val year = exerciseData[i].date / 10000
            val month = (exerciseData[i].date % 10000) / 100
            val date = exerciseData[i].date % 100
            dates.add("${year}/${month}/${date}")
        }

        var data_sets : ArrayList<ILineDataSet> = ArrayList()
        data_sets.add(depenses)
        var data : LineData = LineData(data_sets)
        depenses.color = Color.BLACK
        depenses.valueTextSize = 10f
        if(dataList == timeList) { // 시간이면 시간 형태에 맞춰서 표기
            depenses.valueFormatter = TimeFormatter()
        } else {
            depenses.valueFormatter = null
        }
        depenses.setCircleColor(Color.BLACK)

        lineChart.data = data
        lineChart.invalidate()
    }

    inner class TimeFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
            val hour = value.toInt() / 3600
            val min = (value.toInt() % 3600) / 60
            val sec = (value.toInt() % 3600) % 60
            return "$hour:$min:$sec"
        }
    }

    inner class YAxisFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
            val hour = value.toInt() / 3600
            val min = (value.toInt() % 3600) / 60
            val sec = (value.toInt() % 3600) % 60
            return "$hour:$min:$sec"
        }
    }

    inner class DateFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val year = value.toInt() / 10000
            val month = (value.toInt() % 10000) / 100
            val day = (value.toInt() % 10000) % 100
            return "${year}/${month}/${day}"
        }
    }

    private fun loadExerciseData() : ArrayList<ExerciseStatsData> {
        var data = ArrayList<ExerciseStatsData>()

        sqldb = MyDBHelper.readableDatabase
        val dateCursor : Cursor = sqldb.rawQuery("SELECT DISTINCT date FROM exercise_counter " +
                "WHERE date >= $start_date AND date <= $end_date " +
                "ORDER BY date ASC", null)
        exercise_days = dateCursor.count
        if(dateCursor.moveToFirst()) {
            // 날짜와 이름에 해당하는 운동 기록 가져오기
            do {
                val date = dateCursor.getInt(dateCursor.getColumnIndex("date"))
                val nameCursor : Cursor = sqldb.rawQuery("SELECT DISTINCT exercise_name, tag FROM exercise_counter WHERE date = $date", null)

                if(nameCursor.moveToFirst()) {
                    do {
                        val name = nameCursor.getString(nameCursor.getColumnIndex("exercise_name"))
                        val tag = nameCursor.getString(nameCursor.getColumnIndex("tag"))
                        val cursor : Cursor = sqldb.rawQuery("SELECT weight, exercise_count, time " +
                                "FROM exercise_counter " +
                                "WHERE date = $date AND exercise_name = '$name'", null)

                        data.add(ExerciseStatsData(date, name, tag))
                    } while (nameCursor.moveToNext())
                }
            } while(dateCursor.moveToNext())
        }

        sqldb.close()
        return data
    }

    private fun loadExerciseRecord() {
        sqldb = MyDBHelper.readableDatabase
        val cursor : Cursor = sqldb.rawQuery("SELECT * FROM exercise_record " +
                "WHERE date >= $start_date AND date <= $end_date",null)

        if(cursor.moveToFirst()) {
            total_time = 0
            total_steps = 0

            do {
                total_time += cursor.getInt(cursor.getColumnIndex("total_time"))
                total_steps += cursor.getInt(cursor.getColumnIndex("steps"))
            } while (cursor.moveToNext())
        }
    }

    private fun setFeedback(data : ArrayList<ExerciseStatsData>) {
        // 총 운동 시간, 총 걸음 수
        tv_total_time.text = timeToString(total_time)
        tv_total_steps.text = total_steps.toString()

        // 가장 많이한 부위, 가장 적게한 부위
        var parts = Array<Int>(9) {0}
        var partsString = arrayOf("가슴", "어깨", "등", "복근", "팔", "하체", "엉덩이", "전신", "유산소")
        var part = ""
        for(i in 0 until data.size) {
            for (j in data[i].part.indices) {
                if(data[i].part[j] == ',') {
                    when (part) {
                        partsString[0] -> {
                            parts[0]++
                        }
                        partsString[1] -> {
                            parts[1]++
                        }
                        partsString[2] -> {
                            parts[2]++
                        }
                        partsString[3] -> {
                            parts[3]++
                        }
                        partsString[4] -> {
                            parts[4]++
                        }
                        partsString[5] -> {
                            parts[5]++
                        }
                        partsString[6] -> {
                            parts[6]++
                        }
                        partsString[7] -> {
                            parts[7]++
                        }
                        partsString[8] -> {
                            parts[8]++
                        }
                    }
                    part = ""
                } else {
                    part += data[i].part[j]
                }
            }
        }
        var max = parts.indexOf(parts.max())
        var min = parts.indexOf(parts.min())
        tv_max_part.text = partsString[max]
        tv_min_part.text = partsString[min]
        var avg_time = 0

        var days = dateToDays(start_date, end_date)
        try {
            avg_time = total_time/exercise_days
        } catch (ae : ArithmeticException) {
            Toast.makeText(ct, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        var minute = (avg_time / 3600 * 60) + (avg_time % 3600 / 60)
        var sec = avg_time % 3600 % 60

        var feedback = " ${dateToString(start_date)}부터 ${dateToString(end_date)}까지 총 ${days}일간 " +
                "운동은 ${exercise_days}일 했고, 평균 운동 시간은 약 ${minute}분 ${sec}초입니다. " +
                "세계 보건 기구에서는 일주일에 150분 이상 운동할 것을 권장한답니다."
        feedback += when {
            (exercise_days / days * 100) < 42 -> { // 주 당 운동 3회 미만
                "\n 기록에 따르면 평균 주 3회 미만 운동을 하셨어요. 운동하는 날이 너무 적어요ㅠㅠ " +
                        "조금 더 하시면 건강에 훨씬 좋답니다!"
            }
            (exercise_days / days * 100) < 71 -> { // 주 당 운동 5회 미만
                "\n 사용자님은 매주 평균 3회 이상 꾸준히 운동을 하고 계시는군요! 좋은 습관이에요! 시간이 된다면" +
                        "한 주에 5번 운동하는 것이 좋아요."
            }
            else -> {
                "\n 기록에 따르면 사용자님은 운동을 평균 주 5회 이상 하시는군요! 사용자님의 노력과 열정에 " +
                        "박수를 보내고 싶어요 :)"
            }
        }

        feedback += when {
            minute < 30 -> {
                "\n 사용자님의 평균 운동 시간은 하루 권장량 보다 적어요. 하루에 30분 이상은 운동하는 것이 좋으니" +
                        " 조금 더 해보시는 것은 어떨까요?"
            }
            minute < 60 -> {
                "\n 사용자님은 1회당 적절한 시간을 운동에 쓰고 계시네요. 건강한 습관을 응원합니다!"
            }
            else -> {
                "\n 사용자님께서는 운동 시간이 긴편에 속해요. 혹시 운동 후 피로나 통증이 오래 지속된다면 " +
                        "운동 시간이나 운동 강도를 조절하시는 것이 좋을 것 같아요."
            }
        }

        if((parts.min()!! * 2) < parts.max()!!) {
            feedback += "\n 주로 ${partsString[max]} 운동을 하셨는데, 신체의 균형을 위해 ${partsString[min]}도 " +
                    "더 신경 쓰시면 좋을 것 같아요. 균형이 맞으면 몸은 더욱 멋지게 변하니까요 :)"
        }

        tv_feedback.text = feedback

    }

    private fun timeToString(time : Int) : String {
        var hour = time / 3600
        var minute = time % 3600 / 60
        var sec = time % 3600 % 60

        return "${hour}:${minute}:${sec}"
    }

    private fun dateToDays(start : Int, end : Int) : Int {
        var format = SimpleDateFormat("yyyyMMdd")
        var date1 = format.parse(start.toString())
        var date2 = format.parse(end.toString())

        return ((date2.time - date1.time) / (60 * 60 * 24 * 1000)).toInt()
    }

    companion object {

    }
}