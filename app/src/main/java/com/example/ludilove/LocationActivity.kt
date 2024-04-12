package com.example.ludilove

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.transition.Visibility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource



class LocationActivity : AppCompatActivity() {
    val locationsResponse: List<Location> = listOf(
        Location(
            locationId = 1,
            address = "Московский проспект, 10, Санкт-Петербург",
            coordinates = "59.9391, 30.3159",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 2,
            address = "ул. Большая Морская, 25, Санкт-Петербург",
            coordinates = "59.9343, 30.3294",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 3,
            address = "Невский проспект, 7, Санкт-Петербург",
            coordinates = "59.9349, 30.3355",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 4,
            address = "ул. Большая Конюшенная, 5, Санкт-Петербург",
            coordinates = "59.9340, 30.3301",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 5,
            address = "пр. Лиговский, 12, Санкт-Петербург",
            coordinates = "59.9275, 30.3556",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 6,
            address = "ул. Малая Морская, 5, Санкт-Петербург",
            coordinates = "59.9362, 30.3276",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 7,
            address = "пл. Восстания, 3, Санкт-Петербург",
            coordinates = "59.9312, 30.3613",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 8,
            address = "ул. Гороховая, 8, Санкт-Петербург",
            coordinates = "59.9393, 30.3190",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 9,
            address = "пр. Кронверкский, 18, Санкт-Петербург",
            coordinates = "59.9573, 30.3023",
            distance = "0",
            status = 1
        ),
        Location(
            locationId = 10,
            address = "ул. Малая Митрофаньевская, 15, Санкт-Петербург",
            coordinates = "59.9375, 30.3346",
            distance = "0",
            status = 1
        )
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val arrowToBack: ImageView = findViewById(R.id.arrowToBack)
        arrowToBack.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val autoLocation: ImageView = findViewById(R.id.auto_location)
        autoLocation.setOnClickListener {
            println("Автовыбор /// Заглушка")
        }

            val location_wrapper : RecyclerView = findViewById(R.id.location_wrapper)

            val coordHandler = getBestBakeryByCoord(this@LocationActivity)
            coordHandler.getBakery(object : getBestBakeryByCoord.CoordCallback {
                override fun onCoordReceived(locations: List<Location>) {
                    location_wrapper.layoutManager = LinearLayoutManager(this@LocationActivity)
                    location_wrapper.adapter = LocationsAdapter(locations)
                    progressBar.visibility = View.INVISIBLE
                }
                override fun onCoordFailed() {
                    TODO("Not yet implemented")
                }
            })
    }
}
