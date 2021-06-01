package com.example.bodymanagerapp.menu.Exercise.Routine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R

class RoutineRVAdapter (var data : ArrayList<RoutineData>, val context: Context,
                        var item : RecyclerView) :
        RecyclerView.Adapter<RoutineRVAdapter.ItemViewHolder>() {

    // 뷰홀더 클래스를 내부 클래스로 선언
    inner class ItemViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.item_exercise_name)
        var weightNumTable = view.findViewById<TableLayout>(R.id.item_table_weight_num)
        var numTable = view.findViewById<TableLayout>(R.id.item_table_num)
        var timeTable = view.findViewById<TableLayout>(R.id.item_table_time)

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data: RoutineData, position: Int) {
            //countTable.removeAllViews()
            name.text = data.exercise_name

            if(data.time!![0] == 0 ) {
                if(data.weightList!![0] == 0f) { // 세트와 횟수만
                    weightNumTable.visibility = View.GONE
                    numTable.visibility = View.VISIBLE
                    timeTable.visibility = View.GONE

                    for(i in 0 until (data.set.size)) {
                        val tableRow = TableRow(context)
                        tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        numTable.addView(tableRow)

                        // 세트 수
                        val setTV = TextView(context)
                        setTV.textSize = 15f // 글자 크기
                        setTV.text = data.set[i].toString()
                        setTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(setTV)
                        // 횟수
                        val numTV = TextView(context)
                        numTV.textSize = 15f // 글자 크기
                        numTV.text = data.exercise_count?.get(i).toString()
                        numTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(numTV)

                        val checkBox = CheckBox(context)
                        checkBox.gravity = 17 // 중앙 정렬
                        tableRow.addView(checkBox)
                    }
                } else { // 세트, 횟수, 무게
                    weightNumTable.visibility = View.VISIBLE
                    numTable.visibility = View.GONE
                    timeTable.visibility = View.GONE

                    for(i in 0 until (data.set.size)) {
                        val tableRow = TableRow(context)
                        tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        weightNumTable.addView(tableRow)

                        // 세트 수
                        val setTV = TextView(context)
                        setTV.textSize = 15f // 글자 크기
                        setTV.text = data.set[i].toString()
                        setTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(setTV)
                        // 무게
                        val weightTV = TextView(context)
                        weightTV.textSize = 15f // 글자 크기
                        weightTV.text = data.weightList?.get(i).toString()
                        weightTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(weightTV)
                        // 횟수
                        val numTV = TextView(context)
                        numTV.textSize = 15f // 글자 크기
                        numTV.text = data.exercise_count?.get(i).toString()
                        numTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(numTV)

                        val checkBox = CheckBox(context)
                        checkBox.gravity = 17 // 중앙 정렬
                        tableRow.addView(checkBox)
                    }
                }
            } else { // 세트, 시간
                weightNumTable.visibility = View.GONE
                numTable.visibility = View.GONE
                timeTable.visibility = View.VISIBLE

                for(i in 0 until (data.set.size)) {
                    val tableRow = TableRow(context)
                    tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    timeTable.addView(tableRow)

                    // 세트 수
                    val setTV = TextView(context)
                    setTV.textSize = 15f // 글자 크기
                    setTV.text = data.set[i].toString()
                    setTV.gravity = 17 // 중앙 정렬
                    tableRow.addView(setTV)

                    val timeTV = TextView(context)
                    timeTV.textSize = 15f // 글자 크기
                    val hour = (data.time?.get(i) / 3600)
                    val min = (data.time?.get(i) % 3600) / 60
                    val sec = (data.time?.get(i) % 3600) % 60
                    //timeTV.text = data.time?.get(i-1).toString()
                    timeTV.text = "${hour}:${min}:${sec}"
                    timeTV.gravity = 17 // 중앙 정렬
                    tableRow.addView(timeTV)

                    val checkBox = CheckBox(context)
                    checkBox.gravity = 17 // 중앙 정렬
                    tableRow.addView(checkBox)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineRVAdapter.ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.exercise_items, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RoutineRVAdapter.ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
}