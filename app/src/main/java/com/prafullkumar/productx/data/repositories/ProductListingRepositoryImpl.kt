package com.prafullkumar.productx.data.repositories

import android.content.Context
import android.net.Uri
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
import retrofit2.HttpException
import java.io.File
import java.io.IOException

/**
 * Implementation of the ProductListingRepository interface.
 *
 * @property apiService The API service used to fetch and add products.
 * @property productDao The DAO used to interact with the local database.
 * @property context The context used to access resources and files.
 */
class ProductListingRepositoryImpl(
    private val apiService: ProductXApiService,
    private val productDao: ProductDao,
    private val context: Context
) : ProductListingRepository {

    private val productResponseMapper = ProductResponseMapper()
    private val productEntityMapper = ProductEntityMapper()

    /**
     * Fetches products from the API and updates the local database.
     * If the API call fails, it returns cached products from the local database.
     *
     * @return A Flow emitting the state of the product fetching process.
     */
    override fun getProducts(): Flow<BaseResponse<List<Product>>> = flow {
        emit(BaseResponse.Loading)
        try {
            val apiResponse = apiService.getProducts()
            if (apiResponse.isSuccessful && apiResponse.body() != null) {

                val products =
                    apiResponse.body()!!.map { productResponseMapper.mapToDomainModel(it) }

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
                        message = "Failed to load from server",
                        cachedData = cachedProducts
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
            emit(BaseResponse.Error(message = errorMessage, cachedData = cachedProducts))
        }
    }

    /**
     * Adds a product to the server.
     * If an image URI is provided, it is included in the request.
     *
     * @param product The product to be added.
     * @param imageUri The URI of the image to be uploaded with the product.
     * @return A Flow emitting the state of the product adding process.
     */
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

/**
 * Creates a temporary file from a URI.
 *
 * @param context The context used to access resources and files.
 * @param uri The URI of the file to be converted.
 * @return The temporary file created from the URI.
 */
fun getFileFromUriTemp(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = File.createTempFile("cropped_", ".jpg", context.cacheDir)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}