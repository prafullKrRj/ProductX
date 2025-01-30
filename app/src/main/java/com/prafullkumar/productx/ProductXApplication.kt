package com.prafullkumar.productx

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.prafullkumar.productx.di.productXModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application class for ProductX.
 * Initializes Koin for dependency injection and sets up notification channels.
 */
class ProductXApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ProductXApplication)
            androidLogger()
            modules(productXModule)
        }
        createNotificationChannel()
    }

    /**
     * Sends a notification when a product is added.
     *
     * @param productName The name of the product that was added.
     */
    fun sendProductAddedNotification(productName: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val notification = NotificationCompat.Builder(this, "product_add_channel")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Product Added")
            .setContentText("Product $productName has been added.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Creates a notification channel for product addition notifications.
     */
    private fun createNotificationChannel() {
        val name = "Product Additions"
        val descriptionText = "Notifications for added products"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("product_add_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}