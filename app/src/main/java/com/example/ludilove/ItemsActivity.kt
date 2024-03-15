package com.example.ludilove

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson


class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        // Полноэкранный режим
        FullScreenHelper.enableFullScreen(window)

        // Получаем RecyclerView для товаров
        val itemsList : RecyclerView = findViewById(R.id.itemsList)
        // Создаем массив для заполнения товаров
        val items = arrayListOf<Item>()
        // Получаем RecyclerView для категорий
        val horizontalItemsList : RecyclerView = findViewById(R.id.horizontalItemsList)
        // Создаем массив для заполнения категорий
        val categoryItem = arrayListOf<Category>()



        fun requestData(username: String, password: String) {
            val progressBar : ProgressBar = findViewById(R.id.progressBar)
            progressBar.visibility = View.VISIBLE
            val url = "https://api.ludilove.ru/deliveryclub/menus/"
            val queue = Volley.newRequestQueue(this)
            val request = object : StringRequest(
                Method.GET,
                url,
                { response ->
                    val gson = Gson()
                    val menuResponse = gson.fromJson(response, JsonData.Categories::class.java)
                    val products = menuResponse.menuItems.products;
                    val categories = menuResponse.menuItems.categories;
                    for(product in products) {
                        items.add(Item(product.id.toInt(), product.imageUrl, product.name, product.description, product.description, product.price))
                    }
                    for(category in categories) {
                        categoryItem.add(Category(category.id.toInt(),category.parentId,category.name))
                    }

                    itemsList.layoutManager = GridLayoutManager(this, 2)
                    itemsList.adapter = ItemsAdapter(items, this, intent)

                    // Горизонтальная менюшка
                    horizontalItemsList.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
                    horizontalItemsList.adapter = CategoryItemsAdapter(categoryItem, this).apply {
                        setOnItemClickListener(object : CategoryItemsAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                // Обработка нажатия на элемент списка
                                println("Было нажатие на position ${position}")
                                // Прокрутить RecyclerView к позиции itemPosition с отступом сверху
                                val smoothScroller: SmoothScroller =
                                    object : LinearSmoothScroller(context) {
                                        override fun getVerticalSnapPreference(): Int {
                                            return SNAP_TO_START
                                        }
                                    }
                                smoothScroller.setTargetPosition(position);
                                (itemsList.layoutManager as GridLayoutManager).startSmoothScroll(smoothScroller);

                            }
                        })
                    }


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
            progressBar.visibility = View.INVISIBLE
        }

        horizontalItemsList.setOnClickListener { view ->
            // Получаем позицию нажатого элемента
            val position = horizontalItemsList.getChildAdapterPosition(view)
            itemsList.smoothScrollToPosition(position)
        }

        val buttonCart : Button = findViewById(R.id.cartCount)

        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        val result = db.getCartCount(userLogin?.login)
        buttonCart.text = result;
        buttonCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        requestData("powerbi","'ythubz-'ythubz=59")

        val exit : ImageView = findViewById(R.id.backArrow_item_exit)
        exit.setOnClickListener {
            @Suppress("NAME_SHADOWING") val userLogin = db.get_last_user()
            showConfirmationDialog(userLogin?.login)
        }
    }

    override fun onResume() {
        super.onResume()

        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        val result = db.getCartCount(userLogin?.login)
        val buttonCart : Button = findViewById(R.id.cartCount)
        buttonCart.text = result;
    }

    // Вызов окна подтверждения (да\нет)
    private fun showConfirmationDialog(login : String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Подтверждение")
        builder.setMessage("Вы уверены, что хотите выйти?")

        builder.setPositiveButton("Отмена") { dialog, which ->

        }

        builder.setNegativeButton("Да") { dialog, which ->
            val db = DbHelper(this, null)
            if (login != null) {
                db.change_last_user(login, 0)
            }
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val dialog = builder.create()
        dialog.show()
    }
}