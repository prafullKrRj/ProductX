package com.prafullkumar.productx.domain.model


data class Product(
    val id: Int = 0,
    val image: String = "",
    val price: Double? = null,
    val productName: String = "",
    val productType: String = "",
    val tax: Double? = null
)