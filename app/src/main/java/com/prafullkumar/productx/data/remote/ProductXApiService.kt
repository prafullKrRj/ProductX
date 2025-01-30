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

interface ProductXApiService {

    @GET("public/get")
    suspend fun getProducts(): Response<ProductResponseDto>


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