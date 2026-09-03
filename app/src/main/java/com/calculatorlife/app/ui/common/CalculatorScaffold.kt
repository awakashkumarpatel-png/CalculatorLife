package com.calculatorlife.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import com.calculatorlife.app.R

/**
 * The current calculator's display title, made available to any composable
 * further down the tree — specifically [ResultCard], which uses it to
 * label history entries without every one of the 32 calculator screens
 * needing to thread a title through to it explicitly.
 */
val LocalCalculatorTitle = staticCompositionLocalOf { "" }

/**
 * Lets the ☰-menu-level History screen be opened from any calculator's top
 * bar without CalculatorScaffold needing a NavController — the NavHost
 * provides the real implementation once at the root; screens that render
 * outside the NavHost (there are none today) simply get a no-op.
 */
val LocalHistoryNavigator = staticCompositionLocalOf<() -> Unit> { {} }

/**
 * Every calculator screen shares this shell: a title, the ☰ menu icon, and
 * a history icon that opens the shared History screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScaffold(
    title: String,
    onOpenMenu: () -> Unit,
    content: @Composable (paddingValues: androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val openHistory = LocalHistoryNavigator.current

    CompositionLocalProvider(LocalCalculatorTitle provides title) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onOpenMenu) {
                            Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.action_menu))
                        }
                    },
                    actions = {
                        IconButton(onClick = openHistory) {
                            Icon(Icons.Filled.History, contentDescription = stringResource(R.string.action_history))
                        }
                    }
                )
            }
        ) { padding -> content(padding) }
    }
}
