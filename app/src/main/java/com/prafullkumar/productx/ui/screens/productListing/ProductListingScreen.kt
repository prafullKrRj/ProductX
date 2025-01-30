package com.prafullkumar.productx.ui.screens.productListing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.prafullkumar.productx.Screens
import com.prafullkumar.productx.domain.model.Product
import com.prafullkumar.productx.ui.screens.productListing.components.ProductListUI
import com.prafullkumar.productx.ui.screens.productListing.components.ProductListingTopBar
import com.prafullkumar.productx.ui.screens.productListing.components.ProductLoadingScreen
import com.prafullkumar.productx.utils.BaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProductListingScreen(
    navHostController: NavHostController,
    productListingViewModel: ProductListingViewModel
) {
    val searchQuery by productListingViewModel.searchQuery.collectAsState()
    val products by productListingViewModel.products.collectAsState()
    var showingSearchBar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ProductListingContent(
        navHostController = navHostController,
        searchQuery = searchQuery,
        showingSearchBar = showingSearchBar,
        onSearchQueryChange = { productListingViewModel.updateSearchQuery(it) },
        onSearch = { productListingViewModel.performSearch() },
        onToggleSearchBar = { showingSearchBar = it },
        products = products,
        snackbarHostState = snackbarHostState,
        scope = scope
    )
}

@Composable
fun ProductListingContent(
    navHostController: NavHostController,
    searchQuery: String,
    showingSearchBar: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onToggleSearchBar: (Boolean) -> Unit,
    products: BaseResponse<List<Product>>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    Scaffold(
        topBar = {
            ProductListingTopBar(
                searchQuery = searchQuery,
                showingSearchBar = showingSearchBar,
                onSearchQueryChange = onSearchQueryChange,
                onSearch = onSearch,
                onToggleSearchBar = onToggleSearchBar
            )
        },
        floatingActionButton = {
            FloatingActionButton({
                navHostController.navigate((Screens.AddProductScreen))
            }, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (products) {
                is BaseResponse.Error -> {
                    LaunchedEffect(Unit) {
                        scope.launch {
                            snackbarHostState.showSnackbar(products.message)
                        }
                    }
                    ProductListUI(products.cachedData ?: emptyList())
                }

                BaseResponse.Loading -> {
                    ProductLoadingScreen()
                }

                is BaseResponse.Success -> {
                    ProductListUI(products.data)
                }
            }
        }
    }
}