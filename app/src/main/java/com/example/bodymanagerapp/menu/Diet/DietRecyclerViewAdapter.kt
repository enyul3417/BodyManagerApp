package com.example.bodymanagerapp.menu.Diet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R

class DietRecyclerViewAdapter(var data:ArrayList<DietData>, val context: Context, var item: RecyclerView,
                                  var itemClick:(DietData, Int)->Unit):
    RecyclerView.Adapter<DietRecyclerViewAdapter.ItemViewHolder>() {

    var pos:Int = -1
    var id:Int = 0

    //뷰홀더 클래스 내부 클래스로 선언
    inner class ItemViewHolder(view: View, itemClick: (DietData, Int) -> Unit) : RecyclerView.ViewHolder(view) {

        init{
            view.setOnLongClickListener {
                pos = layoutPosition
                id = data[pos].id
                return@setOnLongClickListener false
            }
            view.setOnCreateContextMenuListener{ menu, v, menuinfo->
                menu.add("삭제")
            }
        }

        var date = view.findViewById<TextView>(R.id.text_diet_time_item)
        var image = view.findViewById<ImageView>(R.id.image_diet_item)
        var memo = view.findViewById<EditText>(R.id.diet_memo_item)

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data: DietData, num: Int) {
            date.text = data.date
            memo.setText(data.memo)

            if (data.image != null){ // 등록된 이미지가 있다면
                image.setImageBitmap(data.image)

            }

            itemView.setOnClickListener {
                itemClick(data, num)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.diet_items, parent, false)
        return ItemViewHolder(view,itemClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
    override fun getItemCount(): Int {
        return data.size
    }

}
