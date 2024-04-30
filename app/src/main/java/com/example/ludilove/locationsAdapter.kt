package com.example.ludilove

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity

import androidx.recyclerview.widget.RecyclerView


class LocationsAdapter(private val locations: List<Location>) :

    RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_locations, parent, false)
        return LocationViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])

    }

    override fun getItemCount(): Int {
        return locations.size
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationName: TextView = itemView.findViewById(R.id.name_location)
        private val locationDistance: TextView = itemView.findViewById(R.id.distance_location)
        private val locationId : TextView = itemView.findViewById(R.id.id_location)
        private val buttonPick : Button = itemView.findViewById(R.id.confirm_location)

        @SuppressLint("SimpleDateFormat", "ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(location: Location) {
            locationName.text = location.name;
            locationId.text = location.guid
            locationId.visibility = View.INVISIBLE;
            locationDistance.text = "${location.distance} Км."


            locationName.setOnClickListener {
                if(locationName.maxLines == 3) {
                    locationName.maxLines = 1
                } else {
                    locationName.maxLines = 3
                }
            }

            val db = DbHelper(itemView.context, null)
            val currentLocation = db.getLocationsData()

            if(location.guid == currentLocation?.guid) {
                buttonPick.text = "Выбрано"
                buttonPick.setBackgroundColor(Color.parseColor("#43646863"))
                buttonPick.isEnabled = false
            }

            buttonPick.setOnClickListener {
                db.deleteAndInsertLocation(location)
                val intent = Intent(itemView.context, ItemsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                itemView.context.startActivity(intent)
            }

        }

    }
}
