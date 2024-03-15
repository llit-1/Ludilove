package com.example.ludilove
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(var carts: List<Cart>, var context : Context) : RecyclerView.Adapter<CartAdapter.myViewHolder>() {
    class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_cart_image)
        val title: TextView = view.findViewById(R.id.item_cart_title)
        val count: TextView = view.findViewById(R.id.item_cart_count)
        val price: TextView = view.findViewById(R.id.item_cart_price)
        val delete: ImageButton = view.findViewById(R.id.deleteItemButton)
        val decrItem: ImageButton = view.findViewById(R.id.decreaseButton)
        val incrItem: ImageButton = view.findViewById(R.id.increaseButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.myViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_cart, parent, false)
        return myViewHolder(view)
    }

    override fun getItemCount(): Int {
        return carts.count()
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        println(carts[position].name)
        holder.title.text = carts[position].name
        holder.price.text = "${carts[position].price * carts[position].count} Р"
        holder.count.text = "${carts[position].count} Шт.";

        // Загрузка изображения с помощью Glide
        Glide.with(context)
            .asBitmap()
            .load(carts[position].image)
            .into(holder.image)

        holder.delete.setOnClickListener {
            val db = DbHelper(context, null)
            val user = db.get_last_user();
            db.deleteCartItem(user?.login.toString(), carts[position].name)
            val intent = Intent(context, CartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            context.startActivity(intent)
        }

        holder.incrItem.setOnClickListener {
            holder.decrItem.isEnabled = true
            val db = DbHelper(context, null)
            val user = db.get_last_user();
            db.incrOrDecrItemsInCart(carts[position].name, user?.login.toString(), "incr")
            println("+")
            val intent = Intent(context, CartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            context.startActivity(intent)
        }

        holder.decrItem.setOnClickListener {
            println("${holder.count.text}")
            if(holder.count.text == "2 Шт.") {
                holder.decrItem.alpha = 0.3F
                holder.decrItem.isEnabled = false
            }
            else {
                holder.decrItem.alpha = 1F
                holder.decrItem.isEnabled = true
            }
            val db = DbHelper(context, null)
            val user = db.get_last_user();
            db.incrOrDecrItemsInCart(carts[position].name, user?.login.toString(), "decr")
            val intent = Intent(context, CartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            context.startActivity(intent)
        }

        if(holder.count.text == "1 Шт.") {
            holder.decrItem.alpha = 0.3F
            holder.decrItem.isEnabled = false
        }
        else {
            holder.decrItem.alpha = 1F
            holder.decrItem.isEnabled = true
        }
    }
}