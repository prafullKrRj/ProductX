package com.prafullkumar.productx.domain.repositories

import android.net.Uri
import com.prafullkumar.productx.data.remote.dto.productAddingDtos.AddingProductResponseDto
import com.prafullkumar.productx.domain.model.Product
import com.prafullkumar.productx.utils.BaseResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing product listings.
 */
interface ProductListingRepository {

    /**
     * Fetches a list of products.
     *
     * @return A [Flow] emitting [BaseResponse] containing a list of [Product]s.
     */
    fun getProducts(): Flow<BaseResponse<List<Product>>>

    /**
     * Adds a new product.
     *
     * @param product The [Product] to be added.
     * @param imageUri The [Uri] of the product image, if any.
     * @return A [Flow] emitting [BaseResponse] containing the response of the add operation.
     */
    fun addProduct(product: Product, imageUri: Uri?): Flow<BaseResponse<AddingProductResponseDto>>
}