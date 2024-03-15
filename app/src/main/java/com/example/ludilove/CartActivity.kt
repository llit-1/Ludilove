package com.example.ludilove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        FullScreenHelper.enableFullScreen(window)

        // Собираем информацию о корзине и передаем в адаптер
        val cartList : RecyclerView = findViewById(R.id.recyclerViewCart)
        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        val getCartInfo : List<Cart> = db.getCartInfo(userLogin?.login.toString())
        val backArrow_item : ImageButton = findViewById(R.id.backArrow_item)
        cartList.layoutManager = LinearLayoutManager(this)
        cartList.adapter = CartAdapter(getCartInfo, this)

        // Просчитываем стоимость всей корзины
        val totalSumOrder : TextView = findViewById(R.id.totalSumOrder)
        var totalSum = 0;
        for(item in getCartInfo) {
            totalSum += item.price * item.count;
        }
        totalSumOrder.text = "К оплате: $totalSum Р"

        // Очистка корзины
        val buttonClear : Button = findViewById(R.id.button_clear)
        buttonClear.setOnClickListener {
            val login = db.get_last_user();
            if (login != null) {
                db.deleteCartItemsByUserLogin(login?.login)
            };
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
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
        cartList.adapter = CartAdapter(getCartInfo, this)

        // Просчитываем стоимость всей корзины
        val totalSumOrder : TextView = findViewById(R.id.totalSumOrder)
        var totalSum = 0;
        for(item in getCartInfo) {
            totalSum += item.price * item.count;
        }
        totalSumOrder.text = "К оплате: $totalSum Р"
    }

}