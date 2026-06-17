package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CreationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {
    @Query("SELECT * FROM creation_items ORDER BY timestamp DESC")
    fun getAllCreations(): Flow<List<CreationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(item: CreationItem)

    @Query("DELETE FROM creation_items WHERE id = :id")
    suspend fun deleteCreationById(id: Int)

    @Query("DELETE FROM creation_items")
    suspend fun clearAllCreations()
}
