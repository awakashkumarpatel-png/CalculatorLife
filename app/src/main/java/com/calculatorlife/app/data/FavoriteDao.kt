package com.calculatorlife.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_calculators ORDER BY addedAtMillis DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FavoriteEntity)

    @Query("DELETE FROM favorite_calculators WHERE route = :route")
    suspend fun deleteByRoute(route: String)
}
