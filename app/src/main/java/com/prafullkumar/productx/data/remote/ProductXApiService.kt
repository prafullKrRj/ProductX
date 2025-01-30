package com.prafullkumar.productx.data.remote

import com.prafullkumar.productx.data.remote.dto.productAddingDtos.AddingProductResponseDto
import com.prafullkumar.productx.data.remote.dto.productGettingDtos.ProductResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Interface defining the API service for ProductX.
 */
interface ProductXApiService {

    /**
     * Fetches the list of products.
     *
     * @return A [Response] containing a [ProductResponseDto] object.
     */
    @GET("public/get")
    suspend fun getProducts(): Response<ProductResponseDto>

    /**
     * Adds a new product.
     *
     * @param productName The name of the product as a [RequestBody].
     * @param productType The type of the product as a [RequestBody].
     * @param price The price of the product as a [RequestBody].
     * @param tax The tax on the product as a [RequestBody].
     * @param files A list of image files as [MultipartBody.Part].
     * @return A [Response] containing an [AddingProductResponseDto] object.
     */
    @Multipart
    @POST("public/add")
    suspend fun addProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: List<MultipartBody.Part>?
    ): Response<AddingProductResponseDto>
}