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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val arrowToBack: ImageView = findViewById(R.id.arrowToBack)
        arrowToBack.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        val autoLocation: ImageView = findViewById(R.id.auto_location)
        autoLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
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
