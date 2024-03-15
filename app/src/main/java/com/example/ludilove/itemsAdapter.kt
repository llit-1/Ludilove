@file:Suppress("DEPRECATION")

package com.example.ludilove

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class ItemsAdapter(
    var items: List<Item>,
    var context: Context,
    val intent: Intent
) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_list_image)
        val title: TextView = view.findViewById(R.id.item_list_title)
        val price: Button = view.findViewById(R.id.item_list_price)

        // Кнопка для подробной карточки товара
        val btnCard : ConstraintLayout = view.findViewById(R.id.cardWrapper)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db = DbHelper(context, null)
        val lastUser = db.get_last_user()
        val cartItems = db.getCartInfo(lastUser?.login.toString())

        val isItemExists = cartItems.any { it.name == items[position].title }
        println(items[position].title )
        println(isItemExists)
        if(isItemExists) {
            val count = db.getCartItemQuantity(lastUser?.login.toString(), items[position].title)
            holder.price.text = "В корзине"
        } else {
            holder.price.text = "${items[position].price} Р"
        }

        holder.title.text = items[position].title


        // Загрузка изображения с помощью Glide
        Glide.with(context)
            .asBitmap()
            .load(items[position].image)
            .into(holder.image)

        holder.btnCard.setOnClickListener {
            val intent = Intent(context, ItemActivity::class.java)
            intent.putExtra("ItemTitle", items[position].title)
            intent.putExtra("ItemText", items[position].text)
            intent.putExtra("ItemPrice", items[position].price.toString())
            intent.putExtra("ItemImage", items[position].image)
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
