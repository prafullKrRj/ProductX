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

/**
 * ViewModel for managing the product listing screen.
 *
 * @property repository The repository for accessing product data.
 */
class ProductListingViewModel(private val repository: ProductListingRepository) : ViewModel() {

    // StateFlow for the search query entered by the user.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // StateFlow for all products fetched from the repository.
    private val _allProducts = repository.getProducts()
        .stateIn(viewModelScope, SharingStarted.Lazily, BaseResponse.Loading)

    // Combined StateFlow of products and search query to filter products based on the query.
    val products = combine(
        _allProducts,
        searchQuery
    ) { productsResponse, query -> // combine: Combines two flows and emits a new value whenever either of the flows emits a new value.
        when (productsResponse) {
            // Handle the success case by filtering products based on the search query.
            is BaseResponse.Success -> {
                if (query.isBlank()) {
                    productsResponse
                } else {
                    BaseResponse.Success(filterProducts(productsResponse.data, query))
                }
            }

            // Handle the error case by filtering cached products based on the search query.
            is BaseResponse.Error -> {
                if (query.isBlank()) {
                    productsResponse
                } else {
                    productsResponse.cachedData?.let { cached ->
                        BaseResponse.Error(productsResponse.message, filterProducts(cached, query))
                    } ?: productsResponse
                }
            }

            // Return the original response for other cases (e.g., loading).
            else -> productsResponse
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, BaseResponse.Loading)

    /**
     * Updates the search query.
     *
     * @param query The new search query.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Filters the list of products based on the search query.
     *
     * @param products The list of products to filter.
     * @param query The search query to filter by.
     * @return The filtered list of products.
     */
    private fun filterProducts(products: List<Product>, query: String): List<Product> {
        return products.filter { product ->
            product.productName.contains(query, ignoreCase = true) ||
                    product.productType.contains(query, ignoreCase = true)
        }
    }

    /**
     * Triggers a search with the current search query.
     */
    fun performSearch() {
        _searchQuery.value = _searchQuery.value
    }
}