package com.prafullkumar.productx

import android.app.Application
import com.prafullkumar.productx.di.productXModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ProductXApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ProductXApplication)
            androidLogger()
            modules(productXModule)
        }
    }
}