package com.calculatorlife.app.data

import android.content.Context
import com.calculatorlife.app.CalculatorLifeApp

object HistoryRecorder {
    suspend fun record(context: Context, calculatorTitle: String, resultSummary: String, detailSummary: String?) {
        val app = context.applicationContext as? CalculatorLifeApp ?: return
        app.database.historyDao().insert(
            HistoryEntity(
                calculatorTitle = calculatorTitle,
                resultSummary = resultSummary,
                detailSummary = detailSummary,
                timestampMillis = System.currentTimeMillis()
            )
        )
    }
}
