package com.example.ludilove

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide

class ItemActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        // Включаем полноэкранный режим
//        FullScreenHelper.enableFullScreen(window)
        // Получаем элементы страницы
        val title : TextView = findViewById(R.id.item_list_title_second)
        val image : ImageView = findViewById(R.id.item_list_image)
        val desc : TextView = findViewById(R.id.item_list_text)
        val price : TextView = findViewById(R.id.item_price_text)
        val buttonBuy : Button = findViewById(R.id.button_buy)
        val arrowBack : ImageButton = findViewById(R.id.backArrow_item)
        val minusButton : ImageButton = findViewById(R.id.decreaseButton)
        val countItemInCart : TextView = findViewById(R.id.item_cart_count)
        val plusButton : ImageButton = findViewById(R.id.increaseButton)
        // Заполняем основные данные из хранилища
        title.text = intent.getStringExtra("ItemTitle")
        desc.text = intent.getStringExtra("ItemText")
        price.text = "Стоимость: ${intent.getStringExtra("ItemPrice")} Р"

        // Получаем стоимость товара для расчетов
        var priceForCalculate = intent.getStringExtra("ItemPrice");
        // Получаем последнего пользователя
        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()
        // Получаем кол-во этого товара для пользователя
        var getCartItemQuantity = db.getCartItemQuantity(userLogin?.login, title.text.toString())
        // Если есть хоть 1 товар, то показываем стрелочки и убираем кнопку добавления в корзину
        if(getCartItemQuantity != 0) {
            buttonBuy.isVisible = false
            minusButton.isVisible = true
            countItemInCart.isVisible = true
            plusButton.isVisible = true
            // Обновляем отображение кол-ва товаров
            countItemInCart.text = "$getCartItemQuantity шт."
            // Обновляем общую стоимость в зависимости от кол-ва товаров
            priceForCalculate = intent.getStringExtra("ItemPrice")?.toInt()
                ?.times(getCartItemQuantity).toString()
            // Вставляем получившуюся сумму
            price.text = "Стоимость: ${priceForCalculate} Р"
        }

        // При тыке на кнопку "Добавить в корзину" показываем стрелочки и убираем кнопку
        buttonBuy.setOnClickListener {
            buttonBuy.isVisible = false
            minusButton.isVisible = true
            countItemInCart.isVisible = true
            plusButton.isVisible = true
            // Добавляем в корзину 1 товар
            db.addToCart(intent.getStringExtra("ItemId")!!.toInt() ,title.text.toString(), intent.getStringExtra("ItemPrice")!!.toInt(), userLogin!!.login, intent.getStringExtra("ItemImage")!!, 1)
            // Меняем прозрачность минуса
            minusButton.alpha = 0.3F

            arrowBack.setOnClickListener {
                val intent = Intent(this, ItemsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                this.startActivity(intent)
            }
        }

        // При тыке на кнопку "+"
        plusButton.setOnClickListener{
            // Увеличиваем число товара в БД на 1
            db.incrOrDecrItemsInCart(title.text.toString(), userLogin!!.login, "incr")
            // Получаем общее кол-во этого товара в корзине пользователя
            getCartItemQuantity = db.getCartItemQuantity(userLogin.login, title.text.toString())
            // Обновляем состояние кол-ва товаров
            countItemInCart.text = "$getCartItemQuantity шт."
            // Обновляем состояние общей стоимости на странице
            priceForCalculate = (priceForCalculate?.toInt()?.plus(intent.getStringExtra("ItemPrice")?.toInt()!!)).toString()
            // Устанавливаем новую общую стоимость
            price.text = "Стоимость: $priceForCalculate Р"

            // Меняем прозрачность
            if(getCartItemQuantity < 2) {
                minusButton.alpha = 0.3F
            } else {
                minusButton.alpha = 1F
            }
        }

        // При тыке на кнопку "-"
        minusButton.setOnClickListener{
            // Если прозрачность не равна 0.3F (то есть активна), то выполняем действия
            if(minusButton.alpha != 0.3F) {
                // Уменьшаем кол-во товаров в базе
                db.incrOrDecrItemsInCart(title.text.toString(), userLogin!!.login, "decr")
                // Получаем новое значение кол-ва товаров
                getCartItemQuantity = db.getCartItemQuantity(userLogin.login, title.text.toString())
                // Обновляем данные кол-ва на активити
                countItemInCart.text = "$getCartItemQuantity шт."
                // Отключаем кнопку, если кол-во равно 1
                if(getCartItemQuantity == 1) {
                    minusButton.alpha = 0.3F
                }
                // Обновляем состояние общей стоимости на странице
                priceForCalculate = (priceForCalculate?.toInt()?.minus(intent.getStringExtra("ItemPrice")?.toInt()!!)).toString()
                // Устанавливаем новую общую стоимость
                price.text = "Стоимость: $priceForCalculate Р"
            }
        }

        arrowBack.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = FLAG_ACTIVITY_REORDER_TO_FRONT;
            this.startActivity(intent)
        }


        Glide.with(this)
            .asBitmap()
            .load(intent.getStringExtra("ItemImage"))
            .into(image)

    }
}