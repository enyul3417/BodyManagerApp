package com.example.bodymanagerapp.menu.Stats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R

class BodyImageRecyclerViewAdapter(var data : ArrayList<BodyImageData>, val context: Context,
                                   var item : RecyclerView) :
        RecyclerView.Adapter<BodyImageRecyclerViewAdapter.ItemViewHolder>() {

    // 뷰홀더 클래스를 내부 클래스로 선언
    inner class ItemViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var date = view.findViewById<TextView>(R.id.tv_sb_img_date)
        var image = view.findViewById<ImageView>(R.id.img_sb)

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data : BodyImageData, num : Int) {
            date.text = data.date
            image.setImageBitmap(data.img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyImageRecyclerViewAdapter.ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.body_img_items, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BodyImageRecyclerViewAdapter.ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
}