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

val productXModule = module {
    single<ProductXApiService> {
        Retrofit.Builder().baseUrl("https://app.getswipe.in/api/").addConverterFactory(
            GsonConverterFactory.create()
        ).build().create(ProductXApiService::class.java)
    }
    single<ProductDao> {
        Room.databaseBuilder(androidContext(), ProductDatabase::class.java, "product_database")
            .build().productDao()
    }
    single<ProductListingRepository> {
        ProductListingRepositoryImpl(apiService = get(), productDao = get(), androidContext())
    }
    viewModel { ProductListingViewModel(repository = get()) }
    viewModel { AddProductViewModel(repository = get()) }
}