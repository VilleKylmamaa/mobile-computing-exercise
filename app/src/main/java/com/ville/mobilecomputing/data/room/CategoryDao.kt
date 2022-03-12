package com.ville.mobilecomputing.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ville.mobilecomputing.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {

    @Query(value = "SELECT * FROM categories WHERE name = :name")
    abstract suspend fun getCategoryWithName(name: String): Category?

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    abstract suspend fun getCategoryWithId(categoryId: Long): Category?

    @Query("SELECT * FROM categories LIMIT 15")
    abstract fun categories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<Category>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Category)

    @Delete
    abstract suspend fun delete(entity: Category): Int
}