package com.calculatorlife.app.ui.vault

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/** True only when the device actually has usable biometric enrollment right now. */
fun isBiometricAvailable(activity: FragmentActivity): Boolean {
    val manager = BiometricManager.from(activity)
    return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
        BiometricManager.BIOMETRIC_SUCCESS
}

fun showBiometricPrompt(
    activity: FragmentActivity,
    title: String,
    subtitle: String,
    negativeButtonText: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            onError(errString.toString())
        }
    }
    val prompt = BiometricPrompt(activity, executor, callback)
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setNegativeButtonText(negativeButtonText)
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .build()
    prompt.authenticate(promptInfo)
}
