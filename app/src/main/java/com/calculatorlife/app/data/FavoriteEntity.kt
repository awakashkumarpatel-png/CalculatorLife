package com.calculatorlife.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_calculators")
data class FavoriteEntity(
    @PrimaryKey val route: String,
    val addedAtMillis: Long
)
