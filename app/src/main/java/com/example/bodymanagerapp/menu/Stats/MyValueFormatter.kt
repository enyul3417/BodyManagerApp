package com.example.bodymanagerapp.menu.Stats

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class MyValueFormatter : ValueFormatter() {
    private lateinit var mFormat : DecimalFormat
    fun MyValueFormatter() {
        mFormat = DecimalFormat("###,###,##0.00") // use one decimal
    }
    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
       return mFormat.format(value)
    }

}