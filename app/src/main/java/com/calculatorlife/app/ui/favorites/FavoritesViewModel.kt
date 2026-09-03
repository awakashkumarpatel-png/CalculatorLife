package com.calculatorlife.app.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calculatorlife.app.CalculatorLifeApp
import com.calculatorlife.app.data.FavoriteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as CalculatorLifeApp).database.favoriteDao()

    /** Routes of every favorited calculator — the single source of truth used by both
     * the drawer (star icons) and the Favorites screen, so they can never disagree. */
    val favoriteRoutes: StateFlow<Set<String>> = dao.observeAll()
        .map { list -> list.map { it.route }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggle(route: String) {
        viewModelScope.launch {
            if (favoriteRoutes.value.contains(route)) {
                dao.deleteByRoute(route)
            } else {
                dao.insert(FavoriteEntity(route = route, addedAtMillis = System.currentTimeMillis()))
            }
        }
    }
}
