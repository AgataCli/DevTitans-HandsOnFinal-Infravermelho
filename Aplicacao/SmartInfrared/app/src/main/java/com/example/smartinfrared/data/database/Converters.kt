package com.example.smartinfrared.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListToString(list: List<Int>): String {
        return list.joinToString(separator = ",")
    }

    @TypeConverter
    fun fromStringToList(data: String): List<Int> {
        return if (data.isEmpty()) {
            emptyList()
        } else {
            data.split(",").map { it.toInt() }
        }
    }
}
