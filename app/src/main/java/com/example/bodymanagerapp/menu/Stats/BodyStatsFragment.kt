package com.example.bodymanagerapp.menu.Stats

import android.app.DatePickerDialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.myDBHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*
import kotlin.collections.ArrayList

// 수정해야할 것
// 날짜 범위 조절 필요


class BodyStatsFragment : Fragment() {
    // DB
    lateinit var myDBHelper: myDBHelper
    lateinit var sqldb: SQLiteDatabase

    lateinit var tv_start_date : TextView // 시작 날짜
    lateinit var tv_end_date : TextView // 끝 날짜
    lateinit var btn_7days : Button // 최근 7일
    lateinit var btn_1month : Button // 최근 한 달
    lateinit var btn_3month : Button // 최근 3개월
    lateinit var btn_1year : Button // 최근 1년
    lateinit var btn_height : Button // 키
    lateinit var btn_weight : Button // 몸무게
    lateinit var btn_muscle : Button // 골격근량
    lateinit var btn_fat : Button // 체지방량
    lateinit var btn_body_img : Button // 눈바디

    var start_date : Int= 0
    var end_date : Int= 0
    var now_date : Int= 0

    // 차트에 사용할 ArrayList
    var date_list = ArrayList<Int>()
    var data_list = ArrayList<Float>()
    var img_list = ArrayList<Bitmap>()

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

        var calendar : Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DATE)
        /*var ymd = "$year"
        ymd += if(month < 10)
            "0${month+1}"
        else "${month+1}"
        ymd += if(date < 10)
            "0$date"
        else "$date"*/
        now_date = dateToInt(year, month, date)

        tv_start_date = view.findViewById(R.id.tv_sb_start_date)
        tv_end_date = view.findViewById(R.id.tv_sb_end_date)
        btn_7days = view.findViewById(R.id.button_sb_7days)
        btn_1month = view.findViewById(R.id.button_sb_1month)
        btn_3month = view.findViewById(R.id.button_sb_3months)
        btn_1year = view.findViewById(R.id.button_sb_1year)
        btn_height = view.findViewById(R.id.button_sb_height)
        btn_weight = view.findViewById(R.id.button_sb_weight)
        btn_muscle = view.findViewById(R.id.button_sb_muscle)
        btn_fat = view.findViewById(R.id.button_sb_fat)
        btn_body_img = view.findViewById(R.id.button_sb_img)

        // 시작 날짜 선택
        tv_start_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                start_date = dateToInt(y, m, d)
                tv_start_date.text = "${y}년 ${m+1}월 ${d}일"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show()
        }

        tv_end_date.setOnClickListener {
            DatePickerDialog(ct, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                end_date = dateToInt(y, m, d)
                tv_end_date.text = "${y}년 ${m+1}월 ${d}일"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show()
        }

        btn_7days.setOnClickListener {
            start_date = dateToInt(year, month, date - 7)
            tv_start_date.text = "${year}년 ${month}월 ${(date - 7)}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"
        }

        btn_1month.setOnClickListener {
            start_date = dateToInt(year, month - 1, date + 1)
            tv_start_date.text = "${year}년 ${month - 1}월 ${(date + 1)}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"
        }

        btn_3month.setOnClickListener {
            start_date = dateToInt(year, month - 3, date + 1)
            tv_start_date.text = "${year}년 ${month - 3}월 ${(date + 1)}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"
        }

        btn_1year.setOnClickListener {
            start_date = dateToInt(year - 1, month, date + 1)
            tv_start_date.text = "${year - 1}년 ${month}월 ${(date + 1)}일"
            end_date = dateToInt(year, month, date)
            tv_end_date.text = "${year}년 ${month}월 ${date}일"
        }

        btn_height.setOnClickListener {
            loadData("height")
            lineChartGraph(view, data_list, date_list)
        }

        btn_weight.setOnClickListener {
            loadData("weight")
            lineChartGraph(view, data_list, date_list)
        }

        btn_muscle.setOnClickListener {
            loadData("muscle_mass")
            lineChartGraph(view, data_list, date_list)
        }

        btn_fat.setOnClickListener {
            loadData("fat_mass")
            lineChartGraph(view, data_list, date_list)
        }

        btn_body_img.setOnClickListener {
            loadData("body_photo")

        }

        return view
    }

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

    private fun loadData(str : String) {
        data_list.clear()
        date_list.clear()
        img_list.clear()
        sqldb = myDBHelper.readableDatabase
        var cursor : Cursor = sqldb.rawQuery("SELECT $str, date FROM body_record WHERE date >= $start_date AND date <= $end_date", null)

        if(cursor.moveToFirst()) {
            do {
                if (str == "body_photo") {
                    var bitmap = try {
                        val image : ByteArray ?= cursor.getBlob(cursor.getColumnIndex(str))
                        BitmapFactory.decodeByteArray(image, 0, image!!.size)
                    } catch (rte: RuntimeException) { // 이미지가 없을 경우
                        null
                    }
                    img_list.add(bitmap!!)
                } else {
                    data_list.add(cursor.getFloat(cursor.getColumnIndex("$str")))
                }
                date_list.add(cursor.getInt(cursor.getColumnIndex("date")))
            } while (cursor.moveToNext())
        }
    }

    // 이미지를 제외한 값들의 그래프
    private fun lineChartGraph(view : View,  data_list : ArrayList<Float>, date_list : ArrayList<Int> ) {
        var lineChart : LineChart = view.findViewById(R.id.sb_chart)

        var entries : ArrayList<Entry> = ArrayList()
        for(i in 0 until data_list.size) {
            entries.add(Entry(data_list[i], i))
        }

        var depenses : LineDataSet = LineDataSet(entries, "# of Calls")
        depenses.axisDependency = YAxis.AxisDependency.LEFT

        var dates : ArrayList<String> = ArrayList()
        for(i in 0 until date_list.size) {
            dates.add(date_list[i].toString())
        }

        var data_sets : ArrayList<ILineDataSet> = ArrayList()
        data_sets.add(depenses)
        var data : LineData = LineData(dates, data_sets)
        depenses.setColors(ColorTemplate.COLORFUL_COLORS)

        lineChart.data = data
        lineChart.animateXY(1000, 1000)
        lineChart.invalidate()
    }

    companion object {

    }

}