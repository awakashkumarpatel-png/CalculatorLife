package com.calculatorlife.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_media ORDER BY addedAtMillis DESC")
    fun observeAllMedia(): Flow<List<VaultMediaEntity>>

    @Query("SELECT * FROM vault_media WHERE albumId = :albumId ORDER BY addedAtMillis DESC")
    fun observeMediaInAlbum(albumId: Long): Flow<List<VaultMediaEntity>>

    @Query("SELECT * FROM vault_media WHERE albumId IS NULL ORDER BY addedAtMillis DESC")
    fun observeUnfiledMedia(): Flow<List<VaultMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: VaultMediaEntity): Long

    @Query("DELETE FROM vault_media WHERE id = :id")
    suspend fun deleteMedia(id: Long)

    @Query("SELECT * FROM vault_albums ORDER BY createdAtMillis DESC")
    fun observeAlbums(): Flow<List<VaultAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: VaultAlbumEntity): Long

    @Query("DELETE FROM vault_albums WHERE id = :id")
    suspend fun deleteAlbum(id: Long)

    @Query("UPDATE vault_media SET albumId = NULL WHERE albumId = :albumId")
    suspend fun unassignAlbumFromMedia(albumId: Long)
}
