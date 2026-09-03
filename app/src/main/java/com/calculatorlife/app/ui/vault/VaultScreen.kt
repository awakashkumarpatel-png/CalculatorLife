package com.calculatorlife.app.ui.vault

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * The single Vault destination in the nav graph. Which screen renders is
 * driven entirely by [VaultViewModel.isUnlocked] — there's no separate
 * "locked"/"unlocked" route, so there's no way to deep-link or back-button
 * around the PIN check.
 */
@Composable
fun VaultScreen(onOpenMenu: () -> Unit, viewModel: VaultViewModel = viewModel()) {
    val isUnlocked by viewModel.isUnlocked.collectAsState()
    if (isUnlocked) {
        VaultHomeScreen(onOpenMenu = onOpenMenu, viewModel = viewModel)
    } else {
        VaultUnlockScreen(onOpenMenu = onOpenMenu, onUnlocked = {}, viewModel = viewModel)
    }
}
