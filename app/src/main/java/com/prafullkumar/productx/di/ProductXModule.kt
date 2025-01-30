package com.prafullkumar.productx.di

import androidx.room.Room
import com.prafullkumar.productx.data.local.ProductDao
import com.prafullkumar.productx.data.local.ProductDatabase
import com.prafullkumar.productx.data.remote.ProductXApiService
import com.prafullkumar.productx.data.repositories.ProductListingRepositoryImpl
import com.prafullkumar.productx.domain.repositories.ProductListingRepository
import com.prafullkumar.productx.ui.screens.addProduct.AddProductViewModel
import com.prafullkumar.productx.ui.screens.productListing.ProductListingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Koin module for providing dependencies related to ProductX.
 */
val productXModule = module {
    /**
     * Provides a singleton instance of ProductXApiService.
     */
    single<ProductXApiService> {
        Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductXApiService::class.java)
    }

    /**
     * Provides a singleton instance of ProductDao.
     */
    single<ProductDao> {
        Room.databaseBuilder(androidContext(), ProductDatabase::class.java, "product_database")
            .build()
            .productDao()
    }

    /**
     * Provides a singleton instance of ProductListingRepository.
     */
    single<ProductListingRepository> {
        ProductListingRepositoryImpl(
            apiService = get(),
            productDao = get(),
            context = androidContext()
        )
    }

    /**
     * Provides a ViewModel instance for ProductListingViewModel.
     */
    viewModel { ProductListingViewModel(repository = get()) }

    /**
     * Provides a ViewModel instance for AddProductViewModel.
     */
    viewModel { AddProductViewModel(repository = get(), context = androidContext()) }
}