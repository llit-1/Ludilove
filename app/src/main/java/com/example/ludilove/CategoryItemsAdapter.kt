package com.example.ludilove

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class CategoryItemsAdapter(var categories: List<Category>, var context: Context) :
    RecyclerView.Adapter<CategoryItemsAdapter.MyViewCategoryHolder>(){
    inner class MyViewCategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_listCategory_title)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewCategoryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_category_list, parent, false)
        return MyViewCategoryHolder(view)
    }
    override fun getItemCount(): Int {
        return categories.count()
    }
    override fun onBindViewHolder(holder: MyViewCategoryHolder, position: Int) {
        holder.title.text = categories[position].name
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position * 5)
        }
    }

    // Установка клика для ссылок-якорей
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    private var itemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

}