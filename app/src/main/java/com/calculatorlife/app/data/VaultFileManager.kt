package com.calculatorlife.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.util.UUID

/**
 * Encrypts every imported photo/video at rest (AES-256-GCM via
 * [EncryptedFile], Keystore-backed) and stores it in the app's private
 * internal storage — never in shared/public storage, and never uploaded
 * anywhere. Files are decrypted only into memory (photos) or a private
 * cache file (videos, since playback needs a real file path) on demand.
 */
class VaultFileManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val vaultDir: File by lazy {
        File(context.filesDir, "vault_media").apply { if (!exists()) mkdirs() }
    }

    /** Copies+encrypts the picked [sourceUri] into the vault; returns the stored file name. */
    fun importMedia(sourceUri: Uri, extension: String): String? {
        val fileName = "${UUID.randomUUID()}.$extension.enc"
        val destination = File(vaultDir, fileName)
        val encryptedFile = EncryptedFile.Builder(
            context, destination, masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                encryptedFile.openFileOutput().use { output ->
                    input.copyTo(output)
                }
            }
            fileName
        } catch (e: Exception) {
            destination.delete()
            null
        }
    }

    fun deleteMedia(fileName: String) {
        File(vaultDir, fileName).delete()
    }

    /** Decrypts a photo directly into a Bitmap for display — never written back to disk unencrypted. */
    fun decryptPhoto(fileName: String): Bitmap? {
        val file = File(vaultDir, fileName)
        if (!file.exists()) return null
        val encryptedFile = EncryptedFile.Builder(
            context, file, masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        return try {
            encryptedFile.openFileInput().use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Decrypts a video into the app's private cache directory (playback
     * needs a real file path) and returns that file. Callers should delete
     * it via [clearPlaybackCache] once done — nothing here is left decrypted
     * longer than the current playback session.
     */
    fun decryptVideoToCache(fileName: String): File? {
        val source = File(vaultDir, fileName)
        if (!source.exists()) return null
        val encryptedFile = EncryptedFile.Builder(
            context, source, masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val playbackCacheDir = File(context.cacheDir, "vault_playback").apply { if (!exists()) mkdirs() }
        val tempFile = File(playbackCacheDir, fileName.removeSuffix(".enc"))
        return try {
            encryptedFile.openFileInput().use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            tempFile
        } catch (e: Exception) {
            tempFile.delete()
            null
        }
    }

    fun clearPlaybackCache() {
        File(context.cacheDir, "vault_playback").deleteRecursively()
    }
}
