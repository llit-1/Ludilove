package com.example.ludilove

import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        FullScreenHelper.enableFullScreen(window)

        val title : TextView = findViewById(R.id.item_list_title_second)
        val image : ImageView = findViewById(R.id.item_list_image)
        val desc : TextView = findViewById(R.id.item_list_text)
        val price : TextView = findViewById(R.id.item_price_text)

        val buttonBuy : Button = findViewById(R.id.button_buy)
        val arrowBack : ImageButton = findViewById(R.id.backArrow_item)


        title.text = intent.getStringExtra("ItemTitle")
        desc.text = intent.getStringExtra("ItemText")
        price.text = "Стоимость: ${intent.getStringExtra("ItemPrice")} Р"


        val db = DbHelper(this, null)
        val userLogin = db.get_last_user()

        buttonBuy.setOnClickListener {
            val result = intent.getStringExtra("ItemPrice")
                ?.let {
                    db.addToCart(intent.getStringExtra("ItemTitle")!!, it.toInt(), userLogin?.login.toString(),
                        intent.getStringExtra("ItemImage")!!, 1
                    )
                    val intent = Intent(this, ItemsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    this.startActivity(intent)
                }
        }

        arrowBack.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            this.startActivity(intent)
        }


        Glide.with(this)
            .asBitmap()
            .load(intent.getStringExtra("ItemImage"))
            .into(image)

    }
}