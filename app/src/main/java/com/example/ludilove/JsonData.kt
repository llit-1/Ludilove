package com.example.ludilove

import com.google.gson.annotations.SerializedName

data class JsonData(
    @SerializedName("categories")
    val categories: List<Category>
)

data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("responseItems")
    val responseItems: List<ResponseItem>
)

data class ResponseItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int?, // Измените тип данных на double, если цены представлены числами
    @SerializedName("image")
    val image: String,
    @SerializedName("menuCategory")
    val menuCategory: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("measure")
    val measure: Measure,
    @SerializedName("amount")
    val amount: Int
)

data class Measure(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
