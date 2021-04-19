package com.example.bodymanagerapp.menu.Exercise

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.myDBHelper

class ExerciseRecyclerViewAdapter(var data : ArrayList<ExerciseData>, val context: Context,
                                  var item : RecyclerView):
    RecyclerView.Adapter<ExerciseRecyclerViewAdapter.ItemViewHolder>() {

    var myDBHelper: myDBHelper = myDBHelper(context)
    lateinit var sqldb: SQLiteDatabase

    //뷰홀더 클래스 내부 클래스로 선언
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.item_exercise_name)
        var weightNumTable = view.findViewById<TableLayout>(R.id.item_table_weight_num)
        var numTable = view.findViewById<TableLayout>(R.id.item_table_num)
        var timeTable = view.findViewById<TableLayout>(R.id.item_table_time)
        var countTable = view.findViewById<TableLayout>(R.id.item_table_exercise_count)

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data: ExerciseData, position: Int) {
            //countTable.removeAllViews()
            name.text = data.name

            if(data.time!![0] == "null" ) {
                if(data.weight!![0] == 0) { // 세트와 횟수만
                   weightNumTable.visibility = View.GONE
                    numTable.visibility = View.VISIBLE
                    timeTable.visibility = View.GONE

                    for(i in 1..(data.set.size)) {
                        val tableRow = TableRow(context)
                        tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        numTable.addView(tableRow)

                        // 세트 수
                        val setTV = TextView(context)
                        setTV.textSize = 15f // 글자 크기
                        setTV.text = data.set[i-1].toString()
                        setTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(setTV)
                        // 횟수
                        val numTV = TextView(context)
                        numTV.textSize = 15f // 글자 크기
                        numTV.text = data.num?.get(i-1).toString()
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

                    for(i in 1..(data.set.size)) {
                        val tableRow = TableRow(context)
                        tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        weightNumTable.addView(tableRow)

                        // 세트 수
                        val setTV = TextView(context)
                        setTV.textSize = 15f // 글자 크기
                        setTV.text = data.set[i-1].toString()
                        setTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(setTV)
                        // 무게
                        val weightTV = TextView(context)
                        weightTV.textSize = 15f // 글자 크기
                        weightTV.text = data.weight?.get(i-1).toString()
                        weightTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(weightTV)
                        // 횟수
                        val numTV = TextView(context)
                        numTV.textSize = 15f // 글자 크기
                        numTV.text = data.num?.get(i-1).toString()
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

                for(i in 1..(data.set.size)) {
                    val tableRow = TableRow(context)
                    tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    timeTable.addView(tableRow)

                    // 세트 수
                    val setTV = TextView(context)
                    setTV.textSize = 15f // 글자 크기
                    setTV.text = data.set[i-1].toString()
                    setTV.gravity = 17 // 중앙 정렬
                    tableRow.addView(setTV)
                    // 시간
                    val timeTV = TextView(context)
                    timeTV.textSize = 15f // 글자 크기
                    timeTV.text = data.time?.get(i-1).toString()
                    timeTV.gravity = 17 // 중앙 정렬
                    tableRow.addView(timeTV)

                    val checkBox = CheckBox(context)
                    checkBox.gravity = 17 // 중앙 정렬
                    tableRow.addView(checkBox)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.exericse_items, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
}