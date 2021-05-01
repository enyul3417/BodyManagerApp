package com.example.bodymanagerapp.menu.Stats.Exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.bodymanagerapp.R
import com.github.mikephil.charting.charts.LineChart


class ExericseStatsFragment : Fragment() {

    lateinit var tv_start_date : TextView // 시작 날짜
    lateinit var tv_end_date : TextView // 끝 날짜
    lateinit var btn_7days : Button // 최근 7일
    lateinit var btn_1month : Button // 최근 한 달
    lateinit var btn_3month : Button // 최근 3개월
    lateinit var btn_1year : Button // 최근 1년
    lateinit var spinner : Spinner
    lateinit var btn_max_weight : Button
    lateinit var btn_volume : Button
    lateinit var btn_time : Button
    lateinit var lineChart : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exericse_stats, container, false)

        return view
    }

    companion object {

    }
}