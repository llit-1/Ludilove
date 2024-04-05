package com.example.ludilove

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class OldOrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_orders)
        val helper_for_logo : TextView = findViewById(R.id.helper_for_logo)
        helper_for_logo.setText("Завершенные заказы")

        val orderList : RecyclerView = findViewById(R.id.horizontalItemsList)
        val db = DbHelper(this, null)
        val user = db.get_last_user()
        val max = 4;
        val min = 2


        fun getOrdersFromAPI(id : Int, min: Int, max: Int) {
            val url = "https://appapi.ludilove.ru/api/order/$id, $min, $max"
            val queue = Volley.newRequestQueue(this)
            val request = object : StringRequest(
                Method.GET,
                url,
                { response ->
                    val orderResponse = Gson().fromJson(response, OrderResponse::class.java)
                    val button_for_old_orders : ImageButton = findViewById(R.id.button_for_old_orders)
                    button_for_old_orders.setImageDrawable(null)
                    val backArrow_item : ImageButton = findViewById(R.id.backArrow_item)
                    backArrow_item.setOnClickListener {
                        val intent = Intent(this, OrdersActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                    if(orderResponse.responseOrders.isNotEmpty()) {
                        val sad_icon : ImageView = findViewById(R.id.sad_icon)
                        val sad_message : TextView = findViewById(R.id.sad_message)

                        sad_icon.visibility = View.INVISIBLE
                        sad_message.visibility = View.INVISIBLE



                        orderList.visibility = View.VISIBLE
                        orderList.layoutManager = LinearLayoutManager(this)
                        orderList.adapter = OrdersAdapter(orderResponse.responseOrders)
                    }


                },
                { error ->
                    println(error)
                }) {

            }
            queue.add(request)
        }

        if (user != null) {
            getOrdersFromAPI(user.id, min, max)
        }

        val backArrow_item : ImageButton = findViewById(R.id.backArrow_item)
        backArrow_item.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


    }
}