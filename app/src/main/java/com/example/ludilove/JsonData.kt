package com.example.ludilove

class JsonData {
//    data class MenuResponse(
//        val lastUpdatedAt: String,
//        val menuItems: MenuItems
//    )
//
//    data class MenuItems(
//        val categories: List<Category>,
//        val products: List<Product>
//    )
//
//    data class Category(
//        val id: String,
//        val parentId: String,
//        val name: String
//    )
//
    data class Categories(
        val id: Int,
        val categoryName: String,
        val image: String,
        val responseItems: List<Product>
    )

    data class Product(
        val id: String,
        val name: String,
        val price: Int,
        val image: String,
        val menuCategory: Int,
        val description: String,
        val measure: Measure,
        val amount: Int
    )

    data class Measure(
        val id: Int,
        val name: String
    )
}

