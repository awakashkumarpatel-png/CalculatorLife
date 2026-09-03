package com.calculatorlife.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calculatorlife.app.CalculatorLifeApp
import com.calculatorlife.app.data.LanguageMode
import com.calculatorlife.app.data.SettingsRepository
import com.calculatorlife.app.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as CalculatorLifeApp
    private val settings = SettingsRepository(application)
    private val historyDao = app.database.historyDao()
    private val vaultSecurity = app.vaultSecurity

    val themeMode: StateFlow<ThemeMode> = settings.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)
    val languageMode: StateFlow<LanguageMode> = settings.languageMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LanguageMode.SYSTEM)
    val decimalPlaces: StateFlow<Int> = settings.decimalPlaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settings.setThemeMode(mode) }
    }

    fun setLanguageMode(mode: LanguageMode) {
        viewModelScope.launch { settings.setLanguageMode(mode) }
    }

    fun setDecimalPlaces(places: Int) {
        viewModelScope.launch { settings.setDecimalPlaces(places) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { historyDao.clearAll() }
    }

    fun isVaultPinSet(): Boolean = vaultSecurity.isPinSet()
    fun isVaultBiometricEnabled(): Boolean = vaultSecurity.isBiometricEnabled()
    fun setVaultBiometricEnabled(enabled: Boolean) = vaultSecurity.setBiometricEnabled(enabled)
}
