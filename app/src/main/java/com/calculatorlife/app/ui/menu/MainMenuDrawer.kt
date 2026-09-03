package com.calculatorlife.app.ui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.calculatorlife.app.R
import com.calculatorlife.app.ui.navigation.CalculatorCatalog
import com.calculatorlife.app.ui.navigation.CalculatorCategory
import com.calculatorlife.app.ui.navigation.CalculatorMenuItem
import com.calculatorlife.app.ui.navigation.Screen

/**
 * The ☰ menu: every calculator listed directly under its category, no
 * nested submenu, plus Favorites / History / Private Vault / Settings.
 * Items not yet built (implemented = false) render disabled rather than
 * navigating to a fake screen. Each implemented row has a star toggle so
 * any calculator can be favorited straight from here.
 */
@Composable
fun MainMenuDrawer(
    currentRoute: String?,
    favoriteRoutes: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onCalculatorSelected: (Screen) -> Unit,
    onFavoritesSelected: () -> Unit,
    onHistorySelected: () -> Unit,
    onVaultSelected: () -> Unit,
    onSettingsSelected: () -> Unit
) {
    ModalDrawerSheet {
        LazyColumn(contentPadding = PaddingValues(vertical = 12.dp)) {
            item {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
                HorizontalDivider()
            }

            item { CategoryHeader(stringResource(R.string.menu_basic)) }
            items(CalculatorCatalog.items.filter { it.category == CalculatorCategory.BASIC }) { item ->
                CalculatorRow(item, currentRoute, item.screen.route in favoriteRoutes, onToggleFavorite, onCalculatorSelected)
            }

            item { CategoryHeader(stringResource(R.string.menu_finance)) }
            items(CalculatorCatalog.items.filter { it.category == CalculatorCategory.FINANCE }) { item ->
                CalculatorRow(item, currentRoute, item.screen.route in favoriteRoutes, onToggleFavorite, onCalculatorSelected)
            }

            item { CategoryHeader(stringResource(R.string.menu_business)) }
            items(CalculatorCatalog.items.filter { it.category == CalculatorCategory.BUSINESS }) { item ->
                CalculatorRow(item, currentRoute, item.screen.route in favoriteRoutes, onToggleFavorite, onCalculatorSelected)
            }

            item {
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_favorites)) },
                    icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                    selected = currentRoute == Screen.Favorites.route,
                    onClick = onFavoritesSelected,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_history)) },
                    icon = { Icon(Icons.Filled.History, contentDescription = null) },
                    selected = currentRoute == Screen.History.route,
                    onClick = onHistorySelected,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_vault)) },
                    icon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    selected = currentRoute == Screen.PrivateVault.route,
                    onClick = onVaultSelected,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_settings)) },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = onSettingsSelected,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String) {
    Column(modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun CalculatorRow(
    item: CalculatorMenuItem,
    currentRoute: String?,
    isFavorite: Boolean,
    onToggleFavorite: (String) -> Unit,
    onCalculatorSelected: (Screen) -> Unit
) {
    NavigationDrawerItem(
        label = { Text(stringResource(item.titleRes)) },
        icon = { Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null) },
        selected = currentRoute == item.screen.route,
        colors = NavigationDrawerItemDefaults.colors(),
        onClick = { if (item.implemented) onCalculatorSelected(item.screen) },
        modifier = Modifier.padding(horizontal = 12.dp),
        badge = {
            if (item.implemented) {
                IconButton(onClick = { onToggleFavorite(item.screen.route) }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null,
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(text = "•", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}
