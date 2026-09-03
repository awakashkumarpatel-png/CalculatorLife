package com.calculatorlife.app.data

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Stores the Vault PIN as a salted SHA-256 hash — never the PIN itself —
 * inside an EncryptedSharedPreferences file, which is itself encrypted at
 * rest using an Android Keystore-backed master key. So the PIN is
 * protected twice over: once by hashing, once by the encrypted store.
 */
class VaultSecurity(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "vault_security_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun isPinSet(): Boolean = prefs.contains(KEY_HASH)

    fun setPin(pin: String) {
        val salt = randomSalt()
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(KEY_HASH, hash)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val saltEncoded = prefs.getString(KEY_SALT, null) ?: return false
        val storedHash = prefs.getString(KEY_HASH, null) ?: return false
        val salt = Base64.decode(saltEncoded, Base64.NO_WRAP)
        return hashPin(pin, salt) == storedHash
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    private fun randomSalt(): ByteArray = ByteArray(16).also { SecureRandom().nextBytes(it) }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    companion object {
        private const val KEY_SALT = "pin_salt"
        private const val KEY_HASH = "pin_hash"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }
}
