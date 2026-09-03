package com.calculatorlife.app.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calculatorlife.app.CalculatorLifeApp
import com.calculatorlife.app.data.HistoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as CalculatorLifeApp).database.historyDao()

    val entries: StateFlow<List<HistoryEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(id: Long) {
        viewModelScope.launch { dao.deleteById(id) }
    }

    fun clearAll() {
        viewModelScope.launch { dao.clearAll() }
    }
}
