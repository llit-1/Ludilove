@file:Suppress("DEPRECATION")

package com.example.ludilove

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ItemsAdapter(
    private var items: List<ResponseItem>, // Список всех элементов для отображения
    private val context: Context // Контекст приложения
) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    // Внутренний класс ViewHolder для удержания представлений элемента списка
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_list_image) // Изображение товара
        val title: TextView = view.findViewById(R.id.item_list_title) // Название товара
        val price: Button = view.findViewById(R.id.item_list_price) // Цена товара

        // Кнопка для подробной карточки товара
        val btnCard: ConstraintLayout = view.findViewById(R.id.cardWrapper)
    }

    // Создание нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    // Получение общего количества элементов в списке
    override fun getItemCount(): Int {
        return items.size
    }

    // Привязка данных к элементу списка
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db = DbHelper(context, null)
        val lastUser = db.get_last_user()
        val cartItems = db.getCartInfo(lastUser?.login.toString())

        // Проверка, есть ли товар в корзине
        val isItemExists = cartItems.any { it.name == items[position].name }
        if (isItemExists) {
            holder.price.text = "В корзине"
        } else {
            holder.price.text = "${items[position].price} Р"
        }

        // Установка названия товара
        holder.title.text = items[position].name

        // Загрузка изображения товара с помощью Glide
        Glide.with(context)
            .asBitmap()
            .load(items[position].image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .into(holder.image)

        // Если цена товара неизвестна, установка статуса "Недоступно"
        if (holder.price.text == "null Р") {
            holder.price.background.alpha = 80;
            holder.price.text = "Недоступно"
        } else {
            // Установка слушателя кликов на кнопку для открытия детальной информации о товаре
            holder.btnCard.setOnClickListener {
                val intent = Intent(context, ItemActivity::class.java)
                intent.putExtra("ItemId", items[position].id.toString())
                intent.putExtra("ItemTitle", items[position].name)
                intent.putExtra("ItemText", items[position].description)
                intent.putExtra("ItemPrice", items[position].price.toString())
                intent.putExtra("ItemImage", items[position].image)
                context.startActivity(intent)
            }
        }
    }

    // Метод для обновления списка товаров
    fun updateItems(newItems: List<ResponseItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
