package com.calculatorlife.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.calculatorlife.app.data.SettingsRepository
import com.calculatorlife.app.data.ThemeMode
import com.calculatorlife.app.ui.navigation.CalculatorLifeNavHost
import com.calculatorlife.app.ui.theme.CalculatorLifeTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * Single activity for the whole app. Extends AppCompatActivity (which
 * itself extends FragmentActivity) so both per-app language switching
 * (AppCompatDelegate.setApplicationLocales, used by Settings) and the
 * Vault's BiometricPrompt (which needs a FragmentActivity) work from the
 * same activity.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settingsRepository = SettingsRepository(applicationContext)
        setContent {
            val themeMode by settingsRepository.themeMode
                .stateIn(lifecycleScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)
                .collectAsState()

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            CalculatorLifeTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorLifeNavHost()
                }
            }
        }
    }
}
