package com.example.bodymanagerapp.menu.Exercise.Routine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Exercise.ExerciseData
import com.example.bodymanagerapp.menu.Stats.Body.BodyImageRecyclerViewAdapter

class SavedRoutineRVAdapter (var data : ArrayList<RoutineData>, val context: Context,
                             var item : RecyclerView) :
        RecyclerView.Adapter<SavedRoutineRVAdapter.ItemViewHolder>() {

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
                        numTV.text = data.exercise_count?.get(i-1).toString()
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
                        weightTV.text = data.weightList?.get(i-1).toString()
                        weightTV.gravity = 17 // 중앙 정렬
                        tableRow.addView(weightTV)
                        // 횟수
                        val numTV = TextView(context)
                        numTV.textSize = 15f // 글자 크기
                        numTV.text = data.exercise_count?.get(i-1).toString()
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
                    /*val linearLayout = LinearLayout(context)
                    linearLayout.gravity = 17 // 중앙 정렬
                    val hourTV = TextView(context) // 시
                    hourTV.textSize = 15f // 글자 크기
                    hourTV.text = ((data.time?.get(i-1)) / 3600).toString()
                    linearLayout.addView(hourTV)*/

                    val timeTV = TextView(context)
                    timeTV.textSize = 15f // 글자 크기
                    val hour = (data.time?.get(i-1) / 3600)
                    val min = (data.time?.get(i-1) % 3600) / 60
                    val sec = (data.time?.get(i-1) % 3600) % 60
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRoutineRVAdapter.ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.exericse_items, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SavedRoutineRVAdapter.ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
}