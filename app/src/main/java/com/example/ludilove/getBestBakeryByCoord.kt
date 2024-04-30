package com.example.ludilove

import LocationHandler
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.text.DecimalFormat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class getBestBakeryByCoord(private val context: Context) {
    interface CoordCallback {
        fun onCoordReceived(locations : List<Location>)
        fun onCoordFailed()
    }
    fun calculateDistance(coord1: Pair<Double, Double>?, coord2: Pair<Double, Double>): Double{
        val R = 6371 // Радиус Земли в километрах
        val lat1 = coord1?.let { Math.toRadians(it.first) }
        val lon1 = coord1?.let { Math.toRadians(it.second) }
        val lat2 = Math.toRadians(coord2.first)
        val lon2 = Math.toRadians(coord2.second)
        val dLat = lat2 - lat1!!
        val dLon = lon2 - lon1!!
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c

        return distance
    }
    fun getBakery(callback: CoordCallback) {

        var coord : Pair<Double,Double>? = null

        val locationHandler = LocationHandler(context)
        locationHandler.getCoordinates(object : LocationHandler.LocationCallback {
            override fun onLocationReceived(latitude: Double, longitude: Double) {
                coord = Pair(latitude, longitude)
                val db = DbHelper(context, null)
                val user = db.get_last_user()
                val url = "https://appapi.ludilove.ru/api/Location"
                val queue = Volley.newRequestQueue(context)
                val request = object : StringRequest(
                    Method.GET,
                    url,
                    { response ->
                        if(response != null) {
                            val locationResponse: Array<Location> = Gson().fromJson(response, Array<Location>::class.java)
                            for(loc in locationResponse)
                            {
                                loc.distance = calculateDistance(coord , Pair(loc.latitude, loc.longitude))
                                val df = DecimalFormat("#.##")
                                loc.distance = df.format(loc.distance).replace(',', '.').toDouble()
                            }
                                val sortedLocations = locationResponse.sortedBy { it.distance}
                                db.putAllLocationsList(Gson().toJson(sortedLocations))
                                callback.onCoordReceived(sortedLocations)
                        }

                    },
                    { error ->
                        println(error)
                    }) {}
                queue.add(request)


            }

            override fun onLocationFailed() {
                // Обработка ошибки получения координат
                println("Unlucko")
            }
        })
    }
}