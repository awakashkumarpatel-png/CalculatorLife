package com.calculatorlife.app

import android.app.Application
import com.calculatorlife.app.data.AppDatabase
import com.calculatorlife.app.data.VaultSecurity

/**
 * App-level Application class. Holds the single Room database instance and
 * the Vault's PIN/biometric-preference store — a hand-rolled service
 * locator rather than a DI framework, since this is a single-module app.
 */
class CalculatorLifeApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val vaultSecurity: VaultSecurity by lazy { VaultSecurity(this) }
}
