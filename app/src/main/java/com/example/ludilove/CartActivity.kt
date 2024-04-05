package com.example.ludilove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.nio.charset.Charset

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

//        FullScreenHelper.enableFullScreen(window)

        // Собираем информацию о корзине и передаем в адаптер
        val cartList : RecyclerView = findViewById(R.id.recyclerViewCart)
        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        val getCartInfo : List<Cart> = db.getCartInfo(userLogin?.login.toString())
        val backArrow_item : ImageButton = findViewById(R.id.backArrow_item)
        cartList.layoutManager = LinearLayoutManager(this)
        cartList.adapter = CartAdapter( this, this)

        // Просчитываем стоимость всей корзины
        val totalSumOrder : TextView = findViewById(R.id.totalSumOrder)
        var totalSum = 0;
        for(item in getCartInfo) {
            totalSum += item.price * item.count;
        }
        totalSumOrder.text = "К оплате: $totalSum Р"

        val order_button : Button = findViewById(R.id.order_button)
        order_button.setOnClickListener {
            val login = db.get_last_user();
            if (login != null) {
                val dataFromCart: List<Cart> = db.getCartInfo(userLogin?.login.toString())
                val ordersList = dataFromCart.map {
                    OrderFromApp(
                        ItemId = it.id,
                        Quantity = it.count,
                        UserId = login.id
                    )
                }
                val gson = Gson()
                val json = gson.toJson(ordersList)
                var escapedJson = json.replace("\"", "\\\"")
                escapedJson = "\"" + escapedJson + "\""
                val url = "https://appapi.ludilove.ru/api/order"
                val queue = Volley.newRequestQueue(this)
                val stringRequest = object : StringRequest(
                    Method.POST,
                    url,
                    Response.Listener { response ->
                        db.deleteCartItemsByUserLogin(login.login)
                        val dialogFragment = Modal()
                        val args = Bundle()
                        args.putBoolean("State", true) // Передача вашей переменной
                        dialogFragment.arguments = args
                        dialogFragment.show(supportFragmentManager, "MyDialogFragment")
                    },
                    Response.ErrorListener { error ->
                        val dialogFragment = Modal()
                        val args = Bundle()
                        args.putBoolean("State", false) // Передача вашей переменной
                        dialogFragment.arguments = args
                        dialogFragment.show(supportFragmentManager, "MyDialogFragment")
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json"
                    }

                    override fun getBody(): ByteArray {
                        return escapedJson.toByteArray(Charset.defaultCharset())
                    }
                }

                queue.add(stringRequest)
            }
        }

        // Очистка корзины
        val buttonClear : Button = findViewById(R.id.button_clear)
        buttonClear.setOnClickListener {
            val login = db.get_last_user();
            if (login != null) {
                db.deleteCartItemsByUserLogin(login?.login)
            };
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }

        backArrow_item.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            this.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val cartList : RecyclerView = findViewById(R.id.recyclerViewCart)
        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        val getCartInfo : List<Cart> = db.getCartInfo(userLogin?.login ?: "")
        cartList.layoutManager = LinearLayoutManager(this)
        cartList.adapter = CartAdapter(this, this)
    }

    fun calcTotalSumInCart(){
        // Просчитываем стоимость всей корзины
        val totalSumOrder : TextView = findViewById(R.id.totalSumOrder)
        var totalSum = 0;
        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        val getCartInfo : List<Cart> = db.getCartInfo(userLogin?.login ?: "")
        for(item in getCartInfo) {
            totalSum += item.price * item.count;
        }
        totalSumOrder.text = "К оплате: $totalSum Р"
    }
}