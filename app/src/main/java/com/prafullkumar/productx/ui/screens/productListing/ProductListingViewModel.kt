package com.prafullkumar.productx.ui.screens.productListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.productx.domain.model.Product
import com.prafullkumar.productx.domain.repositories.ProductListingRepository
import com.prafullkumar.productx.utils.BaseResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProductListingViewModel(private val repository: ProductListingRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _allProducts = repository.getProducts()
        .stateIn(viewModelScope, SharingStarted.Lazily, BaseResponse.Loading)

    val products = combine(_allProducts, searchQuery) { productsResponse, query ->
        when (productsResponse) {
            is BaseResponse.Success -> {
                if (query.isBlank()) {
                    productsResponse
                } else {
                    BaseResponse.Success(filterProducts(productsResponse.data, query))
                }
            }

            is BaseResponse.Error -> {
                if (query.isBlank()) {
                    productsResponse
                } else {
                    productsResponse.cachedData?.let { cached ->
                        BaseResponse.Error(productsResponse.message, filterProducts(cached, query))
                    } ?: productsResponse
                }
            }

            else -> productsResponse
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, BaseResponse.Loading)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filterProducts(products: List<Product>, query: String): List<Product> {
        return products.filter { product ->
            product.productName.contains(query, ignoreCase = true) ||
                    product.productType.contains(query, ignoreCase = true)
        }
    }

    fun performSearch() {
        _searchQuery.value = _searchQuery.value
    }
}