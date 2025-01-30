package com.prafullkumar.productx.domain.model


/**
 * Data class representing a Product in the domain model.
 *
 * @property id The unique identifier of the product.
 * @property image The URL or path to the product image.
 * @property price The price of the product, nullable.
 * @property productName The name of the product.
 * @property productType The type or category of the product.
 * @property tax The tax applied to the product, nullable.
 */
data class Product(
    val id: Int = 0,
    val image: String = "",
    val price: Double? = null,
    val productName: String = "",
    val productType: String = "",
    val tax: Double? = null
)