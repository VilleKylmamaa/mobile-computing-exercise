package com.ville.mobilecomputing.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ville.mobilecomputing.data.entity.Category
import com.ville.mobilecomputing.data.entity.Payment

/**
 * The [RoomDatabase] for this app
 */
@Database(
    entities = [Category::class, Payment::class],
    version = 3,
    exportSchema = false
)
abstract class MobileComputingDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentDao(): PaymentDao
}