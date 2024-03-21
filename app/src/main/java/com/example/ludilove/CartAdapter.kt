package com.example.ludilove

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private val context: Context, private val activity: CartActivity) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val db = DbHelper(context, null)
    private val userLogin = db.get_last_user()
    private val getCartInfo : List<Cart> = db.getCartInfo(userLogin?.login.toString())
    private var carts: List<Cart> = getCartInfo




    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_cart_image)
        val title: TextView = itemView.findViewById(R.id.item_cart_title)
        val count: TextView = itemView.findViewById(R.id.item_cart_count)
        val price: TextView = itemView.findViewById(R.id.item_cart_price)
        val delete: ImageButton = itemView.findViewById(R.id.deleteItemButton)
        val decrItem: ImageButton = itemView.findViewById(R.id.decreaseButton)
        val incrItem: ImageButton = itemView.findViewById(R.id.increaseButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_cart, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val cartItem = carts[position]
        holder.title.text = cartItem.name
        holder.price.text = "${cartItem.price * cartItem.count} Р"
        holder.count.text = "${cartItem.count} Шт."

        Glide.with(context)
            .asBitmap()
            .load(cartItem.image)
            .into(holder.image)

        holder.delete.setOnClickListener {
            val db = DbHelper(context, null)
            val user = db.get_last_user()
            db.deleteCartItem(user?.login.toString(), cartItem.name)
            notifyDataSetChanged()
            updateCart()
            activity.calcTotalSumInCart()
        }

        holder.incrItem.setOnClickListener {
            val db = DbHelper(context, null)
            val user = db.get_last_user()
            db.incrOrDecrItemsInCart(cartItem.name, user?.login.toString(), "incr")
            updateCart()
            activity.calcTotalSumInCart()
        }

        holder.decrItem.setOnClickListener {
            val db = DbHelper(context, null)
            val user = db.get_last_user()
            db.incrOrDecrItemsInCart(cartItem.name, user?.login.toString(), "decr")
            updateCart()
            activity.calcTotalSumInCart()
        }

        if (cartItem.count == 1) {
            holder.decrItem.alpha = 0.3F
            holder.decrItem.isEnabled = false
        } else {
            holder.decrItem.alpha = 1F
            holder.decrItem.isEnabled = true
        }
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setCartItems(items: List<Cart>) {
        carts = items
        notifyDataSetChanged()
    }

    private fun updateCart() {
        val db = DbHelper(context, null)
        val user = db.get_last_user()
        val cartItems = db.getCartInfo(user?.login.toString())
        setCartItems(cartItems)
    }
}
