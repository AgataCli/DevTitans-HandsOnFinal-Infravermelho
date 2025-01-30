package com.example.smartinfrared.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "controls")
data class ControlEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)