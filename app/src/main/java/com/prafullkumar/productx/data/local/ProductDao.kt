package com.prafullkumar.productx.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for accessing the Product database.
 */
@Dao
interface ProductDao {

    /**
     * Retrieves all products from the database.
     * @return A Flow that emits a list of ProductEntity objects.
     */
    @Query("SELECT * FROM products")
    fun getProducts(): Flow<List<ProductEntity>>

    /**
     * Inserts a list of products into the database.
     * If a product already exists, it will be replaced.
     * @param products The list of ProductEntity objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    /**
     * Deletes all products from the database.
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    /**
     * Replaces all products in the database with the provided list.
     * This is done by first deleting all existing products and then inserting the new ones.
     * @param products The list of ProductEntity objects to insert.
     */
    @Transaction
    suspend fun replaceAllProducts(products: List<ProductEntity>) {
        deleteAllProducts()
        insertProducts(products)
    }
}