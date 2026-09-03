package com.calculatorlife.app.ui.vault

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calculatorlife.app.CalculatorLifeApp
import com.calculatorlife.app.data.VaultFileManager
import com.calculatorlife.app.data.VaultSecurity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val security: VaultSecurity = (application as CalculatorLifeApp).vaultSecurity
    val fileManager = VaultFileManager(application)
    val vaultDao = (application as CalculatorLifeApp).database.vaultDao()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    fun isPinSet(): Boolean = security.isPinSet()
    fun isBiometricEnabled(): Boolean = security.isBiometricEnabled()

    fun createPin(pin: String) {
        security.setPin(pin)
        _isUnlocked.value = true
    }

    /** Returns true and unlocks on a correct PIN; false (without unlocking) otherwise. */
    fun verifyPin(pin: String): Boolean {
        val correct = security.verifyPin(pin)
        if (correct) _isUnlocked.value = true
        return correct
    }

    /** Used by the Standard Calculator's hidden PIN-unlock gesture — same check, same effect as [verifyPin]. */
    fun tryUnlockFromCalculator(typedValue: String): Boolean = verifyPin(typedValue)

    fun unlockViaBiometric() {
        _isUnlocked.value = true
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (!security.verifyPin(oldPin)) return false
        security.setPin(newPin)
        return true
    }

    fun setBiometricEnabled(enabled: Boolean) = security.setBiometricEnabled(enabled)

    /** Called whenever a Vault screen leaves composition — always re-locks, no exceptions. */
    fun lock() {
        _isUnlocked.value = false
        viewModelScope.launch { fileManager.clearPlaybackCache() }
    }
}
