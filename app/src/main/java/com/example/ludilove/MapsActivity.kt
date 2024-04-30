package com.example.ludilove

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraBounds
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class MapsActivity : AppCompatActivity(){

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

        val southwest = Point(59.566389, 30.126389) // Юго-западная точка (например, окрестности Питера)
        val northeast = Point(60.194722, 30.767778) // Северо-восточная точка (например, окрестности Питера)
        val boundingBox = BoundingBox(southwest, northeast)

        maps.map.move(
                CameraPosition(startLocation, zoomValue, 0.0f, 0.0f),
                Animation(Animation.Type.LINEAR, 1f),
            null
        )
        maps.map.cameraBounds.latLngBounds = boundingBox


        val backArrow_item : ImageButton = findViewById(R.id.backArrow_item)
        backArrow_item.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val imageProvider = ImageProvider.fromResource(this, R.drawable.pin)
        val imageCurrentProvider = ImageProvider.fromResource(this, R.drawable.pin_picked)
        val pinsCollection = maps.map.mapObjects.addCollection()
        val db = DbHelper(this, null)
        val allLocations = db.getAllLocationsList()
        val locationResponse: Array<Location> = Gson().fromJson(allLocations, Array<Location>::class.java)
        for(loc in locationResponse)
        {
            val db = DbHelper(this, null)
            val current_loc = db.getLocationsData()
            val coord = Point(loc.latitude, loc.longitude)
            val reg = pinsCollection.addPlacemark().apply {
                geometry = coord
                userData = loc
                if(current_loc?.guid == loc.guid) {
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