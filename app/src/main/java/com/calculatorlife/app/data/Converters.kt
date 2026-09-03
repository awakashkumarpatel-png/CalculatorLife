package com.calculatorlife.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromVaultMediaType(type: VaultMediaType): String = type.name

    @TypeConverter
    fun toVaultMediaType(value: String): VaultMediaType = VaultMediaType.valueOf(value)
}
