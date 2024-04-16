package com.example.ludilove

import android.content.Context
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class getBestBakeryByCoord(private val context: Context) {

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
    interface CoordCallback {
        fun onCoordReceived(locations : List<Location>)
        fun onCoordFailed()
    }
    fun calculateDistance(coord1: Pair<Double, Double>?, coord2Str: String): String{
        val coord2 = parseCoordinates(coord2Str) ?: return ""
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
        return "%.2f".format(distance)
    }

    fun parseCoordinates(coordStr: String): Pair<Double, Double>? {
        val parts = coordStr.split(",")
        if (parts.size != 2) return null
        val lat = parts[0].trim().toDoubleOrNull() ?: return null
        val lon = parts[1].trim().toDoubleOrNull() ?: return null
        return Pair(lat, lon)
    }
    fun getBakery(callback: CoordCallback) {

        var coord : Pair<Double,Double>? = null

        val locationHandler = LocationHandler(context)
        locationHandler.getCoordinates(object : LocationHandler.LocationCallback {
            override fun onLocationReceived(latitude: Double, longitude: Double) {
                coord = Pair(latitude, longitude)
                for(loc in locationsResponse) {
                    loc.distance = calculateDistance(coord , loc.coordinates)
                }
                val sortedLocations = locationsResponse.sortedBy { it.distance.replace(',', '.').toDoubleOrNull() ?: Double.MAX_VALUE }
                callback.onCoordReceived(sortedLocations)
            }

            override fun onLocationFailed() {
                // Обработка ошибки получения координат
                println("Unlucko")
            }
        })
    }

}