package com.example.ludilove

data class Location(
    var guid: String,
    val name: String,
    val rkCode: Int,
    val latitude: Double,
    val longitude: Double,
    var distance: Double,
    val actual: Int,
)