package com.calculatorlife.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "calculator_life_settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class LanguageMode { SYSTEM, ENGLISH, HINDI }

/**
 * App-wide display preferences — separate from Vault security prefs, which
 * live in their own encrypted store ([VaultSecurity]) since they're
 * sensitive and these aren't.
 */
class SettingsRepository(private val context: Context) {
    private val keyTheme = stringPreferencesKey("theme_mode")
    private val keyLanguage = stringPreferencesKey("language_mode")
    private val keyDecimalPlaces = intPreferencesKey("decimal_places")

    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data.map { prefs ->
        prefs[keyTheme]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }

    val languageMode: Flow<LanguageMode> = context.settingsDataStore.data.map { prefs ->
        prefs[keyLanguage]?.let { runCatching { LanguageMode.valueOf(it) }.getOrNull() } ?: LanguageMode.SYSTEM
    }

    /** Decimal places shown on the Standard and Scientific calculator displays. Default 2, range 0-6. */
    val decimalPlaces: Flow<Int> = context.settingsDataStore.data.map { prefs -> prefs[keyDecimalPlaces] ?: 2 }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[keyTheme] = mode.name }
    }

    suspend fun setLanguageMode(mode: LanguageMode) {
        context.settingsDataStore.edit { it[keyLanguage] = mode.name }
    }

    suspend fun setDecimalPlaces(places: Int) {
        context.settingsDataStore.edit { it[keyDecimalPlaces] = places.coerceIn(0, 6) }
    }
}
