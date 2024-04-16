package com.example.ludilove

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class MapsActivity : AppCompatActivity(){

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

    private val startLocation = Point(59.938678, 30.314474)
    private var zoomValue: Float = 12f

    private val placemarkTapListener = MapObjectTapListener { mapObjects, point ->
        val loca = mapObjects.userData as Location
        ModalForMap(loca, this).show(supportFragmentManager, "MyDialogFragment")
        true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this@MapsActivity);
        setContentView(R.layout.activity_maps)
        val maps : MapView = findViewById(R.id.mapView)
        maps.map.move(
                CameraPosition(startLocation, zoomValue, 0.0f, 0.0f),
                Animation(Animation.Type.LINEAR, 1f),
                null)
        val backArrow_item : ImageButton = findViewById(R.id.backArrow_item)
        backArrow_item.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val imageProvider = ImageProvider.fromResource(this, R.drawable.pin)
        val imageCurrentProvider = ImageProvider.fromResource(this, R.drawable.pin_picked)
        val pinsCollection = maps.map.mapObjects.addCollection()

        for(loc in locationsResponse) {
            val parts = loc.coordinates.split(",")
            val db = DbHelper(this, null)
            val current_loc = db.getLocationsData()
            val coord = Point(parts[0].toDouble(), parts[1].toDouble())
            val reg = pinsCollection.addPlacemark().apply {
                geometry = coord
                userData = loc
                if(current_loc?.locationId == loc.locationId) {
                    setIcon(imageCurrentProvider)
                } else {
                    setIcon(imageProvider)
                }

            }
            reg.addTapListener(placemarkTapListener)
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        val maps : MapView = findViewById(R.id.mapView)
        maps.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        val maps : MapView = findViewById(R.id.mapView)
        maps.onStop()
        super.onStop()
    }
}