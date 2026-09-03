package com.calculatorlife.app.ui.vault

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculatorlife.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultUnlockScreen(
    onOpenMenu: () -> Unit,
    onUnlocked: () -> Unit,
    viewModel: VaultViewModel = viewModel()
) {
    val activity = LocalContext.current as? FragmentActivity
    val pinAlreadySet = remember { viewModel.isPinSet() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.vault_title)) },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.action_menu))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (pinAlreadySet) {
                UnlockExistingPin(viewModel, activity, onUnlocked)
            } else {
                CreatePin(viewModel, onUnlocked)
            }
        }
    }
}

@Composable
private fun CreatePin(viewModel: VaultViewModel, onUnlocked: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val tooShortMessage = stringResource(R.string.vault_pin_too_short)
    val mismatchMessage = stringResource(R.string.vault_pin_mismatch)

    Text(stringResource(R.string.vault_create_pin_title), style = MaterialTheme.typography.titleLarge)
    Text(
        stringResource(R.string.vault_create_pin_body),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
    )

    OutlinedTextField(
        value = pin,
        onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) pin = it },
        label = { Text(stringResource(R.string.vault_enter_pin)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = confirmPin,
        onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) confirmPin = it },
        label = { Text(stringResource(R.string.vault_confirm_pin)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
    )

    error?.let {
        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
    }

    Button(
        onClick = {
            when {
                pin.length < 4 -> error = tooShortMessage
                pin != confirmPin -> error = mismatchMessage
                else -> {
                    viewModel.createPin(pin)
                    onUnlocked()
                }
            }
        },
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Text(stringResource(R.string.vault_create_button))
    }
}

@Composable
private fun UnlockExistingPin(viewModel: VaultViewModel, activity: FragmentActivity?, onUnlocked: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val wrongPinMessage = stringResource(R.string.vault_wrong_pin)
    val biometricTitle = stringResource(R.string.vault_biometric_title)
    val biometricSubtitle = stringResource(R.string.vault_biometric_subtitle)
    val cancelText = stringResource(R.string.action_cancel)
    val onUnlockedState = rememberUpdatedState(onUnlocked)

    val canUseBiometric = activity != null && viewModel.isBiometricEnabled() && isBiometricAvailable(activity)

    Text(stringResource(R.string.vault_unlock_title), style = MaterialTheme.typography.titleLarge)

    OutlinedTextField(
        value = pin,
        onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) { pin = it; error = null } },
        label = { Text(stringResource(R.string.vault_enter_pin)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
    )

    error?.let {
        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
    }

    Button(
        onClick = {
            if (viewModel.verifyPin(pin)) onUnlockedState.value() else error = wrongPinMessage
        },
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Text(stringResource(R.string.vault_unlock_button))
    }

    if (canUseBiometric) {
        OutlinedButton(
            onClick = {
                showBiometricPrompt(
                    activity = activity!!,
                    title = biometricTitle,
                    subtitle = biometricSubtitle,
                    negativeButtonText = cancelText,
                    onSuccess = { viewModel.unlockViaBiometric(); onUnlockedState.value() },
                    onError = { }
                )
            },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Icon(Icons.Filled.Fingerprint, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text(stringResource(R.string.vault_use_biometric))
        }
    }
}
