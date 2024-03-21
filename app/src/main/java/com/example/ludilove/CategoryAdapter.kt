package com.example.ludilove

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Адаптер для списка категорий
class CategoryAdapter(private val categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.textview_for_category)
        private val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_for_category)
        private lateinit var itemsAdapter: ItemsAdapter

        init {
            itemsRecyclerView.layoutManager = GridLayoutManager(itemView.context, 2)
        }

        fun bind(category: Category) {
            categoryNameTextView.text = category.categoryName

            if (!::itemsAdapter.isInitialized) {
                itemsAdapter = ItemsAdapter(category.responseItems, itemView.context)
                itemsRecyclerView.adapter = itemsAdapter
            } else {
                // Просто обновляем данные в адаптере
                itemsAdapter.updateItems(category.responseItems)
            }
        }
    }
}
