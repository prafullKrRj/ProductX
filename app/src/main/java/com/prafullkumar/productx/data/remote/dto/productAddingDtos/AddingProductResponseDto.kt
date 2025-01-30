package com.prafullkumar.productx.data.remote.dto.productAddingDtos

import com.google.gson.annotations.SerializedName
import com.prafullkumar.productx.data.remote.dto.productGettingDtos.ProductResponseItemDto

data class AddingProductResponseDto(
    @SerializedName("message")
    val message: String,
    @SerializedName("product_details")
    val product_details: ProductResponseItemDto,
    @SerializedName("product_id")
    val product_id: Int,
    @SerializedName("success")
    val success: Boolean
)