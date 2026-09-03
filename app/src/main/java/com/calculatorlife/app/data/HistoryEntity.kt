package com.calculatorlife.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One saved calculation. [calculatorTitle] is stored as plain text (not a
 * string-resource id) because history is a record of what happened, not a
 * live UI reference — it should still read correctly even if display
 * strings change later, and it needs to survive a language switch as the
 * language it was recorded in.
 */
@Entity(tableName = "history_entries")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calculatorTitle: String,
    val resultSummary: String,
    val detailSummary: String?,
    val timestampMillis: Long
)
