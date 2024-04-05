package com.example.ludilove

import java.sql.Date
import java.time.format.DateTimeFormatter

data class Order(
    val orderId: Int,
    val creationDate: String,
    val sum: Int,
    val status: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val itemId: Int,
    val name: String,
    val sum: Int,
    val quantity: Int
)

data class OrderResponse(
    val responseOrders: List<Order>,
    val exceptionCode: Int,
    val exceptionText: String
)