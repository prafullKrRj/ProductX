package com.prafullkumar.productx.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import coil.network.HttpException
import com.prafullkumar.productx.data.local.ProductDao
import com.prafullkumar.productx.data.mapper.ProductEntityMapper
import com.prafullkumar.productx.data.mapper.ProductResponseMapper
import com.prafullkumar.productx.data.remote.ProductXApiService
import com.prafullkumar.productx.data.remote.dto.productAddingDtos.AddingProductResponseDto
import com.prafullkumar.productx.domain.model.Product
import com.prafullkumar.productx.domain.repositories.ProductListingRepository
import com.prafullkumar.productx.utils.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class ProductListingRepositoryImpl(
    private val apiService: ProductXApiService,
    private val productDao: ProductDao,
    private val context: Context
) : ProductListingRepository {

    private val productResponseMapper = ProductResponseMapper()
    private val productEntityMapper = ProductEntityMapper()

    override fun getProducts(): Flow<BaseResponse<List<Product>>> = flow {
        emit(BaseResponse.Loading)
        try {
            val apiResponse = apiService.getProducts()
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                Log.d("ProductListingRepositoryImpl", "getProducts: ${apiResponse.body()}")
                val products =
                    apiResponse.body()!!.map { productResponseMapper.mapToDomainModel(it) }
                Log.d("ProductListingRepositoryImpl", "getProducts: $products")
                productDao.replaceAllProducts(products.map {
                    productEntityMapper.mapFromDomainModel(
                        it
                    )
                })
                emit(BaseResponse.Success(products))
            } else {
                val cachedProducts = productDao.getProducts()
                    .map { entities -> entities.map { productEntityMapper.mapToDomainModel(it) } }
                    .firstOrNull()
                emit(
                    BaseResponse.Error(
                        message = "Failed to load from server", cachedData = cachedProducts
                    )
                )
            }
        } catch (e: Exception) {
            val cachedProducts = productDao.getProducts()
                .map { entities -> entities.map { productEntityMapper.mapToDomainModel(it) } }
                .firstOrNull()
            val errorMessage = when (e) {
                is IOException -> "Network error"
                is HttpException -> "Server error"
                else -> "Unknown error"
            }
            emit(
                BaseResponse.Error(
                    message = errorMessage, cachedData = cachedProducts
                )
            )
        }
    }

    override fun addProduct(
        product: Product, imageUri: Uri?
    ): Flow<BaseResponse<AddingProductResponseDto>> {
        return flow {
            try {
                val productNameBody = product.productName.toRequestBody(MultipartBody.FORM)
                val productTypeBody = product.productType.toRequestBody(MultipartBody.FORM)
                val priceBody = product.price.toString().toRequestBody(MultipartBody.FORM)
                val taxBody = product.tax.toString().toRequestBody(MultipartBody.FORM)
                val imageParts = if (imageUri != Uri.EMPTY || imageUri != null) {
                    imageUri?.let { getFileFromUriTemp(context, it) }?.let {
                        MultipartBody.Part.createFormData(
                            name = "files[]",
                            filename = it.name,
                            body = it.asRequestBody()
                        )
                    } ?: MultipartBody.Part.createFormData("files[]", "")
                } else {
                    MultipartBody.Part.createFormData("files[]", "")
                }
                val apiResponse = apiService.addProduct(
                    productNameBody, productTypeBody, priceBody, taxBody, listOf(imageParts)
                )
                if (apiResponse.isSuccessful && apiResponse.body() != null) {
                    emit(BaseResponse.Success(apiResponse.body()!!))
                } else {
                    emit(BaseResponse.Error(message = "Failed to add product"))
                }
            } catch (e: Exception) {
                emit(BaseResponse.Error(message = e.message ?: "Unknown error occurred"))
            }
        }
    }
}

fun getFileFromUriTemp(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = File.createTempFile("cropped_", ".jpg", context.cacheDir)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}