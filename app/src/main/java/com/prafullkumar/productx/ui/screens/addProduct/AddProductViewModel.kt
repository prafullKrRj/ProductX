package com.prafullkumar.productx.ui.screens.addProduct

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.productx.ProductXApplication
import com.prafullkumar.productx.domain.model.Product
import com.prafullkumar.productx.domain.repositories.ProductListingRepository
import com.prafullkumar.productx.utils.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for adding a product.
 *
 * @property repository The repository to handle product listing operations.
 * @property context The context to access application resources.
 */
class AddProductViewModel(
    private val repository: ProductListingRepository,
    private val context: Context
) : ViewModel() {

    // StateFlow to manage loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // StateFlow to manage error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // StateFlow to manage success state
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    // Mutable state for price and tax
    var price by mutableStateOf("")
    var tax by mutableStateOf("")

    // StateFlow to manage product details
    private val _product = MutableStateFlow(Product())
    val product = _product.asStateFlow()

    // StateFlow to manage selected image URIs
    private val _selectedImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
    val selectedImageUris = _selectedImageUri.asStateFlow()

    /**
     * Updates the product name.
     *
     * @param name The new product name.
     */
    fun updateProductName(name: String) {
        _product.update { it.copy(productName = name) }
    }

    /**
     * Updates the product type.
     *
     * @param type The new product type.
     */
    fun updateProductType(type: String) {
        _product.update { it.copy(productType = type) }
    }

    /**
     * Updates the price.
     *
     * @param _price The new price.
     */
    fun updatePrice(_price: String) {
        price = _price
    }

    /**
     * Updates the tax.
     *
     * @param _tax The new tax.
     */
    fun updateTax(_tax: String) {
        tax = _tax
    }

    init {
        _product.value = Product()
        price = ""
        tax = ""
    }

    /**
     * Adds an image URI to the selected images.
     *
     * @param uri The URI of the image to add.
     */
    fun addImageUri(uri: Uri) {
        _selectedImageUri.value = uri
    }

    /**
     * Adds a product using the repository.
     */
    fun addProduct() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.addProduct(
                    product.value.copy(
                        price = if (price.isEmpty()) 0.0 else price.toDoubleOrNull(),
                        tax = if (tax.isEmpty()) 0.0 else tax.toDoubleOrNull()
                    ), _selectedImageUri.value
                ).collect { response ->
                    when (response) {
                        is BaseResponse.Error -> {
                            _errorMessage.value = "Failed to add product"
                        }

                        BaseResponse.Loading -> {
                            _isLoading.value = true
                        }

                        is BaseResponse.Success -> {
                            withContext(Dispatchers.Main) {
                                (context as ProductXApplication).sendProductAddedNotification(
                                    product.value.productName
                                )
                            }
                            _isSuccess.value = true
                            _errorMessage.value = null
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}