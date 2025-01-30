package com.prafullkumar.productx.domain.repositories

import android.net.Uri
import com.prafullkumar.productx.data.remote.dto.productAddingDtos.AddingProductResponseDto
import com.prafullkumar.productx.domain.model.Product
import com.prafullkumar.productx.utils.BaseResponse
import kotlinx.coroutines.flow.Flow

interface ProductListingRepository {
    fun getProducts(): Flow<BaseResponse<List<Product>>>

    fun addProduct(product: Product, imageUri: Uri?): Flow<BaseResponse<AddingProductResponseDto>>
}