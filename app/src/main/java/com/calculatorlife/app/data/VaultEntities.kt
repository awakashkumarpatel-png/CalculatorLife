package com.calculatorlife.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VaultMediaType { PHOTO, VIDEO }

@Entity(tableName = "vault_albums")
data class VaultAlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAtMillis: Long
)

/**
 * Metadata only — [encryptedFileName] points at the actual encrypted bytes
 * on disk, managed by [VaultFileManager]. Keeping metadata in Room and
 * content in encrypted files (rather than blobs in the database) keeps the
 * database itself small and the encryption scheme simple to reason about.
 */
@Entity(tableName = "vault_media")
data class VaultMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val albumId: Long?,
    val type: VaultMediaType,
    val encryptedFileName: String,
    val originalFileName: String?,
    val addedAtMillis: Long
)
