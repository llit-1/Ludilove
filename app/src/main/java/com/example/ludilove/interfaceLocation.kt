package com.example.ludilove

data class Location(
    var locationId: Int,
    val address: String,
    val coordinates: String,
    var distance: String,
    val status: Int,
)