package com.example.ludilove

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Scroller
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.AppBarLayout.LayoutParams.ScrollFlags
import com.google.gson.Gson


@Suppress("NAME_SHADOWING")
class ItemsActivity : AppCompatActivity() {

    fun paintingItems(response: String) {
        val gson = Gson()
        val menuResponse = gson.fromJson(response, JsonData::class.java)
        val itemsList : RecyclerView = findViewById(R.id.itemsList)
        val horizontalItemsList : RecyclerView = findViewById(R.id.horizontalItemsList)

        val layoutManager = GridLayoutManager(this, 1)
        itemsList.layoutManager = layoutManager
        val categoryAdapter = CategoryAdapter(menuResponse.categories)
        itemsList.adapter = categoryAdapter

        // Горизонтальная менюшка
        horizontalItemsList.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        horizontalItemsList.adapter = CategoryItemsAdapter(menuResponse.categories, this).apply {

            setOnItemClickListener(object : CategoryItemsAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val layoutManager = itemsList.layoutManager as GridLayoutManager
                    val currentPosition = layoutManager.findFirstVisibleItemPosition()
                        // Обработка нажатия на элемент списка
                        // Прокрутить RecyclerView к позиции itemPosition с отступом сверху
                        val smoothScroller: RecyclerView.SmoothScroller =
                            object : LinearSmoothScroller(context) {
                                override fun getVerticalSnapPreference(): Int {
                                    return SNAP_TO_START
                                }
                            }
                        smoothScroller.targetPosition = position;
                        (itemsList.layoutManager as GridLayoutManager).startSmoothScroll(smoothScroller);
                }
            })



        }

        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        // Полноэкранный режим
//        FullScreenHelper.enableFullScreen(window)
        // Получаем RecyclerView для товаров
        val itemsList : RecyclerView = findViewById(R.id.itemsList)
        // Создаем массив для заполнения товаров
        val items = arrayListOf<Item>()
        // Получаем RecyclerView для категорий
        val horizontalItemsList : RecyclerView = findViewById(R.id.horizontalItemsList)
        // Создаем массив для заполнения категорий
        val locations : TextView = findViewById(R.id.helper_for_logo)
        val db = DbHelper(this, null)
        locations.text = db.getLocationsData()?.address
        locations.paintFlags = locations.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        fun requestData() {
            val url = "https://appapi.ludilove.ru/api/menu"
            val queue = Volley.newRequestQueue(this)
            val request = object : StringRequest(
                Method.GET,
                url,
                { response ->
                    val db = DbHelper(this, null)
                    db.saveJsonData(response)
                    paintingItems(response)
                },
                { error ->
                    // Обработка ошибки
                    println(error)
                }) {
            }
            queue.add(request)
        }

        horizontalItemsList.setOnClickListener { view ->
            // Получаем позицию нажатого элемента
            val position = horizontalItemsList.getChildAdapterPosition(view)
            itemsList.smoothScrollToPosition(position)
        }

        val buttonCart : Button = findViewById(R.id.cartCount)


        val userLogin = db.get_last_user()
        val result = db.getCartCount(userLogin?.login)
        buttonCart.text = result;
        buttonCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val responseFromDB = db.getJsonData();
        if(responseFromDB != null) {
            paintingItems(responseFromDB)
        } else {
            requestData()
        }


        val exit : ImageView = findViewById(R.id.backArrow_item_exit)
        exit.setOnClickListener {
            @Suppress("NAME_SHADOWING") val userLogin = db.get_last_user()
            showConfirmationDialog(userLogin?.login, userLogin!!.id)
        }

        val link_to_auth : ImageButton = findViewById(R.id.link_to_auth)

        link_to_auth.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        locations.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }




    }

    override fun onResume() {
        super.onResume()
        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        val result = db.getCartCount(userLogin?.login)
        val buttonCart : Button = findViewById(R.id.cartCount)
        val responseFromDB = db.getJsonData();
        if (responseFromDB != null) {
            paintingItems(responseFromDB)
        }
        buttonCart.text = result;
    }

    // Вызов окна подтверждения (да\нет)
    private fun showConfirmationDialog(login : String?, id : Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Подтверждение")
        builder.setMessage("Вы уверены, что хотите выйти?")

        builder.setPositiveButton("Отмена") { dialog, which ->

        }

        builder.setNegativeButton("Да") { dialog, which ->
            val db = DbHelper(this, null)
            if (login != null) {
                db.get_last_user()
                db.change_last_user(login, 0, id.toString())
            }
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val dialog = builder.create()
        dialog.show()
    }
}