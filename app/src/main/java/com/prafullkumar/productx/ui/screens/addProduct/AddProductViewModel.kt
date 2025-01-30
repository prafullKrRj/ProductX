package com.prafullkumar.productx.ui.screens.addProduct

import android.content.Context
import android.net.Uri
import android.util.Log
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

class AddProductViewModel(
    private val repository: ProductListingRepository,
    private val context: Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isSuccess = MutableStateFlow(false)

    val isLoading = _isLoading.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()
    val isSuccess = _isSuccess.asStateFlow()

    var price by mutableStateOf("")
    var tax by mutableStateOf("")
    private val _product = MutableStateFlow(Product())
    val product = _product.asStateFlow()

    fun updateProductName(name: String) {
        _product.update { it.copy(productName = name) }
    }

    fun updateProductType(type: String) {
        _product.update { it.copy(productType = type) }
    }

    fun updatePrice(_price: String) {
        price = _price
    }

    fun updateTax(_tax: String) {
        tax = _tax
    }

    init {
        _product.value = Product()
        price = ""
        tax = ""
    }

    private val _selectedImageUris = MutableStateFlow<Uri?>(Uri.EMPTY)
    val selectedImageUris = _selectedImageUris.asStateFlow()

    fun addImageUri(uri: Uri) {
        _selectedImageUris.value = uri
    }

    fun clearImages() {
        _selectedImageUris.value = Uri.EMPTY
    }

    fun addProduct() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.addProduct(
                    product.value.copy(
                        price = if (price.isEmpty()) 0.0 else price.toDoubleOrNull(),
                        tax = if (tax.isEmpty()) 0.0 else tax.toDoubleOrNull()
                    ), _selectedImageUris.value
                ).collect { response ->
                    when (response) {
                        is BaseResponse.Error -> {
                            Log.e("AddProductViewModel", "Failed ${response.message}")
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