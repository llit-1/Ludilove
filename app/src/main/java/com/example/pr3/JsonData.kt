package com.example.ludilove

class JsonData {

    data class MenuResponse(
        val lastUpdatedAt: String,
        val menuItems: MenuItems
    )

    data class MenuItems(
        val categories: List<Category>,
        val products: List<Product>
    )

    data class Category(
        val id: String,
        val parentId: String,
        val name: String
    )

    data class Product(
        val id: String,
        val categoryId: String,
        val name: String,
        val description: String,
        val price: Int,
        val imageUrl: String,
        val weight: String
    )

}