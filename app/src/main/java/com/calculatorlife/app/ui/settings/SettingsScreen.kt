package com.calculatorlife.app.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculatorlife.app.R
import com.calculatorlife.app.data.LanguageMode
import com.calculatorlife.app.data.ThemeMode
import com.calculatorlife.app.ui.common.ModeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenMenu: () -> Unit,
    onOpenVault: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val languageMode by viewModel.languageMode.collectAsState()
    val decimalPlaces by viewModel.decimalPlaces.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_settings)) },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.action_menu))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSection(title = stringResource(R.string.settings_theme)) {
                    val themeOptions = ThemeMode.entries
                    ModeSelector(
                        options = listOf(
                            stringResource(R.string.settings_theme_system),
                            stringResource(R.string.settings_theme_light),
                            stringResource(R.string.settings_theme_dark)
                        ),
                        selectedIndex = themeOptions.indexOf(themeMode),
                        onSelect = { viewModel.setThemeMode(themeOptions[it]) }
                    )
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_language)) {
                    val languageOptions = LanguageMode.entries
                    ModeSelector(
                        options = listOf(
                            stringResource(R.string.settings_language_system),
                            stringResource(R.string.settings_language_english),
                            stringResource(R.string.settings_language_hindi)
                        ),
                        selectedIndex = languageOptions.indexOf(languageMode),
                        onSelect = { index ->
                            val mode = languageOptions[index]
                            viewModel.setLanguageMode(mode)
                            val locales = when (mode) {
                                LanguageMode.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
                                LanguageMode.ENGLISH -> LocaleListCompat.forLanguageTags("en")
                                LanguageMode.HINDI -> LocaleListCompat.forLanguageTags("hi")
                            }
                            AppCompatDelegate.setApplicationLocales(locales)
                        }
                    )
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_calculator_prefs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.settings_decimal_places))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { if (decimalPlaces > 0) viewModel.setDecimalPlaces(decimalPlaces - 1) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) { Text("−") }
                            Text(decimalPlaces.toString(), style = MaterialTheme.typography.titleMedium)
                            OutlinedButton(
                                onClick = { if (decimalPlaces < 6) viewModel.setDecimalPlaces(decimalPlaces + 1) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) { Text("+") }
                        }
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_history)) {
                    OutlinedButton(onClick = { showClearHistoryConfirm = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_history_clear))
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_vault_security)) {
                    if (viewModel.isVaultPinSet()) {
                        var biometricEnabled by remember { mutableStateOf(viewModel.isVaultBiometricEnabled()) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.settings_biometric))
                            Switch(
                                checked = biometricEnabled,
                                onCheckedChange = {
                                    biometricEnabled = it
                                    viewModel.setVaultBiometricEnabled(it)
                                }
                            )
                        }
                        Text(
                            stringResource(R.string.settings_change_pin_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        OutlinedButton(onClick = onOpenVault, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text(stringResource(R.string.settings_open))
                        }
                    } else {
                        Text(
                            stringResource(R.string.settings_biometric_no_pin),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(onClick = onOpenVault, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text(stringResource(R.string.settings_open))
                        }
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_privacy)) {
                    OutlinedButton(onClick = { showPrivacyDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_open))
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_about)) {
                    OutlinedButton(onClick = { showAboutDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.settings_open))
                    }
                }
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.settings_privacy)) },
            text = { Text(stringResource(R.string.settings_privacy_body)) },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text(stringResource(R.string.action_confirm)) }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.settings_about)) },
            text = { Text(stringResource(R.string.settings_about_body)) },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text(stringResource(R.string.action_confirm)) }
            }
        )
    }

    if (showClearHistoryConfirm) {
        AlertDialog(
            onDismissRequest = { showClearHistoryConfirm = false },
            title = { Text(stringResource(R.string.history_clear_confirm_title)) },
            text = { Text(stringResource(R.string.history_clear_confirm_body)) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAllHistory(); showClearHistoryConfirm = false }) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryConfirm = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}
