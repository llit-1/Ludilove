package com.example.ludilove

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.coroutineContext

class OrdersAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_orders, parent, false)
        return OrderViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val orderNumberTextView: TextView = itemView.findViewById(R.id.order_number)
        private val orderDateTextView: TextView = itemView.findViewById(R.id.order_date)
        private val orderContentRecyclerView: RecyclerView = itemView.findViewById(R.id.order_content)
        private val orderPriceTextView: TextView = itemView.findViewById(R.id.order_price)
        private val orderStatusColorTextView: TextView = itemView.findViewById(R.id.order_status_color_text)

        @SuppressLint("SimpleDateFormat", "ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(order: Order) {
            orderNumberTextView.text = "Заказ #${order.orderId}"
            orderDateTextView.text = order.creationDate
            orderPriceTextView.text = "Стоимость заказа: ${order.sum} р."
            orderStatusColorTextView.text = "${order.status}"

            val yellow = ContextCompat.getColor(itemView.context, R.color.yellow)
            val gray = ContextCompat.getColor(itemView.context, R.color.dark_gray)
            val green = ContextCompat.getColor(itemView.context, R.color.green)
            val red = ContextCompat.getColor(itemView.context, R.color.red)

            if(order.status == "Новый") { orderStatusColorTextView.setTextColor(yellow)}
            if(order.status == "Выдан") { orderStatusColorTextView.setTextColor(gray) }
            if(order.status == "Готов к выдаче") { orderStatusColorTextView.setTextColor(green) }
            if(order.status == "Отменён") {
                val cardWrapper : ConstraintLayout = itemView.findViewById(R.id.cardWrapper)
                cardWrapper.setBackgroundResource(R.drawable.rounded_background_for_card_grey)
                orderStatusColorTextView.setTextColor(red)
            }
            if(order.status == "Выдан") {
                val cardWrapper : ConstraintLayout = itemView.findViewById(R.id.cardWrapper)
                cardWrapper.setBackgroundResource(R.drawable.rounded_background_for_card_grey)
                orderStatusColorTextView.setTextColor(gray)
            }

            // Настройка RecyclerView для списка товаров в заказе
            orderContentRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            orderContentRecyclerView.adapter = OrderItemAdapter(order.items)
        }
    }
}
