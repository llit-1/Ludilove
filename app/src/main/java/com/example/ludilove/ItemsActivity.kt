package com.example.ludilove

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.util.Base64
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.google.gson.Gson

class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        FullScreenHelper.enableFullScreen(window)

        val itemsList : RecyclerView = findViewById(R.id.itemsList)
        val items = arrayListOf<Item>()



        fun requestData(restId: Int, username: String, password: String) {
            val url = "https://api.ludilove.ru/deliveryclub/menus/$restId"
            val queue = Volley.newRequestQueue(this)
            val request = object : StringRequest(
                Method.GET,
                url,
                { response ->
                    val gson = Gson()
                    val menuResponse = gson.fromJson(response, JsonData.MenuResponse::class.java)
                    val products = menuResponse.menuItems.products
                    for(product in products) {
                        items.add(Item(product.id.toInt(), product.imageUrl, product.name, product.description, product.description, product.price))
                    }

                    itemsList.layoutManager = GridLayoutManager(this, 2)
                    itemsList.adapter = ItemsAdapter(items, this)
                },
                { error ->
                    // Обработка ошибки
                    println(error)
                }) {

                // Переопределение метода для установки заголовка авторизации
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val auth = "$username:$password"
                    val encodedAuth = Base64.encodeToString(auth.toByteArray(), Base64.DEFAULT)
                    val authHeaderValue = "Basic $encodedAuth"
                    headers["Authorization"] = authHeaderValue
                    return headers
                }
            }
            queue.add(request)
        }

        requestData(13, "powerbi","'ythubz-'ythubz=59")

    }
}